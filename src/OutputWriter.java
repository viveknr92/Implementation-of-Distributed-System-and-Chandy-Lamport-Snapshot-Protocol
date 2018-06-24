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
   
				for(int i=0;i<mapObject.globalSnapshots.size();i++){
					for(int j:mapObject.globalSnapshots.get(i)){
						bufferedWriter.write(j+" ");
						
<<<<<<< HEAD
					}
					if(i<(mapObject.globalSnapshots.size()-1)){
	            bufferedWriter.write("\n");
					}
				}			
				mapObject.globalSnapshots.clear();
				// Always close files.
=======
				}
				if(i<(mapObject.output.size()-1)){
					bufferedWriter.write("\n");
				}
				}			
				mapObject.output.clear();
>>>>>>> 370bbc9d5302f4a56b0d479c86db7822b50f355a
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
	}

}

