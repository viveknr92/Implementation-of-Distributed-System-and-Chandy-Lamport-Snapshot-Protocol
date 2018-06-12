package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ServerConnections implements Runnable{
	
	public BlockingQueue<Socket> connections;
	public Socket socket;
	ObjectInputStream din;
	ObjectOutputStream dout;
	boolean shouldRun;
	
	public ServerConnections(BlockingQueue<Socket> connections) {
		this.connections = connections;
		shouldRun = true;
	}
	
	public void sendStringtoClient(AppMessage text) throws IOException { //Send to individual client
		dout.writeObject(text);
		dout.flush();
	}
	
	public void sendStringtoAllClients(AppMessage text) throws IOException, InterruptedException {
		System.out.println("Number of clients : "+ connections.size());
		while (!connections.isEmpty()) { //Send to all clients by checking no of server connections
			sendStringtoClient(text);
			connections.remove();
		}
	}
	
	public void run(){
		try {
			while(shouldRun) {
				if(connections.isEmpty()) {
					synchronized (connections) {
						connections.wait();
					}
				}
				else {
					socket = connections.peek();
					//always declare ObjectOutputStream before ObjectInputStream at both client and server
					dout = new ObjectOutputStream(socket.getOutputStream()); // Output Stream		
					din = new ObjectInputStream(socket.getInputStream()); // Input Stream
					AppMessage textIn = (AppMessage) din.readObject();
					textIn.printAppMsg();
					sendStringtoAllClients(textIn);
				}
			}	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
	public void close() {
		try {
			din.close();
			dout.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
