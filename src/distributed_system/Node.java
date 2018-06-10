package distributed_system;

public class Node {
	//public int nodeId; 
	public String hostName; 
	public int port;
	public boolean state; //Active or passive
	public void setNode(String hostname, int port) {
		this.hostName = hostname;
		this.port = port;
	}
	public void printNode() {
		System.out.println(hostName + "," + port);
	}
}
