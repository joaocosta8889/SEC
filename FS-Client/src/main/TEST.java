package main;

import fs.FSLibrary;

public class TEST {

	public static void main(String[] args) throws Exception {
		
		String[] ports = {"8080", "8081", "8082"};
		
		FSLibrary client = new FSLibrary();
		client.FS_init(ports);
		
		client.FS_write("'sec sec sec'", 0, 0);
		
		String reply1 = client.FS_read(client.getId(), 8, 3);
		if(reply1 != null)
			System.out.println(new String(reply1));
		
		
	}

}
