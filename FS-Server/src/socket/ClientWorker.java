package socket;

import java.io.*;
import java.net.*;
import java.lang.*;

public class ClientWorker implements Runnable{

	private Socket client;
	
	public ClientWorker(Socket client) {
		this.client = client;
	}
	
	public void run(){
		String line;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		}catch(IOException e){
			System.out.println("ClientWorker failed");
			System.exit(-1);
		}
		
		while(true){
			try{
				line = in.readLine();
				out.println(line);
			} catch(IOException e){
				System.out.println("ClientWorker failed");
				System.exit(-1);
			}
		}
	}
}
