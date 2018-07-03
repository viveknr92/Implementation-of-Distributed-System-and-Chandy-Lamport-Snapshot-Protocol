import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Print the globalSnapshots to the output File
public class Output {
	MapProtocol mapObject;

	public Output(MapProtocol mapObject) {
		this.mapObject = mapObject;
	}


	public void storeSnapshotsToFile() {
		String fileName = MapProtocol.outFile + "-" + mapObject.id + ".out";
		synchronized(mapObject.globalSnapshots){
			try {
				File file = new File(fileName);
				FileWriter fW;
				if(file.exists()){
					fW = new FileWriter(file,true);
				}
				else
				{
					fW = new FileWriter(file);
				}
				BufferedWriter bW = new BufferedWriter(fW);

   
				for(int i=0;i<mapObject.globalSnapshots.size();i++){
					for(int j:mapObject.globalSnapshots.get(i)){
						bW.write(j + " ");
						
					}
					if(i<(mapObject.globalSnapshots.size()-1)){
						bW.write("\n");
					}
				}			
				mapObject.globalSnapshots.clear();
				bW.close();
			}
			catch(IOException ex) {
				System.out.println("Error writing to file '" + fileName + "'");
			}
		}
	}

}

