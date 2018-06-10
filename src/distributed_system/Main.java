package distributed_system;

import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		Graph g = ReadConfigFile.readFile("config.txt");
		int nodeId = Integer.parseInt(args[0]);
		//System.out.println("here " + nd.port);
		//System.out.println(g.adjList);
		//System.out.println(g.nodes.get(Integer.parseInt(args[0])).port);
		//System.out.println(g.nodes.get(1).hostName);
		//System.out.println(g.vertices);
		//System.out.println(g.adjList.get(0));
		//g.setAdjMtx();
		//g.printAdjMtx();
		Thread t = new Thread() {
		    public void run() {
		    	new TCPServer(g.nodes.get(nodeId).port);
		    }
		};
		t.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Integer> neighbor = g.adjList.get(nodeId);
		for(int n : neighbor) {
			System.out.println(g.nodes.get(n).hostName + " " + g.nodes.get(n).port);
			new TCPClient(g.nodes.get(n).hostName, g.nodes.get(n).port);
		}
	
	}
	
}
