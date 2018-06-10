package distributed_system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;


public class TCPClient {
	
	ClientConnection cc;
	
	public static void main(String[] args) {
		new TCPClient(args[0], Integer.parseInt(args[1])); //Create a constructor
	}
	
	public TCPClient(String address, int port) { //Constructor method
		try {
			Socket s = new Socket(address, port); //Blocks until it connects to server
			cc = new ClientConnection(s, this);
			cc.start(); //Start a new thread, calls run() method
			listenforinput(); //Get input from user and sent it to server
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listenforinput() {

		//while(true) {
			int[] vector = {0,0};
			AppMessage appmsg = new AppMessage("string", 0, vector);
			cc.sendStringtoServer(appmsg);
			
		//}
	}
}
