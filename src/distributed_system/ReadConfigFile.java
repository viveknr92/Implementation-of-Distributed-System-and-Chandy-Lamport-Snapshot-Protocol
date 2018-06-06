package distributed_system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadConfigFile {
	public String filename;
	public boolean success = false;
	public ReadConfigFile(String filename) {
		this.filename = filename;
		success = readFile(filename);
	}
	public boolean readFile(String filename) {
		File file = new File(filename);
		String line;
		Scanner sc;
		try {
			sc = new Scanner(file);
			line = sc.nextLine();
			if(line.startsWith("#")) { // comment
				line = sc.nextLine();
			}
			String[] globalparameters = line.split(" ");
			try {
				GlobalParameters.setGlobalParameters(globalparameters);
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
				return false;
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(!line.contentEquals("")) {
				throw new ConfigFileFormatException("Missing line break");				
			}
			line = sc.nextLine();
			///////////////////////////////////////////////////////////////////////////////////////////////////
			while(!line.contentEquals("")) {
				String[] nodeInfo = line.split(" ");
				Node nd = new Node();
				nd.setNode(nodeInfo);
				nd.printNode();
				if (nodeInfo.length > 3) {
					if (nodeInfo[3].startsWith("#")) {
						line = sc.nextLine();
					}
					else {
						throw new ConfigFileFormatException("Unidentified characters in line");				
					}
				}
				else {
					line = sc.nextLine();
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(!line.contentEquals("")) {
				throw new ConfigFileFormatException("Missing line break");				
			}
			line = sc.nextLine();
			///////////////////////////////////////////////////////////////////////////////////////////////////
			String[] neighborNodeInfo = line.split(" ");
			NeighborNode neighbor = new NeighborNode();
			while(!line.contentEquals("")) {
				//TODO
				if (neighborNodeInfo.length > 3) {
					if (neighborNodeInfo[3].startsWith("#")) {
						line = sc.nextLine();
					}
					else {
						throw new ConfigFileFormatException("Unidentified characters in line");				
					}
				}
				else {
					line = sc.nextLine();
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			sc.close();
			return true;
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		catch (ConfigFileFormatException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}
