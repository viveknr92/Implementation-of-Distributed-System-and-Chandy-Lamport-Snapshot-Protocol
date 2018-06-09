package distributed_system;

public class Main {

	public static void main(String[] args) {
		Graph g = ReadConfigFile.readFile("config.txt");
		
		//System.out.println("here " + nd.port);
		new TCPServer(g.nodes.get(Integer.parseInt(args[0])).port);
		//System.out.println(g.adjList);
		//System.out.println(g.nodes.get(Integer.parseInt(args[0])).port);
		//System.out.println(g.nodes.get(1).hostName);
		//System.out.println(g.vertices);
		//System.out.println(g.adjList.get(0));
		//g.setAdjMtx();
		//g.printAdjMtx();
		
		
	}
}
