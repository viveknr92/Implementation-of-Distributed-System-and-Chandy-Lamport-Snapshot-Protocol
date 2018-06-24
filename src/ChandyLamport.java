import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ChandyLamport { 
    //method where protocol starts 
	public static void startSnapshotProtocol(MapProtocol mapObject) {
		synchronized(mapObject){
			// node 0 calls this method to initiate chandy and lamport protocol
			//nodesInGraph is array which holds the status of receivedStateMessage from all the nodes in the system
			mapObject.nodesInGraph[mapObject.id] = true;
			//It turns red and sends marker messages to all its outgoing channels
			sendMarkerMessage(mapObject,mapObject.id);
		}
	}

	public static void sendMarkerMessage(MapProtocol mapObject, int channelNo){
		// Node which receives marker message turns red ,becomes passive and sends
		// marker messages to all its outgoing channels , starts logging
		synchronized(mapObject){
			if(mapObject.color == Color.BLUE){
//				System.out.println("Received first Marker message from node and color is blue, "
//						+ "will be changed to red  "+channelNo);
				mapObject.receivedMarker.put(channelNo, true);
				mapObject.color = Color.RED;
				mapObject.myState.active = mapObject.active;
				mapObject.myState.vector = mapObject.vector;
				mapObject.myState.nodeId = mapObject.id;
//				System.out.println("Node "+mapObject.id+" is sending the following timestamp to Node 0");
//				for(ArrayList<ApplicationMsg> a:mapObject.channelStates.values()){
//					System.out.println("******Checking if mapObject has empty channel state:"+a.isEmpty());
//				}
//				for(int k:mapObject.myState.vector){
//					System.out.print(k+" ");
//				}
				int[] vectorCopy = new int[mapObject.myState.vector.length];
				for(int i=0;i<vectorCopy.length;i++){
					vectorCopy[i] = mapObject.myState.vector[i];  //Local Snapshot
				}
//				synchronized(mapObject.output){
				mapObject.output.add(vectorCopy);
//				}
//				new writeToOutputThread(mapObject).start();
				//logging = 1 demands the process to log application messages after it has become red
				mapObject.logging = 1;
				//Send marker messages to all its neighbors
				for(int i : mapObject.neighbors){
					MarkerMsg m = new MarkerMsg();
//					System.out.println("To Node "+i+" process "+mapObject.id+"  is sending marker messages now");
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
//				System.out.println("Received a marker message when the color of process "+mapObject.id+" is red");
				// Record that on this channel a marker message was received
				mapObject.receivedMarker.put(channelNo, true);
				int i=0;
				//Check if this node has received marker messages on all its incoming channels
//				System.out.println("Size of the neighbors list is "+mapObject.neighbors.length);
				while(i<mapObject.neighbors.size() && mapObject.receivedMarker.get(mapObject.neighbors.get(i)) == true){
					//System.out.println("Received Marker msg from neighbor "+mapObject.neighbors[i]);
					i++;
				}
				// If this node has received marker messages from all its incoming channels then 
				// send process state to Node 0
				if(i == mapObject.neighbors.size() && mapObject.id != 0){
					int parent = ConvergeCast.getParent(mapObject.id);				
//					System.out.println("For node "+mapObject.id + ", all neighbours have sent marker messages.");
					// Record the channelState and process State and which node is sending to node 0 as nodeId
					mapObject.myState.channelStates = mapObject.channelStates;
//					for(ArrayList<ApplicationMsg> a:mapObject.channelStates.values()){
//						System.out.println("Checking if mapObject has empty channel state:"+a.isEmpty());
//					}
					mapObject.color = Color.BLUE;
					mapObject.logging = 0;
					// Send channel state to parent 
					ObjectOutputStream oos = mapObject.oStream.get(parent);
//					System.out.println("Sending State Msg  by  "+mapObject.id+" and process state is  "+mapObject.myState.active);
					try {
						oos.writeObject(mapObject.myState);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mapObject.initialize(mapObject);
				}
				if(i == mapObject.neighbors.size() &&  mapObject.id == 0){
//					System.out.println("For node 0, all neighbours have sent marker messages.");
					mapObject.myState.channelStates = mapObject.channelStates;
					mapObject.stateMessages.put(mapObject.id, mapObject.myState);
					mapObject.color = Color.BLUE;
					mapObject.logging = 0;
				}
//				if(i != mapObject.neighbors.length){
//					System.out.println("For node "+mapObject.id + ", neighbor " + mapObject.neighbors[i] 
//							+" has not yet sent a marker message.");
//				}
//				

			}
		}
	}

	// This method is called only by node 0 
	public static boolean processStateMessages(MapProtocol mapObject, StateMsg msg) throws InterruptedException {
		int i=0,j=0,k=0;
		synchronized(mapObject){
			// Check if node 0 has received state message from all the nodes in the graph
			while(i<mapObject.nodesInGraph.length && mapObject.nodesInGraph[i] == true){
				i++;
			}
			//If it has received all the state messages 
			if(i == mapObject.nodesInGraph.length){
				//Go through each state message
				for(j=0;j<mapObject.stateMessages.size();j++){
					// Check if any process is still active , if so then no further check required 
					//wait for snapshot delay and restart snapshot protocol
					if(mapObject.stateMessages.get(j).active == true){
//						System.out.println(" *****************Process is still active ");
						return true;
					}
				}
				//If all processes are passive then j is now equal to numOfNodes 
				if(j == mapObject.numOfNodes){
					//now check for channels 
					for(k=0;k<mapObject.numOfNodes;k++){
						// If any process has non-empty channel,  then wait for snapshot 
						// delay and restart snapshot protocol
						StateMsg value = mapObject.stateMessages.get(k);
						for(ArrayList<ApplicationMsg> g:value.channelStates.values()){
							if(!g.isEmpty()){
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
				if(k == mapObject.numOfNodes){
//					System.out.println("Node 0 is sending finish message since all processes are passive and channels empty");					
					sendFinishMsg(mapObject);
					return false;
				}
			}
		}
		return false;
	}


	//When logging is enabled save all the application messages sent on each channel
	//Array list holds the application messages received on each channel
	public static void logMessage(int channelNo,ApplicationMsg m, MapProtocol mapObject) {
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

	// A process received a state msg on its channel and the process is not Node 0
	// therefore simply forward it over converge cast tree towards Node 0
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
			System.exit(0);
		}
	}
}

