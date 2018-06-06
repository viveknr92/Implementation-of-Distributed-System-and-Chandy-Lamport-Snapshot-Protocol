package distributed_system;

import java.util.ArrayList;

public class Graph {
	public ArrayList<Edge> edges;
	public ArrayList<Node> nodes;
	public int vertices;
	public Graph(int vertices) {
		edges = new ArrayList<>();
		nodes = new ArrayList<>();
		this.vertices = vertices;
	}
}
