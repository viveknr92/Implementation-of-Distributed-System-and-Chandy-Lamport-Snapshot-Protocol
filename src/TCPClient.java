import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
	//Each node acts as a client to all its neighboring nodes
	public TCPClient(MapProtocol mapObject, int curNode) {
		for(int i=0;i<mapObject.numOfNodes;i++){
			if(mapObject.adjMatrix[curNode][i] == 1){
				String hostName = mapObject.store.get(i).host;
				int port = mapObject.store.get(i).port;
				InetAddress address = null;
				try {
					address = InetAddress.getByName(hostName);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}
				Socket client = null;
				try {
					client = new Socket(address,port);
				} catch (IOException e) {
					System.out.println("Connection Broken");
					e.printStackTrace();
					System.exit(1);
				}
				//Send client request to all neighboring nodes
				mapObject.channels.put(i, client);
				mapObject.neighbors.add(i);
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(client.getOutputStream());
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				mapObject.oStream.put(i, oos);	
			}
		}
	}
}
