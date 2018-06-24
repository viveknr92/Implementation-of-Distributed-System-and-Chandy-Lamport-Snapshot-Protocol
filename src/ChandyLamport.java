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
		// marker messages to all its neighboring channels , starts saveChannelMsg
		synchronized(mapObject){
			if(mapObject.color == Color.BLUE){
				mapObject.RxdMarker.put(channelNo, true);
				mapObject.color = Color.RED;
				mapObject.curState.active = mapObject.active;
				mapObject.curState.vector = mapObject.vector;
				mapObject.curState.nodeId = mapObject.id;
				//Record the vector timestamp when marker msg is received
				//and nodeInfo it in globalSnapshots Arraylist
				int[] vectorCopy = new int[mapObject.curState.vector.length];
				for(int i=0;i<vectorCopy.length;i++){
					vectorCopy[i] = mapObject.curState.vector[i];  //Local Snapshot
				}
				mapObject.globalSnapshots.add(vectorCopy);

				//saveChannelMsg = 1 demands the process to log application messages after it has become red
				mapObject.saveChannelMsg = 1;
				
				//Send marker messages to all its neighbors
				for(int i : mapObject.neighbors){
					MarkerMsg m = new MarkerMsg();
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
					// Record the channelState and StateMsg and which node is sending to node 0 as nodeId
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
	public static boolean detectTermination(MapProtocol mapObject, StateMsg msg) throws InterruptedException {
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
						// If any process has non-empty channel,  then wait for snapshot 
						// delay and restart snapshot protocol
						StateMsg value = mapObject.stateMsg.get(channel);
						for(ArrayList<ApplicationMsg> cState : value.channelStates.values()){
							if(!cState.isEmpty()){
//								System.out.println("************** Channels are not empty "+k);
//								for(ApplicationMsg m:g)
//									System.out.println(m.nodeId);
								//If channels are not empty immediately return, restart CL protocol is true
								return true;
							}
						}
					}
				}
				//If the above check has passed then it means all channels are empty and all processes are 
				//passive and now node 0 can announce termination - it can a send finish message to all its neighbors
				if(channel == mapObject.numOfNodes){
//					System.out.println("Node 0 is sending finish message since all processes are passive and channels empty");					
					sendFinishMsg(mapObject);
					return false;
				}
			}
		}
		return false;
	}


	//When saveChannelMsg is enabled save all the application messages sent on each channel
	//Array list holds the application messages received on each channel
	public static void saveChannelMessages(int channelNo,ApplicationMsg m, MapProtocol mapObject) {
		synchronized(mapObject){
			// if the ArrayList is already there just add this message to it 
			if(!(mapObject.channelStates.get(channelNo).isEmpty()) && mapObject.RxdMarker.get(channelNo) != true){
				mapObject.channelStates.get(channelNo).add(m);
			}
			// or create a list and add the message into it
			else if((mapObject.channelStates.get(channelNo).isEmpty()) && mapObject.RxdMarker.get(channelNo) != true){
				ArrayList<ApplicationMsg> msgs = mapObject.channelStates.get(channelNo);
				msgs.add(m);
				mapObject.channelStates.put(channelNo, msgs);
			}
		}
	}

	// A process received a state msg on its channel and the process is not Node 0
	// therefore simply forward it over converge cast tree towards Node 0
	public static void sendToParent(MapProtocol mapObject, StateMsg stateMsg) {
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
			new OutputWriter(mapObject).writeToFile();
			for(int s : mapObject.neighbors){
				FinishMsg m = new FinishMsg();
				ObjectOutputStream oos = mapObject.oStream.get(s);
				try {
					oos.writeObject(m);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(mapObject.id + " - Write to file done");
			System.exit(0);
		}
	}
}

