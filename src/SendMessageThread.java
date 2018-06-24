import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SendMessageThread extends Thread{
	MapProtocol mainObj;
	public SendMessageThread(MapProtocol mainObj) {
		this.mainObj = mainObj;
	}
	void sendMessages() throws InterruptedException{

		// get a random number between minPerActive to maxPerActive to emit that many messages
		int numMsgs = 1;
		int minSendDelay = 0;
		synchronized(mainObj){
			numMsgs = this.getRandomNumber(mainObj.minPerActive,mainObj.maxPerActive);
			// If random number is 0 then since node 0 is the only process active in the beginning it will not start
			// therefore get a bigger random number
			if(numMsgs == 0){
				numMsgs = this.getRandomNumber(mainObj.minPerActive + 1,mainObj.maxPerActive);
			}
			minSendDelay = mainObj.minSendDelay;
		}
		//System.out.println("For Node "+this.id+ "  Random number of messages in range min - max per active is  "+numMsgs);
		// channels hashMap has all neighbors as keys, store them in an array to get random neighbor
		for(int i=0;i<numMsgs;i++){
			synchronized(this){
				//get a random number to index in the neighbors and array and get that neighbor
				int neighborIndex = this.getRandomNumber(0,mainObj.neighbors.size()-1);
				int curNeighbor = mainObj.neighbors.get(neighborIndex);
//				System.out.println("Neighbor chosen is "+curNeighbor);
				if(mainObj.active == true){
					//send application message
					ApplicationMsg m = new ApplicationMsg(); 
					// Code for vector protocol
					mainObj.vector[mainObj.id]++;
					m.vector = new int[mainObj.vector.length];
					System.arraycopy( mainObj.vector, 0, m.vector, 0, mainObj.vector.length );
					m.nodeId = mainObj.id;
					//					System.out.println("Timestamp that is being sent while message is emitted ");
					//					for(int s:m.vector){
					//						System.out.println(s+" ");
					//					}
					// Write the message in the channel connecting to neighbor
					try {
						ObjectOutputStream oos = mainObj.oStream.get(curNeighbor);
						oos.writeObject(m);	
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					//increment totalMessagesSent
					mainObj.totalMessagesSent++;
				}
			}
			// Wait for minimum sending delay before sending another message
			Thread.sleep(minSendDelay);
		}
		synchronized(this){
			// After sending minPerActive to maxPerActive number of messages become passive
			mainObj.active = false;
		}


	}
	public void run(){
		try {
			this.sendMessages();
		} catch (InterruptedException e) {
			System.out.println("Error in EmitMessages");
			e.printStackTrace();
		}
	}
	// Function to generate random number in a given range
	int getRandomNumber(int min,int max){
		// Usually this can be a field rather than a method variable
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
