package socket;

import java.io.*;
import java.net.*;

public class EchoServer {

	public static void main(String[] args) throws IOException{
		int port = 8080;

		ServerSocket serverSocket = null;
		
		try{
			serverSocket = new ServerSocket(port);
		}catch(IOException e){
			System.out.println("" + port);
			System.out.println("" + e.getMessage());
		}
		
		while(true){
			ClientWorker w;
			
			try{
				w = new ClientWorker(serverSocket.accept());
				Thread t = new Thread(w);
				t.start();
			} catch(IOException e){
				System.out.println("Failed");
				System.exit(-1);
			}
		}
	}
}
