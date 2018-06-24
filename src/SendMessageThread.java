import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class SendMessageThread extends Thread{
	MapProtocol mapObject;
	public SendMessageThread(MapProtocol mapObject) {
		this.mapObject = mapObject;
	}
	void sendMessages() throws InterruptedException{

		// get a random number between minPerActive to maxPerActive to emit that many messages
		int numMsgs = 1;
		int minSendDelay = 0;
		synchronized(mapObject){
			numMsgs = this.getRandomNumber(mapObject.minPerActive,mapObject.maxPerActive);
			// If random number is 0 then since node 0 is the only process active in the beginning it will not start
			// therefore get a bigger random number
			if(numMsgs == 0){
				numMsgs = this.getRandomNumber(mapObject.minPerActive + 1,mapObject.maxPerActive);
			}
			minSendDelay = mapObject.minSendDelay;
		}
		//System.out.println("For Node "+this.id+ "  Random number of messages in range min - max per active is  "+numMsgs);
		// channels hashMap has all neighbors as keys, store them in an array to get random neighbor
		for(int i=0;i<numMsgs;i++){
			synchronized(this){
				//get a random number to index in the neighbors and array and get that neighbor
				int neighborIndex = this.getRandomNumber(0,mapObject.neighbors.size()-1);
				int curNeighbor = mapObject.neighbors.get(neighborIndex);
//				System.out.println("Neighbor chosen is "+curNeighbor);
				if(mapObject.active == true){
					//send application message
					ApplicationMsg m = new ApplicationMsg(); 
					// Code for vector protocol
					mapObject.vector[mapObject.id]++;
					m.vector = new int[mapObject.vector.length];
					System.arraycopy( mapObject.vector, 0, m.vector, 0, mapObject.vector.length );
					m.nodeId = mapObject.id;
					//					System.out.println("Timestamp that is being sent while message is emitted ");
					//					for(int s:m.vector){
					//						System.out.println(s+" ");
					//					}
					// Write the message in the channel connecting to neighbor
					try {
						ObjectOutputStream oos = mapObject.oStream.get(curNeighbor);
						oos.writeObject(m);	
						oos.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}	
					//increment totalMessagesSent
					mapObject.totalMessagesSent++;
				}
			}
			// Wait for minimum sending delay before sending another message
			Thread.sleep(minSendDelay);
		}
		synchronized(this){
			// After sending minPerActive to maxPerActive number of messages become passive
			mapObject.active = false;
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
