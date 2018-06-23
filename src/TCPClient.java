import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
	//Create channels and keep it till the end
	public TCPClient(ProjectMain mainObj, int curNode) {
		for(int i=0;i<mainObj.numOfNodes;i++){
			// If the value in adjacency matrix is one for the current Node then its a neighbor
			//Put this in clientconnections.java file
			if(mainObj.adjMatrix[curNode][i] == 1){
				String hostName = mainObj.store.get(i).host;
				//InetAddress hostName = InetAddress.getLocalHost();
				int port = mainObj.store.get(i).port;
				InetAddress address = null;
				try {
					address = InetAddress.getByName(hostName);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				Socket client = null;
				try {
					client = new Socket(address,port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Get the sockets for all neighbors
				//Socket client = new Socket(hostName,port);
				// Put the neighbor sockets in hash map called channels indexed by their node id's
				mainObj.channels.put(i, client);
				mainObj.neighbors.add(i);
				// Get an output stream associated with each socket and put it in a hashmap oStream
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(client.getOutputStream());
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				mainObj.oStream.put(i, oos);	
			}
			//Put this in clientconnections.java file
		}
	}
}
