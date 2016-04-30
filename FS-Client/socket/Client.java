package socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main( String[] args ) throws IOException {

        String host = "localhost";
        int port = 8080;
        String message = "getNumberReplicas";

        // Create client socket
        Socket socket = new Socket(host, port);
        System.out.printf("Connected to server %s on port %d %n", host, port);

        // Create stream to send data to server
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // Send text to server as bytes
        out.writeBytes(message);
        out.writeBytes("\n");
        System.out.println("Sent message: " + message);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        String response = in.readLine();
        System.out.printf("Client Received message with content: '%s'%n", response);
       
        int numberReplicas = 3;
  
        numberReplicas = Integer.parseInt(response);
      
    	for(int i= 1, j=port++; i < numberReplicas ; i++, j++){
    	    System.out.println("new Socket: " + j);
    		new Socket(host, j);
    	}

        // Close client socket
//        socket.close();
//        System.out.println("Connection closed");
    }
}