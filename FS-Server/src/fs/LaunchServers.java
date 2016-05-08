package fs;

import java.rmi.RemoteException;
import java.util.Scanner;

public class LaunchServers {

	public LaunchServers() {
	}
	
	public static void main(String[] args) throws RemoteException {
		
		int port = 8081;
		Scanner sc = new Scanner(System.in);
		System.out.print("Number of faults to be supported : ");
		while (!sc.hasNextInt()) 
			sc.next();
		
		int f = sc.nextInt();
   		
   		for (int i=0; i < ((2 * f) + 1); i++, port++){
			new BlockServer(port);  
			
			System.out.println("LaunchServer on port " + port );    
   		}
	}

}
