package socket;

import java.io.IOException;
import java.util.Scanner;

public class App {

	private static int port = 8080;

    public static void main(String[] args ) throws IOException {
    	System.out.print("How many faults do you want to happen?: ");
    	
    	int numberFaults = new Scanner(System.in).nextInt();
    	int numberReplicas = ((2 * numberFaults) + 1);
    	
    	for(int i= 0, j=port; i < numberReplicas ; i++, j++){
    		 new Thread(new Server(j, numberReplicas)).start();
    	}
    }
}
