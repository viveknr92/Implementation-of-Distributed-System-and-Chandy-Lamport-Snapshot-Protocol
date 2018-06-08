package distributed_system; //this is a package

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{
	
	private ServerSocket serversocket;
	private int port;
	private boolean running;
	private Thread server;
	public TCPServer(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
		try {
			serversocket = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		running = true;
		while(running) {
			try {
				Socket sock = serversocket.accept();
				ClientRequestHandler crh = new ClientRequestHandler(sock);
				server = new Thread(crh, "client request handler");
				server.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		TCPServer tcpserver = new TCPServer(9999);
	}
	
}
