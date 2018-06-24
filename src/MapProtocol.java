import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

enum Color { RED,BLUE};
@SuppressWarnings("serial")
public class MapProtocol implements Serializable  {
	int id;
	int[][] adjMtx;
	int msgSentCount;
	boolean isFirstSnapshot;
	boolean active;
	static String outFile;
	int[] vector;
	int saveChannelMsg;
	int numOfNodes,minPerActive,maxPerActive,minSendDelay,snapshotDelay,maxNumber;
	ArrayList<Integer> neighbors;
	Color color;
	
	String configFileName;
	//ArrayList which holds the total processes(nodes) 
	ArrayList<Node> nodes = new ArrayList<Node>();
	//Mapping between process number as keys and <id,host,port> as value
	HashMap<Integer,Node> nodeInfo = new HashMap<Integer,Node>();
	// Create all the channels in the beginning and keep it open till the end
	// Mapping between each process as a server and its client connections
	HashMap<Integer,Socket> channels = new HashMap<Integer,Socket>();
	// Create all the output streams associated with each socket 
	//Mapping between each sent message with object output stream
	HashMap<Integer,ObjectOutputStream> oStream = new HashMap<Integer,ObjectOutputStream>();
	// Mapping between ArrayList of messages for each process receiving incoming messages
	HashMap<Integer,ArrayList<AppMsg>> channelStates;
	// Mapping between incoming channels and boolean received marker message
	HashMap<Integer,Boolean> RxdMarker;
	// Mapping between processes and StMsg which stores all state messages
	HashMap<Integer,StateMsg> stateMsg;	
	//Check if state message has been received from all the processes in the system
	boolean[] isRxdStateMsg;
	//State(Vector,ChannelStates and its id) of the each process in stored in this StateMsg Object
	StateMsg curState;
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
		mapObject.channelStates = new HashMap<Integer,ArrayList<AppMsg>>();
		mapObject.RxdMarker = new HashMap<Integer,Boolean>();
		mapObject.stateMsg = new HashMap<Integer,StateMsg>();	

		Set<Integer> keys = mapObject.channels.keySet();
		
		for(Integer element : keys){
			ArrayList<AppMsg> arrList = new ArrayList<AppMsg>();
			mapObject.channelStates.put(element, arrList);
		}
		
		for(Integer e: mapObject.neighbors){
			mapObject.RxdMarker.put(e,false);
		}
		mapObject.isRxdStateMsg = new boolean[mapObject.numOfNodes];
		mapObject.curState = new StateMsg();
		mapObject.curState.vector = new int[mapObject.numOfNodes];
	}	
}
