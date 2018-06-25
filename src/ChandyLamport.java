import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ChandyLamport { 
  
	public static void beginCLProtocol(MapProtocol mapObject) {
		synchronized(mapObject){
			mapObject.isRxdStateMsg[mapObject.id] = true;
			sendMarkerMessage(mapObject,mapObject.id);
		}
	}

	public static void sendMarkerMessage(MapProtocol mapObject, int channelNo){
		// Node which receives marker message turns red and sends
		// marker messages to all its neighboring channels.
		// Also save all the incoming application messages
		synchronized(mapObject){
			if(mapObject.color == Color.BLUE){
				mapObject.RxdMarker.put(channelNo, true);
				mapObject.color = Color.RED;
				mapObject.curState.active = mapObject.active;
				mapObject.curState.vector = mapObject.vector;
				mapObject.curState.nodeId = mapObject.id;
				//Record the vector timestamp when marker msg is received
				//and store it in globalSnapshots Arraylist
				int[] vectorCopy = new int[mapObject.curState.vector.length];
				for(int i=0;i<vectorCopy.length;i++){
					vectorCopy[i] = mapObject.curState.vector[i];  //Local Snapshot
				}
				mapObject.globalSnapshots.add(vectorCopy);

				//Save the channel state and application messages after it has become red
				mapObject.saveChannelMsg = 1;
				
				//Send marker messages to all its neighbors
				for(int i : mapObject.neighbors){
					MarkerMessage m = new MarkerMessage();
					m.nodeId = mapObject.id;
					ObjectOutputStream oos = mapObject.oStream.get(i);
					try {
						oos.writeObject(m);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				
				//Edge case when only two nodes are there
				if((mapObject.neighbors.size() == 1) && (mapObject.id!=0)){
					int parent = ConvergeCast.getParent(mapObject.id);	
					mapObject.curState.channelStates = mapObject.channelStates;
					mapObject.color = Color.BLUE;
					mapObject.saveChannelMsg = 0;
					// Send channel state to parent 
					ObjectOutputStream oos = mapObject.oStream.get(parent);
					try {
						oos.writeObject(mapObject.curState);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mapObject.initialize(mapObject);
				}


			}
			//If color of the process is red and a marker message is received on this channel
			else if(mapObject.color == Color.RED){
				// Make note that marker msg was received on this channel
				mapObject.RxdMarker.put(channelNo, true);
				int channel=0;
				//Check if this node has received marker messages on all its incoming channels
				while(channel<mapObject.neighbors.size() && mapObject.RxdMarker.get(mapObject.neighbors.get(channel)) == true){
					channel++;
				}
				
				// If this node has received marker messages from all its incoming channels then 
				// send State Msg to node_0
				if(channel == mapObject.neighbors.size() && mapObject.id != 0){
					int parent = ConvergeCast.getParent(mapObject.id);				
					// Record the channelState and StateMessage and which node is sending to node 0 as nodeId
					mapObject.curState.channelStates = mapObject.channelStates;
					mapObject.color = Color.BLUE;
					mapObject.saveChannelMsg = 0;
					ObjectOutputStream oos = mapObject.oStream.get(parent);
					try {
						oos.writeObject(mapObject.curState);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mapObject.initialize(mapObject);
				}
				
				//If node_0 has receives all marker messages restart state of it
				if(channel == mapObject.neighbors.size() &&  mapObject.id == 0){
					mapObject.curState.channelStates = mapObject.channelStates;
					mapObject.stateMsg.put(mapObject.id, mapObject.curState);
					mapObject.color = Color.BLUE;
					mapObject.saveChannelMsg = 0;
				}
			}
		}
	}

	// When node_0 receives state from all nodes
	public static boolean detectTermination(MapProtocol mapObject, StateMessage msg) throws InterruptedException {
		int channel=0,state=0,node=0;
		synchronized(mapObject){
			// Check if node_0 has received state message from all the nodes 
			while(node < mapObject.isRxdStateMsg.length && mapObject.isRxdStateMsg[node] == true){
				node++;
			}
			//If it has received all the state messages 
			if(node == mapObject.isRxdStateMsg.length){
				//Iterate each state message to check if any process is active
				for(state=0; state < mapObject.stateMsg.size(); state++){
					//If any process is active restart snapshot protocol
					if(mapObject.stateMsg.get(state).active == true){
						return true;
					}
				}
				
				//If all nodes are passive check for channel states
				if(state == mapObject.numOfNodes){
					//Check if any channel is empty or not
					for(channel=0; channel < mapObject.numOfNodes; channel++){
						//If channel id not empty restart snapshot protocol
						StateMessage value = mapObject.stateMsg.get(channel);
						for(ArrayList<AppMessage> cState : value.channelStates.values()){
							if(!cState.isEmpty()){
								return true;
							}
						}
					}
				}

				//If channels are empty and nodes are passive sendFinishMsg for termination
				if(channel == mapObject.numOfNodes){
					sendFinishMsg(mapObject);
					return false;
				}
			}
		}
		return false;
	}


	//When saveChannelMsg is enabled save all the application messages sent on each channel
	//Array list holds the application messages received on each channel
	public static void saveChannelMessages(int channelNo,AppMessage appmsg, MapProtocol mapObject) {
		synchronized(mapObject){ 
			if(mapObject.RxdMarker.get(channelNo) == false) {
				// or create a list and add the message into it
				if((mapObject.channelStates.get(channelNo).isEmpty())){
					ArrayList<AppMessage> msgList = mapObject.channelStates.get(channelNo);
					msgList.add(appmsg);
					mapObject.channelStates.put(channelNo, msgList); // add to Hash map
				}
				// if the ArrayList is already there just add this message to it
				else if(!(mapObject.channelStates.get(channelNo).isEmpty())){
					mapObject.channelStates.get(channelNo).add(appmsg);
				}
			}
		}
	}

	// For all nodes other than node_0
	// forward StateMsg to converge cast tree towards node_0
	public static void sendToParent(MapProtocol mapObject, StateMessage stateMsg) {
		synchronized(mapObject){
			int parent = ConvergeCast.getParent(mapObject.id);
			// Send stateMsg to the parent
			ObjectOutputStream oos = mapObject.oStream.get(parent);
			try {
				oos.writeObject(stateMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Method to send finish message to all the neighbors of the current Node
	public static void sendFinishMsg(MapProtocol mapObject) {
		synchronized(mapObject){
			new OutputWriter(mapObject).storeSnapshotsToFile();
			for(int s : mapObject.neighbors){
				FinishMessage m = new FinishMessage();
				ObjectOutputStream oos = mapObject.oStream.get(s);
				try {
					oos.writeObject(m);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Node : " + mapObject.id + " - Successfully written to output file");
			System.exit(0);
		}
	}
}

