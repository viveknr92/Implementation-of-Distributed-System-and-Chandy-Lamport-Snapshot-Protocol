import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

enum Color { RED,BLUE};
@SuppressWarnings("serial")
public class ProjectMain implements Serializable  {
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
	void initialize(ProjectMain mainObj){
		mainObj.channelStates = new HashMap<Integer,ArrayList<ApplicationMsg>>();
		mainObj.receivedMarker = new HashMap<Integer,Boolean>();
		mainObj.stateMessages = new HashMap<Integer,StateMsg>();	

		Set<Integer> keys = mainObj.channels.keySet();
		//Initialize channelStates hashMap
		for(Integer element : keys){
			ArrayList<ApplicationMsg> arrList = new ArrayList<ApplicationMsg>();
			mainObj.channelStates.put(element, arrList);
		}
		//Initialize boolean hashmap receivedMarker to false
		for(Integer e: mainObj.neighbors){
			mainObj.receivedMarker.put(e,false);
		}
		mainObj.nodesInGraph = new boolean[mainObj.numOfNodes];
		mainObj.myState = new StateMsg();
		mainObj.myState.vector = new int[mainObj.numOfNodes];
	}


	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Read the values for all variables from the configuration file
		ProjectMain mainObj = ConfigParser.readConfigFile(args[1]);
		// Get the node number of the current Node
		mainObj.id = Integer.parseInt(args[0]);
		int curNode = mainObj.id;
		//Get the configuration file from command line
		mainObj.configurationFileName = args[1];
		ProjectMain.outputFileName = mainObj.configurationFileName.substring(0, mainObj.configurationFileName.lastIndexOf('.'));
		//Build converge cast spanning tree in the beginning
		ConvergeCast.buildSpanningTree(mainObj.adjMatrix);
		// Transfer the collection of nodes from ArrayList to hash map which has node id as key since  
		// we need to get and node as value ,it returns <id,host,port> when queried with node Id.
		for(int i=0;i<mainObj.nodes.size();i++){
			mainObj.store.put(mainObj.nodes.get(i).nodeId, mainObj.nodes.get(i));
		}
	
		//Create a server socket and listen for clients
		TCPServer server = new TCPServer(mainObj);
		
		//Create channels and keep it till the end
		TCPClient client = new TCPClient(mainObj, curNode);

		mainObj.vector = new int[mainObj.numOfNodes];

		//Initialize all the datastructures needed for the node to run the protocols
		mainObj.initialize(mainObj);

		//Initially node 0 is active therefore if this node is 0 then it should be active
		if(curNode == 0){
			mainObj.active = true;		
			//Call Chandy Lamport protocol if it is node 0
			new ChandyLamportThread(mainObj).start();		
			new SendMessageThread(mainObj).start();
		}
		
		server.listenforinput(); //Listen for client connections
		
	}
}
