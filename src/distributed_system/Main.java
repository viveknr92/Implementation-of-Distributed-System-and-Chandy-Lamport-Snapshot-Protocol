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
		
		TCPServer tcp0 = new TCPServer(socket_queue, g.nodes.get(0));
		Thread producer = new Thread(tcp0, "Server thread " + nodeId);
		ServerConnections sc = new ServerConnections(socket_queue); //'this' is current obj of TCPServer
		Thread consumer = new Thread(sc, "server connections");
		producer.start();
		consumer.start(); //Start thread execution, calls run() method
		for (int i = 0; i < 3; i++) {
			TCPClient client = new TCPClient(g.nodes.get(1), g.nodes.get(0));
			Thread client_thread = new Thread(client, "client Thread ");
			client_thread.start();
		}
//		TCPClient client = new TCPClient(g.nodes.get(1), g.nodes.get(0));
//		Thread client_thread = new Thread(client, "client Thread 1");
//		
//		TCPClient client2 = new TCPClient(g.nodes.get(1), g.nodes.get(0));
//		Thread client_thread2 = new Thread(client, "client Thread 2");
//		
//		client_thread.start();
//		client_thread2.start();
	}
}
