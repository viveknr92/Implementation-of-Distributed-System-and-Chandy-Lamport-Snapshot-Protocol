import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

//Read object data sent by neighboring clients
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

					//If MarkerMsg send marker messages to all neighboring nodes
					if(msg instanceof MarkerMsg){
						int channelNo = ((MarkerMsg) msg).nodeId;
						ChandyLamport.sendMarkerMsg(mapObject,channelNo);
					}	

					//If ApplicationMsg and node is passive becomes active only if
					//it has sent fewer than maxNumber messages
					else if((mapObject.active == false) && msg instanceof ApplicationMsg && 
							mapObject.msgSentCount < mapObject.maxNumber && mapObject.saveChannelMsg == 0){
						mapObject.active = true; 
						new SendMessageThread(mapObject).start();
					}
					
					//If ApplicationMsg and saveChannelMsg = 1 then save it
					else if((mapObject.active == false) && (msg instanceof ApplicationMsg) && (mapObject.saveChannelMsg == 1)){
						//Save the channel No from where ApplicationMsg was sent
						int channelNo = ((ApplicationMsg) msg).nodeId;
						//Log the application message since saveChannelMsg is enabled
						ChandyLamport.saveChannelMsgs(channelNo,((ApplicationMsg) msg) ,mapObject);
					}

					//If StateMsg then and nodeId is 0 check for termination
					//else forward it to the parent on converge cast tree towards node_0
					else if(msg instanceof StateMsg){
						if(mapObject.id == 0){
							//Message received at node_0 from nodeId
							mapObject.stateMsg.put(((StateMsg)msg).nodeId,((StateMsg)msg));
							mapObject.isRxdStateMsg[((StateMsg) msg).nodeId] = true;
							if(mapObject.stateMsg.size() == mapObject.numOfNodes){
								//Check for termination or take next snapshot
								boolean restartChandy = ChandyLamport.detectTermination(mapObject,((StateMsg)msg));
								if(restartChandy){
									mapObject.initialize(mapObject);
									//Call thread again to take new snapshot
									new ChandyLamportThread(mapObject).start();	
								}								
							}
						}
						else{
							ChandyLamport.sendToParent(mapObject,((StateMsg)msg));
						}
					}
					
					//If finishMsg send to all neighbors
					else if(msg instanceof FinishMsg){	
						ChandyLamport.sendFinishMsg(mapObject);
					}

					if(msg instanceof ApplicationMsg){
						//Implementing vector protocol on receiver side
						for(int i=0;i<mapObject.numOfNodes;i++){
							mapObject.vector[i] = Math.max(mapObject.vector[i], ((ApplicationMsg) msg).vector[i]);
						}
						mapObject.vector[mapObject.id]++;
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
