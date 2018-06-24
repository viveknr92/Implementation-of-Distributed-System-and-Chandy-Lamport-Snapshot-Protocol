import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Read the values for all variables from the configuration file
		MapProtocol mapObject = ConfigParser.readConfigFile(args[1]);
		// Get the node number of the current Node
		mapObject.id = Integer.parseInt(args[0]);
		int curNode = mapObject.id;
		//Get the configuration file from command line
		mapObject.configurationFileName = args[1];
		MapProtocol.outputFileName = mapObject.configurationFileName.substring(0, mapObject.configurationFileName.lastIndexOf('.'));
		//Build converge cast spanning tree in the beginning
		ConvergeCast.buildSpanningTree(mapObject.adjMatrix);
		// Transfer the collection of nodes from ArrayList to hash map which has node id as key since  
		// we need to get and node as value ,it returns <id,host,port> when queried with node Id.
		for(int i=0;i<mapObject.nodes.size();i++){
			mapObject.store.put(mapObject.nodes.get(i).nodeId, mapObject.nodes.get(i));
		}
	
		//Create a server socket and listen for clients
		TCPServer server = new TCPServer(mapObject);
		
		//Create channels and keep it till the end
		new TCPClient(mapObject, curNode);

		mapObject.vector = new int[mapObject.numOfNodes];

		//Initialize all the datastructures needed for the node to run the protocols
		mapObject.initialize(mapObject);

		//Initially node 0 is active therefore if this node is 0 then it should be active
		if(curNode == 0){
			mapObject.active = true;		
			//Call Chandy Lamport protocol if it is node 0
			new ChandyLamportThread(mapObject).start();		
			new SendMessageThread(mapObject).start();
		}
		
		server.listenforinput(); //Listen for client connections
		
	}
}
