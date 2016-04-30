package fs;

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
		int dataPos = cont.getDataPos();
		int dataPosSize = dataPos + cont.getDataSize();
		
		if(dataPosSize > this.content.length){
			// cria um novo vetor e vai copiar conteudo
			int contSize = this.content.length;
			int newSize =  contSize + ((cont.getDataPos() + cont.getDataSize()) - contSize);
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
}


