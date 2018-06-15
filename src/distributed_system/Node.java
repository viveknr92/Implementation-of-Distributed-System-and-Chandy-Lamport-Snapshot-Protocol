package distributed_system;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Node {
	//public int nodeId; 
	public String hostName; 
	public int port;
	public boolean state; //Active or passive
	public int nodeId;
	public String color;
	public boolean isNodeZero;
	public ArrayList<Integer> neighbors;
	public BlockingQueue<StreamMessage> msgqueue;
	public ArrayList<TCPClient> myClients = new ArrayList<>();
	public ArrayList<ServerConnections> connectedClients = new ArrayList<>();
	public void setNode(int nodeId, String hostname, int port) {
		this.nodeId = nodeId;
		this.hostName = hostname;
		this.port = port;
	}
	public void initializeNode() {
		this.color = "blue";
		this.isNodeZero = false;
		this.msgqueue = new ArrayBlockingQueue<>(30);
	}
	public void printNode() {
		System.out.println(nodeId + "," + hostName + "," + port);
	}
	public void setlist() {
	this.neighbors	= Graph.adjList.get(nodeId);
	}
	public ArrayList<Integer> getNeighborList() {
		return neighbors;
	}
	public boolean isNodeZero() {
		if(nodeId==0)
			return true;
		else
			return false;
	}
}
