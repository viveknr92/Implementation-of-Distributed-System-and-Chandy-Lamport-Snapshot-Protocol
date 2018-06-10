package distributed_system;

public class Node {
	//public int nodeId; 
	public String hostName; 
	public int port;
	public boolean state; //Active or passive
	public int nodeId;
	public void setNode(int nodeId, String hostname, int port) {
		this.nodeId = nodeId;
		this.hostName = hostname;
		this.port = port;
	}
	public void printNode() {
		System.out.println(nodeId + "," + hostName + "," + port);
	}
}
