package distributed_system;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
	
	public int nodeId;
	public Graph g = ReadConfigFile.readFile("config.txt");
	public BlockingQueue<Socket> socket_queue = new ArrayBlockingQueue<>(10);
	private int[] vector;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		Main mainObj = new Main();
		mainObj.nodeId = Integer.parseInt(args[0]);
		TCPServer tcp0 = new TCPServer(mainObj.socket_queue, mainObj.g.nodes.get(mainObj.nodeId));
		Thread producer = new Thread(tcp0, "Server thread " + mainObj.nodeId);
		ServerConnections sc = new ServerConnections(mainObj.socket_queue); //'this' is current obj of TCPServer
		Thread consumer = new Thread(sc, "server connections");
		producer.start();
		consumer.start(); //Start thread execution, calls run() method
		
		Thread.sleep(10000);
//		for (int i = 0; i < 2; i++) {
//			TCPClient client = new TCPClient(g.nodes.get(1), g.nodes.get(0));
//			Thread client_t = new Thread(client, "client ");
//			int[] vector = {0,0};
//			AppMessage appmsg = new AppMessage("appmsg ", g.nodes.get(1).nodeId , vector);
//			client.sendStringtoServer(appmsg);
//			client_t.start();			
//		}		
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

	public void sendMessages() {
		int numMsgs = 1;
		boolean isActive = true;
		numMsgs = this.getRandomNumber(GlobalParameters.minPerActive,GlobalParameters.maxPerActive);
		if(numMsgs == 0){
			numMsgs = this.getRandomNumber(GlobalParameters.minPerActive + 1,GlobalParameters.maxPerActive);
		}
		ArrayList<Integer> neighbor = g.adjList.get(nodeId);   
		for(int n : neighbor) {
			TCPClient client = new TCPClient(g.nodes.get(nodeId), g.nodes.get(n));
			Thread client_t = new Thread(client);
			//mainObj.vector = {0};
			AppMessage appmsg = new AppMessage("appmsg ", g.nodes.get(n).nodeId , vector);
			
			
			for(int i = 0; i < numMsgs; i++) {
				if(isActive ) {
					this.vector[this.nodeId]++;
					System.out.println(vector);
					try {
						client.sendStringtoServer(appmsg);
						client_t.start();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(GlobalParameters.minSendDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			isActive = false;
			
		}
		
	}
	
	
}

class EmitMessagesThread extends Thread{

	Main obj;
	public EmitMessagesThread(Main obj){
		this.obj = obj;
	}
	public void run(){
		obj.sendMessages();
	}
}
