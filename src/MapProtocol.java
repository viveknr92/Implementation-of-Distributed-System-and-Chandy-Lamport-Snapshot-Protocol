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
	//ArrayList which holds the nodes part of the distributed system 
	ArrayList<Node> nodes;
	//HashMap which has node number as keys and <id,host,port> as value
	HashMap<Integer,Node> store;
	// Create all the channels in the beginning and keep it open till the end
	// Mapping each connection between the nodes as a channel
	HashMap<Integer,Socket> channels;
	// Create all the output streams associated with each socket 
	// Create a mapping between each sent message with object output stream
	HashMap<Integer,ObjectOutputStream> oStream;
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
	ArrayList<int[]> output;

	public MapProtocol() {
		// TODO Auto-generated constructor stub
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
}
