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
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
