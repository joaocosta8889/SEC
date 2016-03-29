package fs;

import java.nio.BufferOverflowException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
	
	private byte[] id;
	private byte[] content;
	
	public Block(Data_t cont, int sizeBlock,byte[] id) throws BufferOverflowException, NoSuchAlgorithmException{
		
		this.content = new byte[sizeBlock];
		//paddingBlock();
		this.putContentBlock(cont);
		this.id = id;
		
	}
	public Block(Data_t cont, int sizeBlock) throws BufferOverflowException, NoSuchAlgorithmException{
		
		this.content = new byte[sizeBlock];
		//paddingBlock();
		this.putContentBlock(cont);
		this.id = null;
		
	}
	
	public Block(int sizeBlock) throws BufferOverflowException{
		
		this.content = new byte[sizeBlock];
		this.id = null;
		
	}
	
	public void paddingBlock(){
		
		byte[] zero =  "0".getBytes();
		for (int i=0; i < this.content.length; i++){
			this.content[i] = zero[0];
		}
	}
	
	public byte[] getContent(){
		return this.content;
		
	}
	

	public void putContentBlock(Data_t cont) throws NoSuchAlgorithmException{
		
		byte[] dataCont = cont.getData();

		for(int i=cont.getPos(), j=0; (i<this.content.length) && ((i - cont.getPos()) < cont.getSize()) ;i++, j++){
			this.content[i] = dataCont[j];
		}
		
		if(this.id == null){
			this.id = makeHash(cont.getData());
		}
	}

	public byte[] getId(){
		return this.id;
	}
	
	private byte[] makeHash(byte[] cont) throws NoSuchAlgorithmException {
		
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(cont);
		byte[] id = messageDigest.digest();
		return id;
	}
	
	
}
