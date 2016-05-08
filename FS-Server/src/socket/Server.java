package socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread{
	
	public static int REPLICAS = 3;
    private int port;
    private String content;
	
    public static void main( String[] args ) throws IOException {
      
      for(int i=0, j=8080; i<REPLICAS; i++, j++) {
    	  Server s = new Server(j);
          s.start();
      }
    }

    @Override
    public void run() {
    
        try {
        	
        	// Create server socket
        	ServerSocket serverSocket = new ServerSocket(port);
        	System.out.printf("Server accepting connections on port %d %n", port);
        	
        	// wait for and then accept client connection
            // a socket is created to handle the created connection
            Socket clientSocket = serverSocket.accept();
            System.out.printf("Server %d: Connected to client %s on port %d %n", this.port,
            clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
            
            // Create stream to receive data from client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Create stream to send data to client
        	DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            
            // Receive data until client closes the connection
            String message;
            while ((message = in.readLine()) != null) {
            	System.out.printf("Server %d: Received message with content: '%s'%n", this.port, message);
            	if(new String("read").equals(message)) {
            		out.writeBytes(new String(message));
        			out.writeBytes("\n");
            	} else {
            		String[] content = parseContent(message);
            		
            		String ack = Integer.toString(this.port) + ": ACK";
            		out.writeBytes(message);
        			out.writeBytes("\n");
            	}
            }
            
		} catch (IOException e) {
			e.getMessage();
		}

    }
    
    
    public Server(int port) {
    	this.port = port;
    }
    
    public int getPort() {
    	return port;
    }
    
    private String[] parseContent(String request){
    	return request.split(".");
    }

}
