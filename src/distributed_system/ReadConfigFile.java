package distributed_system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadConfigFile {
	public String filename;
	public ReadConfigFile(String filename){
		File file = new File(filename);
		String line;
		this.filename = filename;
		Scanner sc;
		try {
			sc = new Scanner(file);
			line = sc.nextLine();
			if(line.startsWith("#")) {
				line = sc.nextLine();
			}
			String[] globalparameters = line.split(" ");
			try {
				GlobalParameters.nodes = Integer.parseInt(globalparameters[0]);
				GlobalParameters.minPerActive = Integer.parseInt(globalparameters[1]);
				GlobalParameters.maxPerActive = Integer.parseInt(globalparameters[2]);
				GlobalParameters.minSendDelay = Integer.parseInt(globalparameters[3]);
				GlobalParameters.snapshotDelay = Integer.parseInt(globalparameters[4]);
				GlobalParameters.maxNumber = Integer.parseInt(globalparameters[5]);
				GlobalParameters.print();
				if (globalparameters.length > 6) {
					if (globalparameters[6].startsWith("#")) {
						line = sc.nextLine();
					}
					else {
						throw new ConfigFileFormatException("Unidentified characters in line");				
					}
				}
				else {
					line = sc.nextLine();
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigFileFormatException e) {
			System.err.println(e.getMessage());
		}
	}
}
