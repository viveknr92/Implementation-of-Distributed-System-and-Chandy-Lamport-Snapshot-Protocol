package distributed_system;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StreamMessage implements Serializable {

}
class AppMessage extends StreamMessage  {
	String message = "application message";
	int nodeId;
	int[] vector;
	public AppMessage(String message, int nodeId, int[] vector) {
		this.message = message;
		this.nodeId = nodeId;
		this.vector = vector;
	}
	public AppMessage() {
		// TODO Auto-generated constructor stub
	}
	public void printAppMsg()
	{
		System.out.println(this.message);
		System.out.println(this.nodeId);
	}
}
class MarkerMessage extends StreamMessage {
	String message = "Marker message";
	int nodeId;
}
