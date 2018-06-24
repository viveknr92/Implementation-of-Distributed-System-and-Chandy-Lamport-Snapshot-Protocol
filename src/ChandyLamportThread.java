
//Thread to start chandy lamport protocol
public class ChandyLamportThread extends Thread{

	MapProtocol mapObject;
	public ChandyLamportThread(MapProtocol mapObject){
		this.mapObject = mapObject;
	}
	public void run(){
		//If its the first time calling chandy Lamport protocol, start immediately
		if(mapObject.firstTime){
			mapObject.firstTime = false;
		}
		//If its not first time , start after the snapShot delay
		else{
			try {
				Thread.sleep(mapObject.snapshotDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//Irrespective of first or second time we start the protocol if this thread is started
		ChandyLamport.startSnapshotProtocol(mapObject);
	}
}
