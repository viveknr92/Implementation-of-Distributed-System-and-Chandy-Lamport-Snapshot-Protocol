import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

//Server reading objects sent by other clients in the system in a thread 
public class ReceiveThread extends Thread {
	Socket cSocket;
	MapProtocol mapObject;

	public ReceiveThread(Socket csocket,MapProtocol mapObject) {
		this.cSocket = csocket;
		this.mapObject = mapObject;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(cSocket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			try {
				Message msg;
				msg = (Message) ois.readObject();
				// Synchronizing mapObject so that multiple threads access mapObject in a synchronized way
				synchronized(mapObject){

					//If message is a marker message then process has to turn red if its blue and send messages along all its
					//channels
					if(msg instanceof MarkerMsg){
						int channelNo = ((MarkerMsg) msg).nodeId;
						ChandyLamport.sendMarkerMessage(mapObject,channelNo);
					}	

					//A passive process on receiving an application message only becomes active if 
					//it has sent fewer than maxNumber messages
					else if((mapObject.active == false) && msg instanceof ApplicationMsg && 
							mapObject.totalMessagesSent < mapObject.maxNumber && mapObject.logging == 0){
						mapObject.active = true; 
						new SendMessageThread(mapObject).start();
					}
					//If its an application message and logging = 1 then save it
					else if((mapObject.active == false) && (msg instanceof ApplicationMsg) && (mapObject.logging == 1)){
						//Save the channel No from where the message came from
						int channelNo = ((ApplicationMsg) msg).nodeId;
						//Log the application message since logging is enabled
						ChandyLamport.logMessage(channelNo,((ApplicationMsg) msg) ,mapObject);
					}

					//If message is a state message then if this node id is 0 then process it 
					// otherwise forward it to the parent on converge cast tree towards Node 0
					else if(msg instanceof StateMsg){
						if(mapObject.id == 0){
							//System.out.println("Received State msg at Node 0 from node "+((StateMsg)msg).nodeId);
							mapObject.stateMessages.put(((StateMsg)msg).nodeId,((StateMsg)msg));
							mapObject.nodesInGraph[((StateMsg) msg).nodeId] = true;
							//System.out.println("statemessages size = "+mapObject.stateMessages.size());
							if(mapObject.stateMessages.size() == mapObject.numOfNodes){
								//System.out.println("State messages are received at node 0");
								boolean restartChandy = ChandyLamport.processStateMessages(mapObject,((StateMsg)msg));
								if(restartChandy){
									//System.out.println("Restarting Chandy Lamport Protocol");
									mapObject.initialize(mapObject);
									//									for(ArrayList<ApplicationMsg> a:mapObject.channelStates.values()){
									//										System.out.println("Checking if mapObject has empty channel state:"+a.isEmpty());
									//									}
									//Call Chandy Lamport protocol 
									new ChandyLamportThread(mapObject).start();	
								}								
							}
						}
						else{
							//System.out.println("Forwarding state msg to my parent - node"+mapObject.id);
							ChandyLamport.forwardToParent(mapObject,((StateMsg)msg));
						}
					}
					//If a finishMsg is received then forward the message to all its neighbors
					else if(msg instanceof FinishMsg){	
						//System.out.println("Finish Message of Node "+mapObject.id+" finish message is"+((FinishMsg)msg).msg);
						ChandyLamport.sendFinishMsg(mapObject);
					}

					if(msg instanceof ApplicationMsg){
						//						System.out.println("TimeStamp when application message is received and not processed at node "+mapObject.id);
						//						for(int j: ((ApplicationMsg) msg).vector){
						//							System.out.println(j+" ");
						//						}
						//Code for vector protocol
						for(int i=0;i<mapObject.numOfNodes;i++){
							mapObject.vector[i] = Math.max(mapObject.vector[i], ((ApplicationMsg) msg).vector[i]);
						}
						mapObject.vector[mapObject.id]++;
						// print the vector 
						//						System.out.println("vector of node id "+mapObject.id+" when appln msg is received and processed");
						//						for(int i:mapObject.vector){
						//							System.out.println(i);
						//						}
					}
				}
			}
			catch(StreamCorruptedException e) {
				e.printStackTrace();
				System.exit(2);
			}
			catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
}
