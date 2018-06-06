package distributed_system;


public class Edge {
	public int source;
	public int dest;
	
	public Edge(int source, int dest) {
		this.source = source;
		this.dest = dest;
		System.out.print(source + " -> " + dest + "\t");
	}
}
