package distributed_system;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph { // contains nodes and edges
	public HashMap<Integer, ArrayList<Integer>> adjList;
	public int[][] adjMtx;
	public ArrayList<Node> nodes;
	public int vertices;
	public Graph(int vertices) {
		adjList = new HashMap<Integer, ArrayList<Integer>>();
		adjMtx =  new int[GlobalParameters.nodes][GlobalParameters.nodes];
		nodes = new ArrayList<>();
		this.vertices = vertices;
	}
	public void printAdjMtx() {
		for (int i = 0; i < GlobalParameters.nodes; i++) {
			for (int j = 0; j < GlobalParameters.nodes; j++) {
				System.out.print(adjMtx[i][j] + " ");
			}
			System.out.println();
		}
	}
	public void setAdjMtx() {
		for (int i = 0; i < GlobalParameters.nodes; i++) {
			for (int j = 0; j < adjList.get(i).size(); j++) {
				adjMtx[i][adjList.get(i).get(j)] = 1;
				adjMtx[adjList.get(i).get(j)][i] = 1;
			}
		}
	}
}
