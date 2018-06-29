
//Thread to start chandy lamport protocol
public class ChandyLamportThread extends Thread{

	MapProtocol mapObject;
	public ChandyLamportThread(MapProtocol mapObject){
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
