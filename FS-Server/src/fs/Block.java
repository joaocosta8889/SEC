package fs;

import java.security.PublicKey;

public class Block {
	
	private byte[] content;
	private byte[] signature;
	
	public Block(Data data, byte[] signature) {
		
		this.content = writeBlock(data);
		this.signature = signature;
	}
	
	public Block() {
		
		byte[] nullCont = new byte[0];
		this.content = nullCont;
		this.signature = null;
	}
	
	public byte[] getBlockContent() {
		return content;
	}
	
	public byte[] getSignature() {
		return signature;
	}

	public void setBlockContent(Data data, byte[] signature) {
		
		this.content = writeBlock(data);
		this.signature = signature;
	}
	
	private byte[] writeBlock(Data cont){
		
		byte[] newContent = null;
		byte[] toWrite = cont.getDataContent();
		
		if((cont.getDataPos() + cont.getDataSize()) > this.content.length){
			// cria um novo vetor e vai copiar conteudo
			int contSize = this.content.length;
			//int newSize =  contSize + ((cont.getDataPos() + cont.getDataSize()) - contSize);
			int newSize =  contSize + (cont.getDataPos() + cont.getDataSize());
			newContent = new byte[newSize];
			
			// copia conteudo antigo
			for(int i=0; i<contSize; i++){
				newContent[i] = this.content[i];
			}
			
			// copia novo conteudo 
			for(int i=this.content.length, j=0; i<newSize || j < cont.getDataSize(); i++, j++){
				newContent[i] = toWrite[j];
			}
		}else{
			//escreve por cima 
			
			for(int i=cont.getDataPos(), j=0; i<cont.getDataSize() || j < cont.getDataSize(); i++, j++){
				this.content[i] = toWrite[j];
			}
			newContent = this.content;
		}
		return newContent;
	}
}


