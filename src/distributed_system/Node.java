package distributed_system;

public class Node {
	public int nodeId; 
	public String hostName; 
	public int port;
	public void setNode(String[] nodeInfo) {
		nodeId = Integer.parseInt(nodeInfo[0]);
		hostName = nodeInfo[1] + ".utdallas.edu";
		port = Integer.parseInt(nodeInfo[2]);
	}
	public void printNode() {
		System.out.println(nodeId + "," + hostName + "," + port);
	}
}
