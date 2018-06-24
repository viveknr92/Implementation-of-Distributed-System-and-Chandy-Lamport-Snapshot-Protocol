import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Print the globalSnapshots to the output File
public class OutputWriter {
	MapProtocol mapObject;

	public OutputWriter(MapProtocol mapObject) {
		this.mapObject = mapObject;
	}


	public void writeToFile() {
		String fileName = MapProtocol.outFile+"-"+mapObject.id+".out";
		synchronized(mapObject.globalSnapshots){
			try {
				File file = new File(fileName);
				FileWriter fileWriter;
				if(file.exists()){
					fileWriter = new FileWriter(file,true);
				}
				else
				{
					fileWriter = new FileWriter(file);
				}
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				/*if(file.length()!=0){
                bufferedWriter.write("\n");
            }*/
   
				for(int i=0;i<mapObject.globalSnapshots.size();i++){
					for(int j:mapObject.globalSnapshots.get(i)){
						bufferedWriter.write(j+" ");
						
					}
					if(i<(mapObject.globalSnapshots.size()-1)){
	            bufferedWriter.write("\n");
					}
				}			
				mapObject.globalSnapshots.clear();
				// Always close files.
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
				// Or we could just do this: ex.printStackTrace();
			}
		}
	}

}

