package distributed_system;

public class Main {

	public static void main(String[] args) {
		Graph g = ReadConfigFile.readFile("config.txt");
		System.out.println(g.edges.get(0).dest.hostName);
	}
}
