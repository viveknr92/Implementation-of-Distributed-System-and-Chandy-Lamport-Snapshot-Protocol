import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{

	ServerSocket listener = null;
	// This node listens as a Server for the clients requests 
	Socket socket = null;
	int serverPort;
	private MapProtocol mainObj;
	
	public TCPServer(MapProtocol mainObj) {
		
		this.mainObj = mainObj; //Global mainObj
		// Get the port number on which this node should listen 
		serverPort = mainObj.nodes.get(mainObj.id).port;
		try {
			listener = new ServerSocket(serverPort);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void listenforinput(){
		// Start server on this node's assigned port
		try {
			while (true) {
				try {
					socket = listener.accept();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// For every client request start a new thread 
				new ReceiveThread(socket,mainObj).start();
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