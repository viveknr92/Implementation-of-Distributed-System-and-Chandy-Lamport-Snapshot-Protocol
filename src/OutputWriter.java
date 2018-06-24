import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Print the output to the output File
public class OutputWriter {
	MapProtocol mapObject;

	public OutputWriter(MapProtocol mapObject) {
		this.mapObject = mapObject;
	}


	public void writeToFile() {
		String fileName = MapProtocol.outputFileName+"-"+mapObject.id+".out";
		synchronized(mapObject.output){
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
   
				for(int i=0;i<mapObject.output.size();i++){
					for(int j:mapObject.output.get(i)){
						bufferedWriter.write(j+" ");
						
				}
				if(i<(mapObject.output.size()-1)){
					bufferedWriter.write("\n");
				}
				}			
				mapObject.output.clear();
				bufferedWriter.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
	}

}

