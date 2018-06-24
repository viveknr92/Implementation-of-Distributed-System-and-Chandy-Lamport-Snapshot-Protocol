
//Thread to start CLProtocol
public class ChandyLamportThread extends Thread{

	MapProtocol mapObject;
	public ChandyLamportThread(MapProtocol mapObject){
		this.mapObject = mapObject;
	}
	public void run(){
		
		if(mapObject.firstTime){
			mapObject.firstTime = false;
		}
		
		else{
			try {
				Thread.sleep(mapObject.snapshotDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ChandyLamport.startSnapshotProtocol(mapObject);
	}
}
