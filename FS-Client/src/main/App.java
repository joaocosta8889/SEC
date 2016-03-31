package main;

import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import fs.FSLibrary;

public class App {

	public static void main(String[] args) throws Exception {
		
		FSLibrary client1 = new FSLibrary();
		client1.FS_init();
		
		String text = "data...data...data";
		client1.FS_write(0, text.length(), text.getBytes());
		
		FSLibrary client2 = new FSLibrary();
		client2.FS_init();
		
		
		List<PublicKey> keys = client2.FS_list();
		byte[] data_read = client2.FS_read(keys.get(0), 0, text.length());
		System.out.println(new String(data_read));
		
	}
	
}
