import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

enum Color { RED,BLUE};
@SuppressWarnings("serial")
public class MapProtocol implements Serializable  {
	String configFileName;
	int id;
	int[][] adjMtx;
	int[] vector;
	boolean active;
	int numOfNodes,minPerActive,maxPerActive,minSendDelay,snapshotDelay,maxNumber;
	ArrayList<Integer> neighbors;
	int msgSentCount;
	
	Color color;	
	int saveChannelMsg;
	boolean isFirstSnapshot;
	static String outFile;

	//Mapping between process number as keys and <id,host,port> as value
	HashMap<Integer,Node> nodeInfo = new HashMap<Integer,Node>();
	
	//ArrayList which holds the total processes(nodes) 
	ArrayList<Node> nodes = new ArrayList<Node>();
	
	// Create all the channels in the beginning and keep it open till the end
	// Mapping between each process as a server and its client connections
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	
	// Mapping between ArrayList of messages for each process receiving incoming messages
	HashMap<Integer,ArrayList<AppMessage>> channelStates;
	
	// Create all the output streams associated with each socket 
	//Mapping between each sent message with object output stream
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();

	// Mapping between processes and StMsg which stores all state messages
	HashMap<Integer,StateMessage> stateMsg;	

	// Mapping between incoming channels and boolean received marker message
	HashMap<Integer,Boolean> RxdMarker;

	//State(Vector,ChannelStates and its id) of the each process in stored in this StateMessage Object
	StateMessage curState;
	
	//Check if state message has been received from all the processes in the system
	boolean[] isRxdStateMsg;
	
	//Final output vector snapshots
	ArrayList<int[]> globalSnapshots = new ArrayList<int[]>();
	
	//Constructor to initialize all variables
	public MapProtocol() {
		msgSentCount = 0;
		active=false;
		neighbors = new ArrayList<>();
		color = Color.BLUE;
		saveChannelMsg=0;
		isFirstSnapshot = true;
		nodes = new ArrayList<Node>();
		nodeInfo = new HashMap<Integer,Node>();
		channels = new HashMap<Integer,Socket>();
		oStream = new HashMap<Integer,ObjectOutputStream>();
		globalSnapshots = new ArrayList<int[]>();
	}
	
	//Initialize again before taking another snapshot
	void initialize(MapProtocol mapObject){
		mapObject.channelStates = new HashMap<Integer,ArrayList<AppMessage>>();
		mapObject.RxdMarker = new HashMap<Integer,Boolean>();
		mapObject.stateMsg = new HashMap<Integer,StateMessage>();	

		Set<Integer> keys = mapObject.channels.keySet();
		
		for(Integer element : keys){
			ArrayList<AppMessage> arrList = new ArrayList<AppMessage>();
			mapObject.channelStates.put(element, arrList);
		}
		
		for(Integer e: mapObject.neighbors){
			mapObject.RxdMarker.put(e,false);
		}
		mapObject.isRxdStateMsg = new boolean[mapObject.numOfNodes];
		mapObject.curState = new StateMessage();
		mapObject.curState.vector = new int[mapObject.numOfNodes];
	}	
}
