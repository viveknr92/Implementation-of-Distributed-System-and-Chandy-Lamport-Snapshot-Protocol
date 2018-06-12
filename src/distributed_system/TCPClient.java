package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class TCPClient implements Runnable{
	
	ClientConnection cc;
	ObjectInputStream din;
	ObjectOutputStream dout;
	Socket s;
	Node source;
	Node dest;
	
	public TCPClient(Node source_node, Node dest_node) { //Constructor method
		this.source = source_node;
		this.dest = dest_node;
		try {
			s = new Socket(dest.hostName, dest.port);
			System.out.println(Thread.currentThread().getName() + " : connected to server : " + dest.nodeId);
			dout = new ObjectOutputStream(s.getOutputStream()); //Output stream 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Blocks until it connects to server
	}
	
	public void sendStringtoServer(AppMessage text) throws IOException, InterruptedException {
		System.out.println(Thread.currentThread().getName() + " : sendStringtoServer");
		dout.writeObject(text);	//Write input from user to the server
		dout.flush();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			cc = new ClientConnection(s);
			Thread thread = new Thread(cc, "Client Connections");
			thread.start(); //Start a new thread, calls run() method
			
	}
}
