import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class StreamMessage implements Serializable {
	MapProtocol m = new MapProtocol();
	int n = m.numOfNodes;
}

@SuppressWarnings("serial")
// Sends string message and vector timestamp
class AppMessage extends StreamMessage implements Serializable{
	String msg = "Test";
	int nodeId;
	int[] vector;
}
// Sends marker string and nodeId
@SuppressWarnings("serial")
class MarkerMessage extends StreamMessage implements Serializable{
	String msg = "marker";
	int nodeId;
}

// State message is sent to converge cast tree,
// It should have the process state and all its incoming channel states 
@SuppressWarnings("serial")
class StateMessage extends StreamMessage implements Serializable{
	boolean active;
	int nodeId;
	HashMap<Integer,ArrayList<AppMessage>> channelStates;
	int[] vector;
}

// Send Finish messages to all nodes to when termination is detected
@SuppressWarnings("serial")
class FinishMessage extends StreamMessage implements Serializable{
	String msg = "finish";
}
