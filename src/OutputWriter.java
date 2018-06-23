import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Print the output to the output File
public class OutputWriter {
	ProjectMain mainObj;

	public OutputWriter(ProjectMain mainObj) {
		this.mainObj = mainObj;
	}


	public void writeToFile() {
		String fileName = ProjectMain.outputFileName+"-"+mainObj.id+".out";
		synchronized(mainObj.output){
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
   
				for(int i=0;i<mainObj.output.size();i++){
					for(int j:mainObj.output.get(i)){
						bufferedWriter.write(j+" ");
						
					}
					if(i<(mainObj.output.size()-1)){
	            bufferedWriter.write("\n");
					}
				}			
				mainObj.output.clear();
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

