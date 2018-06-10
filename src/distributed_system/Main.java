package distributed_system;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Graph g = ReadConfigFile.readFile("config.txt");
		int nodeId = Integer.parseInt(args[0]);
		
		TCPServer tcp0 = new TCPServer(g.nodes.get(nodeId));
		//TCPServer tcp1 = new TCPServer(g.nodes.get(1));
		Thread s0 = new Thread(tcp0, "Server thread " + nodeId);
		//Thread s1 = new Thread(tcp1, "Server thread 1");
		s0.start();
		Thread.sleep(3000);
//		s1.start();
//		TCPClient client0 = new TCPClient(g.nodes.get(nodeId), g.nodes.get(1)); // connect from Node 0 to Node 1
//		TCPClient client1 = new TCPClient(g.nodes.get(1), g.nodes.get(0)); // connect from Node 1 to Node 0
//		Thread c0 = new Thread(client0, "Client thread 0");
//		Thread c1 = new Thread(client1, "Client thread 1");
//		c0.start();
//		c1.start();
	
		ArrayList<Integer> neighbor = g.adjList.get(nodeId);
		for(int n : neighbor) {
			TCPClient client = new TCPClient(g.nodes.get(nodeId), g.nodes.get(n)); // connect from Node 0 to Node 1
			Thread c = new Thread(client, "Client thread " + n);
			c.start();
		}
	}
}
