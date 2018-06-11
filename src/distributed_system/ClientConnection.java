package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable {

	Socket s;
	ObjectInputStream din;
	ObjectOutputStream dout;
	boolean shouldRun;
	
	public ClientConnection(Socket socket, TCPClient client) {
		s = socket;
		din = null;
		dout = null;
		shouldRun = true;
	}
	public void sendStringtoServer(AppMessage text) throws IOException, InterruptedException {
		System.out.println(Thread.currentThread().getName() + " : sendStringtoServer");
		dout.writeObject(text);	//Write input from user to the server
		dout.flush();
	}
	
	//Thread method 
	//Keeps checking for any reply from server and prints it
	public void run() {
		try {
			//always declare ObjectOutputStream before ObjectInputStream at both client and server
			dout = new ObjectOutputStream(s.getOutputStream()); //Output stream 
			din = new ObjectInputStream(s.getInputStream()); // Input stream
			System.out.println(Thread.currentThread().getName() + " : set IO stream");
			while(shouldRun) {
				StreamMessage reply = new StreamMessage();
				reply = (AppMessage) din.readObject(); //If reply from server print it out to console
				System.out.println(Thread.currentThread().getName() + " : reply from server available");
				reply.printAppMsg();
			}
			close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	
	//Close all connections
	public void close() {
		try {
			s.close();
			din.close();
			dout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
