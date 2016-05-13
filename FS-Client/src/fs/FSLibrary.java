package fs;

import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

public class FSLibrary {
	
	private FrontEnd front_end;
		
	public void FS_init(int faults) throws Exception{
		this.front_end = new FrontEnd(faults);
	}
	
	public void FS_write(String text, int pos, int size) throws Exception{
		front_end.write(text.getBytes(), pos, size);	
	}
	 
	public String FS_read(PublicKey id, int pos, int nbytes) throws InvalidKeyException, RemoteException, IllegalArgumentException, SignatureException, NoSuchAlgorithmException{ 
		byte[] text_bytes = front_end.read(id, pos, nbytes, false);
		return new String(text_bytes);
	}
	
	public PublicKey getId() {
		return front_end.getId();
	}
	
}