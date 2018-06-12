package distributed_system;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Graph g = ReadConfigFile.readFile("config.txt");
		BlockingQueue<Socket> socket_queue = new ArrayBlockingQueue<>(10);
		int nodeId = Integer.parseInt(args[0]);
		
		TCPServer tcp0 = new TCPServer(socket_queue, g.nodes.get(nodeId));
		Thread producer = new Thread(tcp0, "Server thread " + nodeId);
		ServerConnections sc = new ServerConnections(socket_queue); //'this' is current obj of TCPServer
		Thread consumer = new Thread(sc, "server connections");
		producer.start();
		consumer.start(); //Start thread execution, calls run() method
		
		Thread.sleep(10000);
//		for (int i = 0; i < 2; i++) {
//			TCPClient client = new TCPClient(g.nodes.get(1), g.nodes.get(0));
//			Thread client_t = new Thread(client, "client ");
//			int[] vector = {0,0};
//			AppMessage appmsg = new AppMessage("appmsg ", g.nodes.get(1).nodeId , vector);
//			client.sendStringtoServer(appmsg);
//			client_t.start();			
//		}
		ArrayList<Integer> neighbor = g.adjList.get(nodeId);
		for(int n : neighbor) {
			TCPClient client = new TCPClient(g.nodes.get(nodeId), g.nodes.get(n));
			Thread client_t = new Thread(client);
			int[] vector = {0,0};
			AppMessage appmsg = new AppMessage("appmsg ", g.nodes.get(n).nodeId , vector);
			client.sendStringtoServer(appmsg);
			client_t.start();
		}
		
		
	}
}
