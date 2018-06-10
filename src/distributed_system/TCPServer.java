package distributed_system; //this is a package

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer implements Runnable
{
	public ServerSocket ss;
	public Node source;
	public ArrayList<ServerConnections> connections;
	boolean shouldRun;
	
	//Constructor method
	public TCPServer(Node source) {
		this.ss = null;
		this.source = source;
		this.connections = new ArrayList<ServerConnections>();
		this.shouldRun = true;
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(source.port);
			System.out.println(Thread.currentThread().getName() + " : server socket created on node : " + source.nodeId);
			//Thread.sleep(2000);
			// TODO Auto-generated method stub
			while(shouldRun) {
				Socket s = null;
				try {
					s = ss.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " : Waiting for client"); //Blocks until connection request is received from client
				ServerConnections sc = new ServerConnections(s, this); //'this' is current obj of TCPServer
				Thread thread = new Thread(sc, "server connections");
				thread.start(); //Start thread execution, calls run() method
				System.out.println(Thread.currentThread().getName() + " : Connected to client");
				connections.add(sc); //Add connection to Array List
				System.out.println(Thread.currentThread().getName() + " : number of clients connected : " + connections.size());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
}