import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

enum Color { RED,BLUE};
@SuppressWarnings("serial")
public class MapProtocol implements Serializable  {
	static String outputFileName;
	int id;
	int numOfNodes,minPerActive,maxPerActive,minSendDelay,snapshotDelay,maxNumber;
	int totalMessagesSent = 0;
	boolean active=false;
	int[][] adjMatrix;
	int[] vector;
	ArrayList<Integer> neighbors = new ArrayList<>();
	boolean blockAppMsg = false;
	Color color = Color.BLUE;
	int logging=0;
	boolean firstTime = true;
	String configurationFileName;
	//ArrayList which holds the nodes part of the distributed system 
	ArrayList<Node> nodes = new ArrayList<Node>();
	//HashMap which has node number as keys and <id,host,port> as value
	HashMap<Integer,Node> store = new HashMap<Integer,Node>();
	// Create all the channels in the beginning and keep it open till the end
	// Mapping each connection between the nodes as a channel
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	// Create all the output streams associated with each socket 
	// Create a mapping between each sent message with object output stream
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();
	// HashMap which stores ArrayList of messages recorded while the process is red for each channel
	
	HashMap<Integer,ArrayList<ApplicationMsg>> channelStates;
	// HashMap which stores all incoming channels and boolean received marker message
	HashMap<Integer,Boolean> receivedMarker;
	// HashMap which stores all state messages
	HashMap<Integer,StateMsg> stateMessages;	
	//Used to determine if state message has been received from all the processes in the system
	boolean[] nodesInGraph;
	//Every process stores its state(Vector,ChannelStates and its id) in this StateMsg Object
	StateMsg myState;
	//To hold output Snapshots
	ArrayList<int[]> output = new ArrayList<int[]>();

	//Re-initialize everything that is needed for Chandy Lamport protocol before restarting it
	void initialize(MapProtocol mapObject){
		mapObject.channelStates = new HashMap<Integer,ArrayList<ApplicationMsg>>();
		mapObject.receivedMarker = new HashMap<Integer,Boolean>();
		mapObject.stateMessages = new HashMap<Integer,StateMsg>();	

		Set<Integer> keys = mapObject.channels.keySet();
		//Initialize channelStates hashMap
		for(Integer element : keys){
			ArrayList<ApplicationMsg> arrList = new ArrayList<ApplicationMsg>();
			mapObject.channelStates.put(element, arrList);
		}
		//Initialize boolean hashmap receivedMarker to false
		for(Integer e: mapObject.neighbors){
			mapObject.receivedMarker.put(e,false);
		}
		mapObject.nodesInGraph = new boolean[mapObject.numOfNodes];
		mapObject.myState = new StateMsg();
		mapObject.myState.vector = new int[mapObject.numOfNodes];
	}


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
		TCPClient client = new TCPClient(mapObject, curNode);

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
