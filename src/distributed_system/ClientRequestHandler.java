package distributed_system;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {
	private Socket sock;
	private InputStream inputstream;
	private OutputStream outputstream;
	public ClientRequestHandler(Socket sock) {
		// TODO Auto-generated constructor stub
		this.sock = sock;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {	
			outputstream = sock.getOutputStream();
			PrintWriter writer = new PrintWriter(outputstream);
			writer.println("hello from server");
			writer.flush();
			
			inputstream = sock.getInputStream();
			AppMessage appmsg = new AppMessage() ;
			ObjectInputStream objin = new ObjectInputStream(inputstream);
			appmsg = (AppMessage) objin.readObject();
			appmsg.printAppMsg();
			//BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
			//System.out.println("inputstream"+ inputstream.available());
			//System.out.println(reader.readLine());
			writer.close();
			outputstream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
