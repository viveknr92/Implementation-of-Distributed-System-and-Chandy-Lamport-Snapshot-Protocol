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
	public static void setGlobalParameters(String[] globalparameters) {
		GlobalParameters.nodes = Integer.parseInt(globalparameters[0]);
		GlobalParameters.minPerActive = Integer.parseInt(globalparameters[1]);
		GlobalParameters.maxPerActive = Integer.parseInt(globalparameters[2]);
		GlobalParameters.minSendDelay = Integer.parseInt(globalparameters[3]);
		GlobalParameters.snapshotDelay = Integer.parseInt(globalparameters[4]);
		GlobalParameters.maxNumber = Integer.parseInt(globalparameters[5]);
	}
}
