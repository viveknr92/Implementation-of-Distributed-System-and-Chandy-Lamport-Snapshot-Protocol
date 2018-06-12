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
		while (!connections.isEmpty()) { //Send to all clients by checking no of server connections
			System.out.println(Thread.currentThread().getName() + " : Send to clients ");
			sendStringtoClient(text);
			connections.remove();
		}
	}
	
	public void run(){
		try {
			//System.out.println("server connectijs" + socket);
			
			while(shouldRun) {
					if(connections.isEmpty()) {
						synchronized (connections) {
							connections.wait();
						}
					}
					else {
						socket = connections.peek();
						System.out.println(socket);
						//always declare ObjectOutputStream before ObjectInputStream at both client and server
						dout = new ObjectOutputStream(socket.getOutputStream()); // Output Stream		
						din = new ObjectInputStream(socket.getInputStream()); // Input Stream
						AppMessage textIn = (AppMessage) din.readObject();
						System.out.print(Thread.currentThread().getName() + " : Received ");
						textIn.printAppMsg();
						//sendStringtoClient(textIn);
						sendStringtoAllClients(textIn);
						//Thread.sleep(10000);
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
