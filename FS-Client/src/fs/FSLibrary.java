package fs;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

public class FSLibrary {
	
	private FrontEnd front_end;
		
	public void FS_init(String[] ports) throws MalformedURLException, RemoteException, NotBoundException, NoSuchAlgorithmException{
		this.front_end = new FrontEnd(ports);
	}
	
	public void FS_write(String text, int pos, int size){
		try {
			
			front_end.write(text.getBytes(), pos, size);
			
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException | RemoteException e) {
			e.getMessage();
		}
	}
	 
	public String FS_read(PublicKey id, int pos, int nbytes){  
		try {
			
			byte[] text_bytes = front_end.read(id, pos, nbytes, false);
			return new String(text_bytes);
			
		} catch (InvalidKeyException | RemoteException | IllegalArgumentException | SignatureException
				| NoSuchAlgorithmException e) {
			e.getMessage();
		}
		return null;
	}
	
	public PublicKey getId() {
		return front_end.getId();
	}
	
}
