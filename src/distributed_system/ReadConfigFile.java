package distributed_system;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReadConfigFile {
	public String filename;
	public ReadConfigFile(String filename) {
		this.filename = filename;
	}
	public static Graph readFile(String filename) { // returns graph object filled with details or returns null if format is incorrect
		Graph graph = null;
		File file = new File(filename);
		String line;
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			line = sc.nextLine();
			if(line.startsWith("#")) { // ignore comments
				line = sc.nextLine();
			}
			String[] globalparameters = line.split(" ");
			////////////////////////////////////////////////////////////////////////////////////////
			try {
				GlobalParameters.setGlobalParameters(globalparameters);
				//GlobalParameters.print();
				if (globalparameters.length > 6) { // if the line contains more than 6 words
					if (globalparameters[6].startsWith("#")) {
						line = sc.nextLine(); // ignore comments
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
				return null;
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(!line.contentEquals("") && !line.startsWith("#")) { // look for empty line after global parameters
				throw new ConfigFileFormatException("Missing line break");				
			}
			line = sc.nextLine();
			///////////////////////////////////////////////////////////////////////////////////////////////////
			graph = new Graph(GlobalParameters.nodes);
			//Read node information and store it in "nodes" in the "graph" object
			while(!line.contentEquals("") && !line.startsWith("#")) {
				String[] nodeInfo = line.split(" ");
				Node nd = new Node();
				nd.setNode(Integer.parseInt(nodeInfo[0]), nodeInfo[1], Integer.parseInt(nodeInfo[2]));
				graph.nodes.put(Integer.parseInt(nodeInfo[0]), nd);
				if (nodeInfo.length > 3) { // if the line contains more than 3 words
					if (nodeInfo[3].startsWith("#")) {
						line = sc.nextLine(); //ignore comment
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
			if(!line.contentEquals("") && !line.startsWith("#")) {  // look for empty line after node info
				throw new ConfigFileFormatException("Missing line break");				
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////
			String[] words; // Read Adjacency list and store the result in Hashmap
			for (int i = 0; sc.hasNextLine() ; i++) { 
				line = sc.nextLine(); //read each line and convert to string
				ArrayList<Integer> neighbors = new ArrayList<>();
				try {
					words = line.split(" ");
					for (int j = 0; j < words.length; j++) { 
						neighbors.add(Integer.parseInt(words[j])); //obtain the integer values from space separated string
					}
				} 
				catch (NumberFormatException e) {
					//ignore if the character is not an integer
				} 
				graph.adjList.put(i, neighbors);
			}
			//System.out.println(graph.adjList);
			///////////////////////////////////////////////////////////////////////////////////////////////////
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
		catch (ConfigFileFormatException e) {
			System.err.println(e.getMessage());
			return null;
		}
		finally {
			sc.close();
		}
		return graph;
	}
}
