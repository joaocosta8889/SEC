package main;

import java.util.Scanner;

import fs.FSLibrary;

public class Test {

	public static void main(String[] args) throws Exception {
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Number of tolerated faults: ");
		while (!sc.hasNextInt()) 
			sc.next();
		int f = sc.nextInt();
		
		FSLibrary client = new FSLibrary();
		client.FS_init(f);
		
		String text1 = "text1 text2 text3";
		client.FS_write(text1, 0, text1.length());
		
		String reply1 = client.FS_read(client.getId(), 12, 5);

		System.out.println(reply1);
	
		
	}

}
