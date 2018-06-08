package distributed_system;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnections extends Thread{
	
	Socket socket;
	TCPServer server;
	DataInputStream din;
	DataOutputStream dout;
	boolean shouldRun = true;
	
	public ServerConnections(Socket socket, TCPServer server) {
		super("ServerConnectionsThread"); //Allocates a new thread named ServerConneectionsThread
		this.socket = socket;
		this.server = server;
	}
	
	public void sendStringtoClient(String text) { //Send to individual client
		try {
			dout.writeUTF(text);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendStringtoAllClients(String text) {
		
		for (int i = 0; i < server.connections.size(); i++) { //Send to all clients by checking no of server connections
			ServerConnections sc = server.connections.get(i);
			sc.sendStringtoClient( text );
		}
	}
	
	public void run(){
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			
			while(shouldRun) {
				while(din.available() == 0) {	//Wait until any data sent from client
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//Read any data sent from client and send 
				//it to all other clients
				String textIn = din.readUTF();
				sendStringtoAllClients(textIn);
			}
			
			din.close();
			dout.close();
			socket.close(); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
