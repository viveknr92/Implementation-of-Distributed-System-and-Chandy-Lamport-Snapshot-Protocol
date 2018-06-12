package distributed_system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class TCPClient implements Runnable{
	
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendStringtoServer(AppMessage text) throws IOException, InterruptedException {
		dout = new ObjectOutputStream(s.getOutputStream()); //Output stream 
		dout.writeObject(text);	//Write input from user to the server
		dout.flush();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			din = new ObjectInputStream(s.getInputStream());
			StreamMessage reply = new StreamMessage();
			reply = (AppMessage) din.readObject(); //If reply from server print it out to console
			System.out.print(Thread.currentThread().getName() + " : reply from server available : ");
			reply.printAppMsg();
		}
		catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
