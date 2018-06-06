package distributed_system;

public class GlobalParameters {
	public static int nodes; 
	public static int minPerActive;
	public static int maxPerActive;
	public static int minSendDelay;
	public static int snapshotDelay;
	public static int maxNumber;
	public static void print() {
		System.out.println(GlobalParameters.nodes);
		System.out.println(GlobalParameters.minPerActive);
		System.out.println(GlobalParameters.maxPerActive);
		System.out.println(GlobalParameters.minSendDelay);
		System.out.println(GlobalParameters.snapshotDelay);
		System.out.println(GlobalParameters.maxNumber);
	}
}
