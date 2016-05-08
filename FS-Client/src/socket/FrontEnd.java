package socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import fs.Data;
import fs.FSLibrary;

public class FrontEnd {
	
    private static int REPLICAS = 3;
	
	private String host = "localhost";
	private Socket servers[] = new Socket[3];
	private BufferedReader in[] = new BufferedReader[3];
	private DataOutputStream out[] = new DataOutputStream[3];

	
	public FrontEnd() throws UnknownHostException, IOException {
		
		for(int i=0, j=8080; i<REPLICAS; i++, j++) {
			this.servers[i] = new Socket(host, j);
			this.in[i] = new BufferedReader(new InputStreamReader(servers[i].getInputStream()));
			this.out[i] = new DataOutputStream(servers[i].getOutputStream());		
		}
	}
	
	public void write(Data dataObject) throws IOException {
		
		for(int i=0; i<REPLICAS; i++) {
			
			out[i].writeBytes("write");
			out[i].writeBytes("\n");
			
			String ack = in[i].readLine();
			System.out.println(ack);
		}
	}
	
	public void read() throws IOException {
		
		for(int i=0; i<REPLICAS; i++) {
			
			
			out[i].writeBytes("read");
			out[i].writeBytes("\n");
			String received = in[i].readLine();
			
		    System.out.printf("Content received from server %d: %s %n", servers[i].getPort(), received);
		}
	}
	
	public void exit() throws IOException {	
		for(Socket server : servers) {
			server.close();
		}
		System.out.println("Connection closed");
	}
	

}
