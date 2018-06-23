import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

//Server reading objects sent by other clients in the system in a thread 
public class ReceiveThread extends Thread {
	Socket cSocket;
	ProjectMain mainObj;

	public ReceiveThread(Socket csocket,ProjectMain mainObj) {
		this.cSocket = csocket;
		this.mainObj = mainObj;
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
				// Synchronizing mainObj so that multiple threads access mainObj in a synchronized way
				synchronized(mainObj){

					//If message is a marker message then process has to turn red if its blue and send messages along all its
					//channels
					if(msg instanceof MarkerMsg){
						int channelNo = ((MarkerMsg) msg).nodeId;
						ChandyLamport.sendMarkerMessage(mainObj,channelNo);
					}	

					//A passive process on receiving an application message only becomes active if 
					//it has sent fewer than maxNumber messages
					else if((mainObj.active == false) && msg instanceof ApplicationMsg && 
							mainObj.totalMessagesSent < mainObj.maxNumber && mainObj.logging == 0){
						mainObj.active = true; 
						new SendMessageThread(mainObj).start();
					}
					//If its an application message and logging = 1 then save it
					else if((mainObj.active == false) && (msg instanceof ApplicationMsg) && (mainObj.logging == 1)){
						//Save the channel No from where the message came from
						int channelNo = ((ApplicationMsg) msg).nodeId;
						//Log the application message since logging is enabled
						ChandyLamport.logMessage(channelNo,((ApplicationMsg) msg) ,mainObj);
					}

					//If message is a state message then if this node id is 0 then process it 
					// otherwise forward it to the parent on converge cast tree towards Node 0
					else if(msg instanceof StateMsg){
						if(mainObj.id == 0){
							//System.out.println("Received State msg at Node 0 from node "+((StateMsg)msg).nodeId);
							mainObj.stateMessages.put(((StateMsg)msg).nodeId,((StateMsg)msg));
							mainObj.nodesInGraph[((StateMsg) msg).nodeId] = true;
							//System.out.println("statemessages size = "+mainObj.stateMessages.size());
							if(mainObj.stateMessages.size() == mainObj.numOfNodes){
								//System.out.println("State messages are received at node 0");
								boolean restartChandy = ChandyLamport.processStateMessages(mainObj,((StateMsg)msg));
								if(restartChandy){
									//System.out.println("Restarting Chandy Lamport Protocol");
									mainObj.initialize(mainObj);
									//									for(ArrayList<ApplicationMsg> a:mainObj.channelStates.values()){
									//										System.out.println("Checking if mainObj has empty channel state:"+a.isEmpty());
									//									}
									//Call Chandy Lamport protocol 
									new ChandyLamportThread(mainObj).start();	
								}								
							}
						}
						else{
							//System.out.println("Forwarding state msg to my parent - node"+mainObj.id);
							ChandyLamport.forwardToParent(mainObj,((StateMsg)msg));
						}
					}
					//If a finishMsg is received then forward the message to all its neighbors
					else if(msg instanceof FinishMsg){	
						//System.out.println("Finish Message of Node "+mainObj.id+" finish message is"+((FinishMsg)msg).msg);
						ChandyLamport.sendFinishMsg(mainObj);
					}

					if(msg instanceof ApplicationMsg){
						//						System.out.println("TimeStamp when application message is received and not processed at node "+mainObj.id);
						//						for(int j: ((ApplicationMsg) msg).vector){
						//							System.out.println(j+" ");
						//						}
						//Code for vector protocol
						for(int i=0;i<mainObj.numOfNodes;i++){
							mainObj.vector[i] = Math.max(mainObj.vector[i], ((ApplicationMsg) msg).vector[i]);
						}
						mainObj.vector[mainObj.id]++;
						// print the vector 
						//						System.out.println("vector of node id "+mainObj.id+" when appln msg is received and processed");
						//						for(int i:mainObj.vector){
						//							System.out.println(i);
						//						}
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
