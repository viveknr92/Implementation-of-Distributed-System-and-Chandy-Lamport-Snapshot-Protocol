package distributed_system;

import java.util.ArrayList;

public class Graph {
	public ArrayList<Edge> edges;
	public int vertices;
	public Graph(int vertices) {
		edges = new ArrayList<>();
		this.vertices = vertices;
	}
}
