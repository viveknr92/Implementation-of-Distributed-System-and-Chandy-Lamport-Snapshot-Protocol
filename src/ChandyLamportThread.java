
//Thread to start chandy lamport protocol
public class ChandyLamportThread extends Thread{

	ProjectMain mainObj;
	public ChandyLamportThread(ProjectMain mainObj){
		this.mainObj = mainObj;
	}
	public void run(){
		//If its the first time calling chandy Lamport protocol, start immediately
		if(mainObj.firstTime){
			mainObj.firstTime = false;
		}
		//If its not first time , start after the snapShot delay
		else{
			try {
				Thread.sleep(mainObj.snapshotDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Irrespective of first or second time we start the protocol if this thread is started
		ChandyLamport.startSnapshotProtocol(mainObj);
	}
}
