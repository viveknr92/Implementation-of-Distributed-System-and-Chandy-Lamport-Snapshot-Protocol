import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadConfigFile {

	public static MapProtocol readConfigFile(String name) throws IOException{
		MapProtocol mapFile = new MapProtocol();
		int count = 0,flag = 0;
		// Keeps track of current node
		int curNode = 0;
		
		String fileName = System.getProperty("user.dir") + "/" + name;
		
		String line = null;
		try {
			
			FileReader fr = new FileReader(fileName);
			
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null) {
				if(line.length() == 0 || line.startsWith("#"))
					continue;
				// Ignore comments and consider only those lines which are not comments
				String[] config_input;
				if(line.contains("#")){
					String[] config_input_comment = line.split("#.*$"); //Ignore text after # symbol
					config_input = config_input_comment[0].split("\\s+");
				}
				else {
					config_input = line.split("\\s+");
				}

				if(flag == 0 && config_input.length == 6){
					mapFile.numOfNodes = Integer.parseInt(config_input[0]);
					mapFile.minPerActive = Integer.parseInt(config_input[1]);
					mapFile.maxPerActive = Integer.parseInt(config_input[2]);
					mapFile.minSendDelay = Integer.parseInt(config_input[3]);
					mapFile.snapshotDelay = Integer.parseInt(config_input[4]);
					mapFile.maxNumber = Integer.parseInt(config_input[5]);
					mapFile.adjMtx = new int[mapFile.numOfNodes][mapFile.numOfNodes];
					flag++;
				}
				else if(flag == 1 && count < mapFile.numOfNodes)
				{							
					mapFile.nodes.add(new Node(Integer.parseInt(config_input[0]),config_input[1],Integer.parseInt(config_input[2])));
					count++;
					if(count == mapFile.numOfNodes){
						flag = 2;
					}
				}
				else if(flag == 2){
					for(String i : config_input){
						mapFile.adjMtx[curNode][Integer.parseInt(i)] = 1;
					}
					curNode++;
				}
			}
			br.close();  
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");                  
		}
		for(int i=0;i<mapFile.numOfNodes;i++){
			for(int j=0;j<mapFile.numOfNodes;j++){
				if(mapFile.adjMtx[i][j] == 1){
					mapFile.adjMtx[j][i] = 1;
				}
				if(mapFile.adjMtx[i][i] == 1){
					mapFile.adjMtx[i][i] = 0;
				}
			}
		}
		return mapFile;
	}

//	public static void main(String[] args) throws IOException {
//		MapProtocol m = ReadConfigFile.readConfigFile("config.txt");
//		
//		for(Node n : m.nodes) {
//			System.out.println(n.host + " " + n.nodeId + " " + n.port);
//		}
//		System.out.println(m.numOfNodes);
//		System.out.println(m.minPerActive);
//		System.out.println(m.maxPerActive);
//		System.out.println(m.minSendDelay);
//		System.out.println(m.snapshotDelay);
//		System.out.println(m.maxNumber);
//		
//		for(int i=0;i<m.numOfNodes;i++){
//			for(int j=0;j<m.numOfNodes;j++){
//				System.out.print(m.adjMtx[i][j]+"  ");
//			}
//			System.out.println();
//		}
//
//	}
}

