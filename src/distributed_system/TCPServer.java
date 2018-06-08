package distributed_system;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class TCPServer 
{
	ServerSocket ss;
	ArrayList<ServerConnections> connections = new ArrayList<ServerConnections>();
	boolean shouldRun = true;
	
	public static void main(String args[])
	{
		new TCPServer(Integer.parseInt(args[0]));
	}
	
	//Constructor method
	public TCPServer(int port) {
		try {
			ss = new ServerSocket(port);
			while(shouldRun) {
				Socket s = ss.accept(); //Blocks until connection request is received from client
				ServerConnections sc = new ServerConnections(s, this); //'this' is current obj of TCPServer
				sc.start(); //Start thread execution, calls run() method
				connections.add(sc); //Add connection to Array List
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
}