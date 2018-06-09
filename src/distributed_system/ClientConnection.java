package distributed_system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class ClientConnection extends Thread {

	Socket s;
	ObjectInputStream din;
	ObjectOutputStream dout;
	//DataInputStream din;
	//DataOutputStream dout;
	boolean shouldRun = true;
	
	public ClientConnection(Socket socket, TCPClient client) {
		s = socket;		
		 
	}
	
	
	public void sendStringtoServer(AppMessage text) {
		try {
			dout.writeObject(text);	//Write input from user to the server
			//dout.writeUTF(text);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
			close();
		} 
	}
	
	//Thread method 
	//Keeps checking for any reply from server and prints it
	public void run() {
		try {
			//always declare ObjectOutputStream before ObjectInputStream at both client and server
			dout = new ObjectOutputStream(s.getOutputStream()); //Output stream 
			din = new ObjectInputStream(s.getInputStream()); // Input stream
			
			//din = new DataInputStream(s.getInputStream());
			//dout = new DataOutputStream(s.getOutputStream());
			while(shouldRun) {
//					while(din.available()==0) { //Check for any reply from server
//						try {					//If not sleep until get reply
//							Thread.sleep(1);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
					//StreamMessage msg = new AppMessage();
					StreamMessage reply = new StreamMessage();
					reply = (AppMessage) din.readObject(); //If reply from server print it out to console
					if(reply instanceof AppMessage) {
						System.out.println("app msg");
					}
					if(reply instanceof MarkerMessage) {
						System.out.println("marker msg");
					}
					reply.printAppMsg();
					//String reply = din.readUTF();
					//System.out.println(reply);
				} 
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Close all connections
	public void close() {
		try {
			din.close();
			dout.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
