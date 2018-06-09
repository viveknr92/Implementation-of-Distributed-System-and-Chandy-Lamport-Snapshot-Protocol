package distributed_system;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnections extends Thread{
	
	Socket socket;
	TCPServer server;
	ObjectInputStream din;
	ObjectOutputStream dout;
	//DataInputStream din;
	//DataOutputStream dout;
	boolean shouldRun = true;
	
	public ServerConnections(Socket socket, TCPServer server) {
		super("ServerConnectionsThread"); //Allocates a new thread named ServerConneectionsThread
		this.socket = socket;
		this.server = server;
		
	}
	
	public void sendStringtoClient(AppMessage text) { //Send to individual client
		try {
			dout.writeObject(text);
			//dout.writeUTF(text);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendStringtoAllClients(AppMessage text) {
		
		for (int i = 0; i < server.connections.size(); i++) { //Send to all clients by checking no of server connections
			ServerConnections sc = server.connections.get(i);
			sc.sendStringtoClient( text );
		}
	}
	
	public void run(){
		try {
			//always declare ObjectOutputStream before ObjectInputStream at both client and server
			dout = new ObjectOutputStream(socket.getOutputStream()); // Output Stream		
			din = new ObjectInputStream(socket.getInputStream()); // Input Stream
			
			//din = new DataInputStream(socket.getInputStream());
			//dout = new DataOutputStream(socket.getOutputStream());
			
			while(shouldRun) {
//				while(din.available() == 0) {	//Wait until any data sent from client
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
				//Read any data sent from client and send 
				//it to all other clients
				//String textIn = din.readUTF();
				AppMessage textIn = (AppMessage) din.readObject();
				textIn.printAppMsg();
				sendStringtoAllClients(textIn);
			}
			
			din.close();
			dout.close();
			socket.close(); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
