package main;

import java.security.SignatureException;

import fs.FSLibrary;

public class App {

	public static void main(String[] args) throws Exception {
		
		FSLibrary client1 = new FSLibrary();
		client1.FS_init();
		
		String plainText = "This is a test";
		System.out.println("Text: " + plainText);
		
		System.out.println("Writing...");
		try {
			client1.FS_write(0, 14,plainText.getBytes());
		} catch (SignatureException e) {
			e.getMessage();
		}
		System.out.println("Writing Complete!");
		
		System.out.println("Reading...");
		try {
			byte[] contents_read = client1.FS_read(client1.getId(),0 ,14);
			System.out.println("Contents Read: " + new String(contents_read));
		} catch (IllegalArgumentException e) {
			e.getMessage();
		}
				
	}
	
}
