package main;

import fs.FSLibrary;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String[] ports = {"8080", "8081", "8082"};
		
		FSLibrary client = new FSLibrary();
		client.FS_init(ports);
		
		String text = "sec sec sec";
		client.FS_write(text, 0, text.length());
		
		String reply1 = client.FS_read(client.getId(), 0, text.length());
		if(reply1 != null)
			System.out.println(new String(reply1));
		
		
	}

}
