package distributed_system;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient implements Runnable {
	private Socket socket;
	private int port;
	private String address;
	private Thread client;
	public TCPClient(String address, int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
		client = new Thread(this, "client");
		client.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			socket = new Socket(address, port);
			//OutputStream os = socket.getOutputStream();
			//AppMessage appmsg = new AppMessage();
			 //ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.println("this is String");
			writer.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Server says:" + reader.readLine());
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		TCPClient tcpclient = new TCPClient("localhost", 9999);
	}
	
}
