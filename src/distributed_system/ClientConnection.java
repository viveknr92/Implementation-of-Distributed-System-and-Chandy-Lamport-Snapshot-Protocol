package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable {

	ObjectInputStream din;
	boolean shouldRun;
	
	public ClientConnection(Socket socket) {
		try {
			din = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Input stream
		shouldRun = true;
	}
	
	//Thread method 
	//Keeps checking for any reply from server and prints it
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName() + " : set IO stream");
			while(shouldRun) {
				StreamMessage reply = new StreamMessage();
				reply = (AppMessage) din.readObject(); //If reply from server print it out to console
				System.out.println(Thread.currentThread().getName() + " : reply from server available");
				reply.printAppMsg();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
