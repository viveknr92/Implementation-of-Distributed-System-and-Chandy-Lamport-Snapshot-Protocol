package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnections implements Runnable{
	
	Socket socket;
	TCPServer server;
	ObjectInputStream din;
	ObjectOutputStream dout;
	boolean shouldRun;
	
	public ServerConnections(Socket socket, TCPServer server) {
		this.socket = socket;
		this.server = server;
		shouldRun = true;
	}
	
	public synchronized void sendStringtoClient(AppMessage text) throws IOException { //Send to individual client
		dout.writeObject(text);
		dout.flush();
	}
	
	public synchronized void sendStringtoAllClients(AppMessage text) throws IOException {
		for (int i = 0; i < server.connections.size(); i++) { //Send to all clients by checking no of server connections
			ServerConnections sc = server.connections.get(i);
			System.out.print(Thread.currentThread().getName() + " : Send to clients ");
			sc.sendStringtoClient( text );
		}
	}
	
	public void run(){
		try {
			//always declare ObjectOutputStream before ObjectInputStream at both client and server
			dout = new ObjectOutputStream(socket.getOutputStream()); // Output Stream		
			din = new ObjectInputStream(socket.getInputStream()); // Input Stream
			
			while(shouldRun) {
				AppMessage textIn = (AppMessage) din.readObject();
				System.out.print(Thread.currentThread().getName() + " : Received ");
				textIn.printAppMsg();
				sendStringtoAllClients(textIn);
			}
			close(); 	
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public void close() throws IOException {
		din.close();
		dout.close();
		socket.close();	
	}
}
