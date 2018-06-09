package distributed_system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Graph { // contains nodes and edges
	public HashMap<Integer, ArrayList<Integer>> adjList;
	public HashMap<Integer, ArrayList<Node>> adj;
	public int[][] adjMtx;
	public HashMap<Integer, Node> nodes;
	public int vertices;
	public Graph(int vertices) {
		adjList = new HashMap<Integer, ArrayList<Integer>>();
		adjMtx =  new int[GlobalParameters.nodes][GlobalParameters.nodes];
		nodes = new HashMap<>();
		this.vertices = vertices;
	}
	public void setadjListNodes() {
		
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
