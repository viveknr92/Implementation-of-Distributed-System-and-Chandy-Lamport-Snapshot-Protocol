import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SendMessageThread extends Thread{
	MapProtocol mapObject;
	public SendMessageThread(MapProtocol mapObject) {
		this.mapObject = mapObject;
	}
	void sendMessages() throws InterruptedException{

		// get a random number between minPerActive to maxPerActive to send that many messages
		int randNumMsgs = 1;
		int minSendDelay = 0;
		synchronized(mapObject){
			randNumMsgs = this.getRandomNumber(mapObject.minPerActive,mapObject.maxPerActive);
			// If random number is 0
			if(randNumMsgs == 0){
				randNumMsgs = this.getRandomNumber(mapObject.minPerActive + 1,mapObject.maxPerActive);
			}
			minSendDelay = mapObject.minSendDelay;
		}

		//Send the messages to random neighbors each time and add minSendDelay between each send
		for(int i = 0 ; i < randNumMsgs ; i++){
			synchronized(this){
				//get a random neigbour
				int randNeighbor = this.getRandomNumber(0,mapObject.neighbors.size()-1);
				int curNeighbor = mapObject.neighbors.get(randNeighbor);

				if(mapObject.active == true){
					//send application message
					ApplicationMsg m = new ApplicationMsg(); 
					// Implementing Vector clock protocol
					mapObject.vector[mapObject.id]++;
					m.vector = new int[mapObject.vector.length];
					System.arraycopy( mapObject.vector, 0, m.vector, 0, mapObject.vector.length );
					m.nodeId = mapObject.id;
			
					//Send object data to the neighbor
					try {
						ObjectOutputStream oos = mapObject.oStream.get(curNeighbor);
						oos.writeObject(m);	
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					//increment msgSentCount
					mapObject.msgSentCount++;
				}
			}
			// Wait for minimum sending delay before sending another message
			Thread.sleep(minSendDelay);
		}
		synchronized(this){
			// After sending minPerActive to maxPerActive number of messages node should be passive
			mapObject.active = false;
		}


	}
	public void run(){
		try {
			this.sendMessages();
		} catch (InterruptedException e) {
			System.out.println("Error in Sending Messages");
			e.printStackTrace();
		}
	}
	// Function to generate random number in a given range
	int getRandomNumber(int min,int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
