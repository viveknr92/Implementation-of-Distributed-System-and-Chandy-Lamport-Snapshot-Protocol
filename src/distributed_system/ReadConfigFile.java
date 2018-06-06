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
			Graph graph = new Graph(GlobalParameters.nodes);
			while(!line.contentEquals("")) {
				String[] nodeInfo = line.split(" ");
				Node nd = new Node();
				nd.setNode(nodeInfo);
				nd.printNode();
				graph.nodes.add(nd);
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
			///////////////////////////////////////////////////////////////////////////////////////////////////
			String[] neighbors;
			for (int i = 0; sc.hasNextLine() ; i++) { 
				//read each line and convert to string
				line = sc.nextLine();
				for (int j = 0; j < GlobalParameters.nodes ; j++) {
					//obtain the integer values from comma separated string
					try {
						neighbors = line.split(" ");
						Edge e = new Edge(graph.nodes.get(i),graph.nodes.get(Integer.parseInt(neighbors[j])));
						graph.edges.add(e); //add an edge with source vertex, destination vertex and weight 
						System.out.println();
					} 
					catch (NumberFormatException e) {
						//ignore if the character is not an integer
					}
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
