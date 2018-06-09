package distributed_system;

public class Main {

	public static void main(String[] args) {
		Graph g = ReadConfigFile.readFile("config.txt");
		Node nd = new Node();
		nd = g.nodes.get(args[0]);
		new TCPServer(nd.port);
		/*System.out.println(g.adjList);
		System.out.println(g.nodes);
		System.out.println(g.vertices);
		System.out.println(g.adjList.get(0));
		g.setAdjMtx();
		g.printAdjMtx();*/
		
		
	}
}
