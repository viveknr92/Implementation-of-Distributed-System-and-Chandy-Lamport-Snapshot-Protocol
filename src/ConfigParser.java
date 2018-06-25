import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigParser {

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
				if(line.contains("#")){
					String[] input = line.split("#.*$"); //Ignore text after # symbol
					String[] input1 = input[0].split("\\s+");
					if(flag == 0 && input1.length == 6){
						mapFile.numOfNodes = Integer.parseInt(input1[0]);
						mapFile.minPerActive = Integer.parseInt(input1[1]);
						mapFile.maxPerActive = Integer.parseInt(input1[2]);
						mapFile.minSendDelay = Integer.parseInt(input1[3]);
						mapFile.snapshotDelay = Integer.parseInt(input1[4]);
						mapFile.maxNumber = Integer.parseInt(input1[5]);
						mapFile.adjMtx = new int[mapFile.numOfNodes][mapFile.numOfNodes];
						flag++;
					}
					else if(flag == 1 && count < mapFile.numOfNodes)
					{							
						mapFile.nodes.add(new Node(Integer.parseInt(input1[0]),input1[1],Integer.parseInt(input1[2])));
						count++;
						if(count == mapFile.numOfNodes){
							flag = 2;
						}
					}
					else if(flag == 2){
						insertIntoMatrix(input1,mapFile, curNode);
						curNode++;
					}
				}
				else {
					String[] input = line.split("\\s+");
					if(flag == 0 && input.length == 6){
						mapFile.numOfNodes = Integer.parseInt(input[0]);
						mapFile.minPerActive = Integer.parseInt(input[1]);
						mapFile.maxPerActive = Integer.parseInt(input[2]);
						mapFile.minSendDelay = Integer.parseInt(input[3]);
						mapFile.snapshotDelay = Integer.parseInt(input[4]);
						mapFile.maxNumber = Integer.parseInt(input[5]);
						flag++;
						mapFile.adjMtx = new int[mapFile.numOfNodes][mapFile.numOfNodes];
					}
					else if(flag == 1 && count < mapFile.numOfNodes)
					{
						mapFile.nodes.add(new Node(Integer.parseInt(input[0]),input[1],Integer.parseInt(input[2])));
						count++;
						if(count == mapFile.numOfNodes){
							flag = 2;
						}
					}
					else if(flag == 2){
						insertIntoMatrix(input,mapFile,curNode);
						curNode++;
					}
				}
			}
			br.close();  
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" +fileName + "'");                
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

	static void insertIntoMatrix(String[] input, MapProtocol mapFile,int curNode) {
		for(String i:input){
			mapFile.adjMtx[curNode][Integer.parseInt(i)] = 1;
		}
	}

//	public static void main(String[] args) throws IOException{
//		ProjectMain m = ConfigParser.readConfigFile("config.txt");
//		for(int i=0;i<m.numOfNodes;i++){
//			for(int j=0;j<m.numOfNodes;j++){
//				System.out.print(m.adjMtx[i][j]+"  ");
//			}
//			System.out.println();
//		}
//
//	}
}

