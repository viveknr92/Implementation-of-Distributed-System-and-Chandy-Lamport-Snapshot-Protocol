
//Thread to start CLProtocol
public class ChandyLamportThread extends Thread{

	MapProtocol mapObject;
	public ChandyLamportThread(MapProtocol mapObject){
		this.mapObject = mapObject;
	}
	public void run(){
<<<<<<< HEAD
		//If its the first time calling chandy Lamport protocol, start immediately
		if(mapObject.isFirstSnapshot){
			mapObject.isFirstSnapshot = false;
=======
		
		if(mapObject.firstTime){
			mapObject.firstTime = false;
>>>>>>> 370bbc9d5302f4a56b0d479c86db7822b50f355a
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
