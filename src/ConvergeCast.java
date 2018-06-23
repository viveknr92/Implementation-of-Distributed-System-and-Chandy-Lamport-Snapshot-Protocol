import java.util.LinkedList;
import java.util.Queue;

//QNode stores the node value and level
//class QNode{
//	int node;
//	int level;
//	
//	public QNode(int i, int j) {
//		this.node = i;
//		this.level = j;
//	}
//}
public class ConvergeCast {
	
	static int[] parent;
	//Function that returns parent
	public static int getParent(int id) {
		return parent[id];
	}
	
	//Function that implements bfs to build spanning tree
	static void buildSpanningTree(int[][] adjMatrix){
		boolean[] visited = new boolean[adjMatrix.length];
		parent = new int[adjMatrix.length];
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(0);
		parent[0] = 0;
		//If its already visited then no need to visit again since its done in bfs tree , nodes 
		//visited at first level will have direct parents and so on
		visited[0] = true;
		while(!queue.isEmpty()){
			int node = queue.remove();
			for(int i=0;i<adjMatrix[node].length;i++){
				if(adjMatrix[node][i] == 1 && visited[i] == false){
					queue.add(i);
					ConvergeCast.parent[i] = node;
					visited[i] = true;
				}
			}
		}
	}

//	public static void main(String[] args){
//		int[][] adjMatrix ={ { 0,0,0,0,1},{1,0,0,0,0},{0,0,0,1,0},{0,1,0,0,0},{0,0,1,0,0}};
//		buildSpanningTree(adjMatrix);
//		for(int i=0;i<adjMatrix.length;i++)
//		System.out.println("Node  "+i+" Parent is "+getParent(i));
//	}
}
