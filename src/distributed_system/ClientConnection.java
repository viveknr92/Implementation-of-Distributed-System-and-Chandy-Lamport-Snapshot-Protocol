package distributed_system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



public class ClientConnection extends Thread {

	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	boolean shouldRun = true;
	
	public ClientConnection(Socket socket, TCPClient client) {
		s = socket;		
	}
	
	
	public void sendStringtoServer(String text) {

		try {
			dout.writeUTF(text);	//Write input from user to the server
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
			din = new DataInputStream(s.getInputStream());  //Input stream
			dout = new DataOutputStream(s.getOutputStream()); //Output stream 
			
			while(shouldRun) {
				try {
					while(din.available()==0) { //Check for any reply from server
						try {					//If not sleep until get reply
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String reply = din.readUTF(); //If reply from server print it out to console
					System.out.println(reply);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
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
