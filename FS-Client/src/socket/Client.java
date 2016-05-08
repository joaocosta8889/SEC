package socket;

import java.io.IOException;
import java.util.Scanner;

import fs.Data;

public class Client {

    public static void main( String[] args ) throws IOException {
    	
       FrontEnd fe = new FrontEnd();
    	
       System.out.println("Connected to server");

       while(true) {
       		System.out.println("Enter message: ");
       		
       		Scanner scan = new Scanner(System.in);
       		String message = scan.nextLine();
		
       		if(new String("exit").equals(message)) {
       			fe.exit(); 
       			break;
       		}
			if(new String("read").equals(message)) {
				
				fe.read();
       		} else {if(new String("write").equals(message)) {
       			
//				String[] messages = new String[3];
//				messages[0] = new String("Position to start write : ");
//				messages[1] = new String("Numbers of caracters to write: ");
//				messages[2] = new String("Content to write : ");
//				
//				String messageRequest = requestInputs("write", messages);
				Data newData = new Data(0, 3, "abc".getBytes());
				System.out.println("criei data");
				fe.write(newData);
       		} else{
       			System.out.println("Commands available: exit, read, write. Try again!");
       		}
       		}
       		       
        }
    }
    
//	private static String requestInputs(String type, String[] messages){
//		
//		String[] inputRequested = new String[3];
//		for(int i = 0; i < messages.length; i++){
//			String message = null;
//		
//			while(message == null) {
//				System.out.print(messages[i]);
//				Scanner scan = new Scanner(System.in);
//				message = scan.nextLine();
//			}
//			inputRequested[] = inputRequested.concat("." + new String (message));
//		}
//		return inputRequested;
//	}
    
}
