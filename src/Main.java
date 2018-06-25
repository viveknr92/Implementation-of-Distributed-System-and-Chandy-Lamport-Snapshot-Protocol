import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final int NODE_ZERO = 0;
		//Parse through config.txt file
		MapProtocol mapObject = ConfigParser.readConfigFile(args[1]);
		// Get the node number of the current Node
		mapObject.id = Integer.parseInt(args[0]);
		int curNode = mapObject.id;
		//Get the configuration file name from command line
		mapObject.configFileName = args[1];
		MapProtocol.outFile = mapObject.configFileName.substring(0, mapObject.configFileName.lastIndexOf('.'));
		//Build converge cast spanning tree
		ConvergeCast.constructNodeTree(mapObject.adjMtx);
		// Transfer the collection of nodes from ArrayList to hash map nodes
		for(int i=0;i<mapObject.nodes.size();i++){
			mapObject.nodeInfo.put(mapObject.nodes.get(i).nodeId, mapObject.nodes.get(i));
		}
	
		//Create a server socket and listen for clients
		TCPServer server = new TCPServer(mapObject);
		
		//Create channels and keep it till the end
		new TCPClient(mapObject, curNode);

		mapObject.vector = new int[mapObject.numOfNodes];

		//Initialize all data structures
		mapObject.initialize(mapObject);

		//Initially node 0 is active therefore if this node is 0 then it should be active
		if(curNode == NODE_ZERO){
			mapObject.active = true;		
			//Call Chandy Lamport protocol if it is node 0
			new ChandyLamportThread(mapObject).start();		
			new SendMessageThread(mapObject).start();
		}
		
		server.listenforinput(); //Listen for client connections
		
	}
}
