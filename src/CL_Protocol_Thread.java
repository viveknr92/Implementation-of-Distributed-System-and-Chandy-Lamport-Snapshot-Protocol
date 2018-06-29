
//Thread to start chandy lamport protocol
public class CL_Protocol_Thread extends Thread{

	MapProtocol mapObject;
	public CL_Protocol_Thread(MapProtocol mapObject){
		this.mapObject = mapObject;
	}
	public void run(){
		if(mapObject.isFirstSnapshot){
			mapObject.isFirstSnapshot = false;
		}
		else{
			try {
				Thread.sleep(mapObject.snapshotDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		CL_Protocol.beginCLProtocol(mapObject);
	}
}
