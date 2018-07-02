import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{

	ServerSocket listener = null;
	Socket socket = null;
	int serverPort;
	private MapProtocol mapObject;
	
	public TCPServer(MapProtocol mapObject) {
		
		this.mapObject = mapObject; //Global mapObject
		// port number on which this node should listen 
		serverPort = mapObject.nodes.get(mapObject.id).port;
		try {
			listener = new ServerSocket(serverPort);
		} 
		catch(BindException e) {
			System.out.println("Node " + mapObject.id + " : " + e.getMessage() + ", Port : " + serverPort);
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void listenforinput(){
		//Listen and accept for any client connections
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					System.out.println("Connection Broken");
					System.exit(1);
				}
				// For every client request start a new thread 
				new ReceiveThread(socket,mapObject).start();
			}
		}
		finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}