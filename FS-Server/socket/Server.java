package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {
	
	private int port;
	private int numberReplicas;
	
	private ServerSocket serverSocket;
	
    public Server(int port, int replicas) throws IOException {
    	
    	this.port = port;
    	this.numberReplicas = replicas;
        serverSocket = new ServerSocket(port);
        
        System.out.printf("Server accepting connections on port %d %n", port);
    }
    
    public void run() {
    	

		try {
	        // wait for and then accept client connection
	        // a socket is created to handle the created connection
			Socket clientSocket = serverSocket.accept();
			
			 System.out.printf("Connected to client %s on port %d %n",
            clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

	        // Create stream to receive data from client
	        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
	        
	        // Receive data until client closes the connection
	        String response = in.readLine();

	        if (response.equals("getNumberReplicas")){
        		out.writeBytes(new String(Integer.toString(getNumberReplicas())));
        		out.writeBytes("\n");
        	}
	        
        	System.out.printf("Received message with content: '%s'%n", response);
	        
	            

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        // Close connection to current client
//        clientSocket.close();
//        System.out.println("Closed connection with client");
//
//        // Close server socket
//        serverSocket.close();
//        System.out.println("Closed server socket");
    }
    
    public ServerSocket getServerSocket(){
    	return serverSocket;
    }
    
    public int getNumberReplicas(){
    	return numberReplicas;
    }
}
