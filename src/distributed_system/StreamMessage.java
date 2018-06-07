package distributed_system;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StreamMessage implements Serializable {

}
class AppMessage extends StreamMessage implements Serializable  {
	String message = "application message";
	int nodeId;
	int[] vector;
}
class MarkerMessage extends StreamMessage implements Serializable  {
	String message = "Marker message";
	int nodeId;
}
