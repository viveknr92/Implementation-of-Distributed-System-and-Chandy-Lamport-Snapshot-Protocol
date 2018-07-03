import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

//Read object data sent by neighboring clients
public class ReceiveThread extends Thread {
	Socket socket;
	MapProtocol mapObject;

	public ReceiveThread(Socket csocket,MapProtocol mapObject) {
		this.socket = csocket;
		this.mapObject = mapObject;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true){
			try {
				StreamMessage msg;
				msg = (StreamMessage) ois.readObject();
				// Synchronizing mapObject so that multiple threads access mapObject in a synchronized way
				synchronized(mapObject){
					boolean isNotTerminated = true;
					//If MarkerMessage send marker messages to all neighboring nodes
					if(msg instanceof MarkerMessage){
						int channelNo = ((MarkerMessage) msg).nodeId;
						CL_Protocol.sendMarkerMessage(mapObject,channelNo);
					}	

					//If AppMsg and node is passive becomes active only if
					//it has sent fewer than maxNumber messages
					else if((msg instanceof AppMessage) && 
							(mapObject.active == false) && 
							(mapObject.msgSentCount < mapObject.maxNumber) && 
							(mapObject.saveChannelMsg == 0))
					{
						mapObject.active = true; 
						new SendMessageThread(mapObject).start();
					}
					
					//If AppMsg and saveChannelMsg = 1 then save it
					else if((msg instanceof AppMessage) && 
							(mapObject.active == false) && 
							(mapObject.saveChannelMsg == 1))
					{
						//Save the channel No from where AppMsg was sent
						int channelNo = ((AppMessage) msg).nodeId;
						//Log the application message since saveChannelMsg is enabled
						CL_Protocol.saveChannelMessages(channelNo,((AppMessage) msg) ,mapObject);
					}
					
					//If StateMessage then and nodeId is 0 check for termination
					//else forward it to the parent on converge cast tree towards node_0
					else if(msg instanceof StateMessage){
						if(mapObject.id == 0){
							//Message received at node_0 from nodeId
							mapObject.stateMsg.put(((StateMessage)msg).nodeId,((StateMessage)msg));
							mapObject.isRxdStateMsg[((StateMessage) msg).nodeId] = true;
							if(mapObject.stateMsg.size() == mapObject.numOfNodes){
								//Check for termination or take next snapshot
								isNotTerminated = CL_Protocol.detectTermination(mapObject,((StateMessage)msg));
								if(isNotTerminated){
									mapObject.initialize(mapObject);
									//Call thread again to take new snapshot
									new CL_Protocol_Thread(mapObject).start();	
								}								
							}
						}
						else{
							CL_Protocol.sendToParent(mapObject,((StateMessage)msg));
						}
					}
					
					//If finishMsg send to all neighbors
					else if(msg instanceof FinishMessage){	
						CL_Protocol.sendFinishMsg(mapObject);
					}

					if(msg instanceof AppMessage){
						//Implementing vector protocol on receiver side
						for(int i=0;i<mapObject.numOfNodes;i++){
							mapObject.vector[i] = Math.max(mapObject.vector[i], ((AppMessage) msg).vector[i]);
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
