package distributed_system;

import java.io.IOException;
import java.net.Socket;


public class TCPClient implements Runnable{
	
	ClientConnection cc;
	Node source;
	Node dest;
	
	public TCPClient(Node source_node, Node dest_node) { //Constructor method
		this.source = source_node;
		this.dest = dest_node;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket s = new Socket(dest.hostName, dest.port); //Blocks until it connects to server
			System.out.println(Thread.currentThread().getName() + " : connected to server : " + dest.nodeId);
			int[] vector = {0,0};
			AppMessage appmsg = new AppMessage("appmsg ", source.nodeId , vector);
			cc = new ClientConnection(s, this);
			Thread thread = new Thread(cc, "client connections thread");
			thread.start(); //Start a new thread, calls run() method
			Thread.sleep(1000);
			cc.sendStringtoServer(appmsg);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
