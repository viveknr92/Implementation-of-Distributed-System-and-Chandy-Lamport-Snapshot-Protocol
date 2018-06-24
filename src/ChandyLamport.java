import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ChandyLamport { 
  
	public static void startSnapshotProtocol(MapProtocol mapObject) {
		synchronized(mapObject){
			mapObject.nodesInGraph[mapObject.id] = true;
			sendMarkerMessage(mapObject,mapObject.id);
		}
	}

	public static void sendMarkerMessage(MapProtocol mapObject, int channelNo){
		// Node which receives marker message turns red and sends
		// marker messages to all its neighboring channels , starts logging
		synchronized(mapObject){
			if(mapObject.color == Color.BLUE){
				mapObject.receivedMarker.put(channelNo, true);
				mapObject.color = Color.RED;
				mapObject.myState.active = mapObject.active;
				mapObject.myState.vector = mapObject.vector;
				mapObject.myState.nodeId = mapObject.id;
				//Record the vector timestamp when marker msg is received
				//and store it in output Arraylist
				int[] vectorCopy = new int[mapObject.myState.vector.length];
				for(int i=0;i<vectorCopy.length;i++){
					vectorCopy[i] = mapObject.myState.vector[i];  //Local Snapshot
				}
				mapObject.output.add(vectorCopy);

				//logging = 1 demands the process to log application messages after it has become red
				mapObject.logging = 1;
				
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
					mapObject.myState.channelStates = mapObject.channelStates;
					mapObject.color = Color.BLUE;
					mapObject.logging = 0;
					// Send channel state to parent 
					ObjectOutputStream oos = mapObject.oStream.get(parent);
					try {
						oos.writeObject(mapObject.myState);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mapObject.initialize(mapObject);
				}


			}
			//If color of the process is red and a marker message is received on this channel
			else if(mapObject.color == Color.RED){
				// Make note that marker msg was received on this channel
				mapObject.receivedMarker.put(channelNo, true);
				int channel=0;
				
				//Check if this node has received marker messages on all its incoming channels
				while(channel<mapObject.neighbors.size() && mapObject.receivedMarker.get(mapObject.neighbors.get(channel)) == true){
					channel++;
				}
				
				// If this node has received marker messages from all its incoming channels then 
				// send State Msg to node_0
				if(channel == mapObject.neighbors.size() && mapObject.id != 0){
					int parent = ConvergeCast.getParent(mapObject.id);				
					// Record the channelState and StateMsg and which node is sending to node 0 as nodeId
					mapObject.myState.channelStates = mapObject.channelStates;
					mapObject.color = Color.BLUE;
					mapObject.logging = 0;
					ObjectOutputStream oos = mapObject.oStream.get(parent);
					try {
						oos.writeObject(mapObject.myState);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mapObject.initialize(mapObject);
				}
				
				//If node_0 has receives all marker messages restart state of it
				if(channel == mapObject.neighbors.size() &&  mapObject.id == 0){
					mapObject.myState.channelStates = mapObject.channelStates;
					mapObject.stateMessages.put(mapObject.id, mapObject.myState);
					mapObject.color = Color.BLUE;
					mapObject.logging = 0;
				}
			}
		}
	}

	// When node_0 receives state from all nodes
	public static boolean processStateMessages(MapProtocol mapObject, StateMsg msg) throws InterruptedException {
		int channel=0,state=0,node=0;
		synchronized(mapObject){
			// Check if node_0 has received state message from all the nodes 
			while(node < mapObject.nodesInGraph.length && mapObject.nodesInGraph[node] == true){
				node++;
			}
			//If it has received all the state messages 
			if(node == mapObject.nodesInGraph.length){
				//Iterate each state message to check if any process is active
				for(state=0; state < mapObject.stateMessages.size(); state++){
					//If any process is active restart snapshot protocol
					if(mapObject.stateMessages.get(state).active == true){
						return true;
					}
				}
				
				//If all nodes are passive check for channel states
				if(state == mapObject.numOfNodes){
					//Check if any channel is empty or not
					for(channel=0; channel < mapObject.numOfNodes; channel++){
<<<<<<< HEAD
						// If any process has non-empty channel,  then wait for snapshot 
						// delay and restart snapshot protocol
<<<<<<< HEAD
						StateMsg value = mapObject.stateMsg.get(channel);
=======
						//If channel id not empty restart snapshot protocol
						StateMsg value = mapObject.stateMessages.get(channel);
>>>>>>> 370bbc9d5302f4a56b0d479c86db7822b50f355a
=======
						StateMsg value = mapObject.stateMessages.get(channel);
>>>>>>> parent of 55663d9... variable name changes to MapProtocol
						for(ArrayList<ApplicationMsg> g:value.channelStates.values()){
							if(!g.isEmpty()){
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


	//When logging is enabled save all the application messages sent on each channel
	//Array list holds the application messages received on each channel
	public static void logMessage(int channelNo, ApplicationMsg m, MapProtocol mapObject) {
		synchronized(mapObject){
			// if the ArrayList is already there just add this message to it 
			if(!(mapObject.channelStates.get(channelNo).isEmpty()) && mapObject.receivedMarker.get(channelNo) != true){
				mapObject.channelStates.get(channelNo).add(m);
			}
			// or create a list and add the message into it
			else if((mapObject.channelStates.get(channelNo).isEmpty()) && mapObject.receivedMarker.get(channelNo) != true){
				ArrayList<ApplicationMsg> msgs = mapObject.channelStates.get(channelNo);
				msgs.add(m);
				mapObject.channelStates.put(channelNo, msgs);
			}
		}
	}

	// For all nodes other than node_0
	// forward StateMsg to converge cast tree towards node_0
	public static void forwardToParent(MapProtocol mapObject, StateMsg stateMsg) {
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

	//Send Finish msg to all neighbouring nodes
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

