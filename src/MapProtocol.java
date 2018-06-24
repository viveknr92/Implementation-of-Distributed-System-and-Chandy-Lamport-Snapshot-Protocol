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
	int totalMessagesSent;
	boolean active;
	int[][] adjMatrix;
	int[] vector;
	ArrayList<Integer> neighbors;
	boolean blockAppMsg;
	Color color;
	int logging;
	boolean firstTime;
	
	String configurationFileName;
	//ArrayList which holds the total processes(nodes) 
	ArrayList<Node> nodes = new ArrayList<Node>();
	//Mapping between process number as keys and <id,host,port> as value
	HashMap<Integer,Node> store = new HashMap<Integer,Node>();
	// Create all the channels in the beginning and keep it open till the end
	// Mapping between each process as a server and its client connections
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	// Create all the output streams associated with each socket 
	//Mapping between each sent message with object output stream
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();
	// Mapping between ArrayList of messages for each process receiving incoming messages
	HashMap<Integer,ArrayList<ApplicationMsg>> channelStates;
	// Mapping between incoming channels and boolean received marker message
	HashMap<Integer,Boolean> receivedMarker;
	// Mapping between processes and StMsg which stores all state messages
	HashMap<Integer,StateMsg> stateMessages;	
	//Check if state message has been received from all the processes in the system
	boolean[] nodesInGraph;
	//State(Vector,ChannelStates and its id) of the each process in stored in this StateMsg Object
	StateMsg myState;
	//Final output vector snapshots
	ArrayList<int[]> output = new ArrayList<int[]>();
	
	//Constructor to initialize all variables
	public MapProtocol() {
		totalMessagesSent = 0;
		active=false;
		neighbors = new ArrayList<>();
		blockAppMsg = false;
		color = Color.BLUE;
		logging=0;
		firstTime = true;
		nodes = new ArrayList<Node>();
		store = new HashMap<Integer,Node>();
		channels = new HashMap<Integer,Socket>();
		oStream = new HashMap<Integer,ObjectOutputStream>();
		output = new ArrayList<int[]>();
	}
	
	//Initialize again before taking another snapshot
	void initialize(MapProtocol mapObject){
		mapObject.channelStates = new HashMap<Integer,ArrayList<ApplicationMsg>>();
		mapObject.receivedMarker = new HashMap<Integer,Boolean>();
		mapObject.stateMessages = new HashMap<Integer,StateMsg>();	

		Set<Integer> keys = mapObject.channels.keySet();
		
		for(Integer element : keys){
			ArrayList<ApplicationMsg> arrList = new ArrayList<ApplicationMsg>();
			mapObject.channelStates.put(element, arrList);
		}
		
		for(Integer e: mapObject.neighbors){
			mapObject.receivedMarker.put(e,false);
		}
		mapObject.nodesInGraph = new boolean[mapObject.numOfNodes];
		mapObject.myState = new StateMsg();
		mapObject.myState.vector = new int[mapObject.numOfNodes];
	}	
}
