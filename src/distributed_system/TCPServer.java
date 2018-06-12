package distributed_system; //this is a package

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TCPServer implements Runnable
{
	public ServerSocket ss;
	public Socket socket;
	public Node source;
	public BlockingQueue<Socket> connections;
	boolean shouldRun;
	
	//Constructor method
	public TCPServer(BlockingQueue<Socket> connections, Node source) {
		this.ss = null;
		this.source = source;
		this.connections = connections;
		this.shouldRun = true;
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(source.port);
			System.out.println(Thread.currentThread().getName() + " : server socket created on node : " + source.nodeId);
			// TODO Auto-generated method stub
			while(shouldRun) {
				socket = ss.accept();
				System.out.println(Thread.currentThread().getName() + " : Waiting for client"); //Blocks until connection request is received from client
				
				synchronized (connections) {
					connections.put(socket);
					connections.notify();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}