package fs;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Block implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private byte[] content;
	private int timestamp;
	private byte[] signature;
	
	public Block(int timestamp, byte[] signature) {
		this.content = new byte[0];
		this.timestamp = timestamp;
		this.signature = signature;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(Data data) {
		this.content = writeBlock(data);
	}
	
	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	private byte[] writeBlock(Data cont){
		
		byte[] newContent = null;
		byte[] toWrite = cont.getContent();
		int dataPos = cont.getPos();
		int dataPosSize = dataPos + cont.getSize();
		
		if(dataPosSize > this.content.length){
			// cria um novo vetor e vai copiar conteudo
			int contSize = this.content.length;
			int newSize =  contSize + ((cont.getPos() + cont.getSize()) - contSize);
			newContent = new byte[newSize];
			
			// copia conteudo antigo
			for(int i=0; i<contSize; i++){
				newContent[i] = this.content[i];
			}
			
			// copia novo conteudo 
			for(int i=dataPos, j=0; i< dataPosSize; i++, j++){
				newContent[i] = toWrite[j];
			}
		}else{
			//escreve por cima 
			for(int i=dataPos, j=0; i< dataPosSize; i++, j++){
				this.content[i] = toWrite[j];
			}
			newContent = this.content;
		}
		return newContent;
	}
	
	public byte[] getAuthData() {
		//convert timestamp into byte[]
		byte[] timeByte = ByteBuffer.allocate(4).putInt(timestamp).array();
		//append content with timestamp
		byte[] auth_data = new byte[this.content.length + timeByte.length];
		System.arraycopy(this.content, 0, auth_data, 0, this.content.length);
		System.arraycopy(timeByte, 0, auth_data, this.content.length, timeByte.length);
		
		return auth_data;	
	}

	
}


