package distributed_system;


public class Edge { // every edge contains and source and dest node
	public Node source;
	public Node dest;
	
	public Edge(Node source, Node dest) {
		this.source = source;
		this.dest = dest;
		System.out.print(source.nodeId + " -> " + dest.nodeId + "\t");
	}
}
