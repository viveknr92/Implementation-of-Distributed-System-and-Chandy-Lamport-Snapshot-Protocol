package distributed_system;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {
	private InputStream dis;
	private OutputStream dos;
	private Socket s;
	public ClientRequestHandler(Socket s, InputStream dis, OutputStream dos) {
		// TODO Auto-generated constructor stub
		this.s = s;
        this.dis = dis;
        this.dos = dos;
	}
	public ClientRequestHandler() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			writer.println(" hello from server");
			writer.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			System.out.println(reader.readLine());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
