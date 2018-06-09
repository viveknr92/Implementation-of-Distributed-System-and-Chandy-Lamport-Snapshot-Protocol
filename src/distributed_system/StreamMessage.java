package distributed_system;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StreamMessage implements Serializable {
	String message;
	int nodeId;
	public void printAppMsg()
	{
		System.out.println(this.message);
		System.out.println(this.nodeId);
	}
}
class AppMessage extends StreamMessage implements Serializable{
	int[] vector;
	
	public AppMessage(String message, int nodeId, int[] vector) {
		this.message = message;
		this.nodeId = nodeId;
		this.vector = vector;
	}
	public void setAppMessage(String message, int nodeId, int[] vector) {
		this.message = message;
		this.nodeId = nodeId;
		this.vector = vector;
	}
	public AppMessage() {
		// TODO Auto-generated constructor stub
	}
}
class MarkerMessage extends StreamMessage {
	String message = "Marker message";
	int nodeId;
}
