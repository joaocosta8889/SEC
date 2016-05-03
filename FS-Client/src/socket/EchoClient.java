package socket;

import java.io.*;
import java.net.*;

public class EchoClient {

//	public NewClient() {
//		// TODO Auto-generated constructor stub
//	}

	public static void main(String[] args) throws IOException{
		String host = "localhost";
		int port = 8080;
		
		try{
			Socket echoSocket = new Socket(host, port);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		String userInput;
		
		while((userInput = stdIn.readLine()) != null){
			out.println(userInput);
			System.out.println("echo1 " + in.readLine());
			System.out.println("echo2 " + in.readLine());
		}
		} catch(UnknownHostException e){
			System.err.println("" + host);
			System.exit(1);
			
		}catch(IOException e){
			System.err.println("" + host);
			System.exit(1);
		}
	}

}
