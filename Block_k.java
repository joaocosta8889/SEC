package fs;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

public class Block_k {
	
	private static final int BLOCKSIZE =  10; //240; // 10Mb
	
	private ArrayList<Block> block_h = new ArrayList<Block>(0);
	
	private byte[] signature;
	
	private PublicKey pKey;
	
	
	public Block_k(Data_t data, byte[] sign, PublicKey key) throws BufferOverflowException, NoSuchAlgorithmException{
		
		//this.id 
		this.signature = sign;
		this.pKey = key;	
		this.putContent(data);
	
	}
	
	public Block_k(byte[] sign, PublicKey key) throws BufferOverflowException{
		
		this.block_h = null;
		//this.id 
		this.signature = sign;
		this.pKey = key;		
	
	}
	
	
	/* para testar!!! apagar dps */ 
	public Block_k(Data_t data) throws BufferOverflowException, NoSuchAlgorithmException{
		
		this.block_h.add(new Block(BLOCKSIZE));	
		this.putContent(data);
		this.signature = data.getData();
		this.pKey = null;		
	
	}


	public void addBlocks(int nrblobks){

		Data_t d = new Data_t(null, 0, 0);
		// tirar daqui o cont
		Block b = new Block(BLOCKSIZE);
		while (nrblobks > 0){

			this.block_h.add(b);
			nrblobks--;
		}
	}
	
	
	public void putContent(Data_t data) throws NoSuchAlgorithmException {			
		
		// bloco onde vai escrever
		
		int pos = (data.getPos() / BLOCKSIZE);
		int lastPos = block_h.size();

		// acrescenta blocos 
		if ((data.getSize() + data.getPos()) > (lastPos * BLOCKSIZE)){			
			
			int nr = ((lastPos - pos) + (data.getSize() / BLOCKSIZE) - lastPos);
			addBlocks(nr);
		}
		
		Block newBlock = null;
	
		int ciclos = (((data.getPos() % BLOCKSIZE)  + data.getSize()) / BLOCKSIZE);
		//recursao

		while (ciclos >= 0){
		
		if(((data.getPos() % BLOCKSIZE) + data.getSize()) > BLOCKSIZE){

			// bytes a escrever no bloco

			int originalSpace = data.getSize();
			
			data.setSize(BLOCKSIZE);
			Data_t cont = new Data_t(this.block_h.get(pos).getContent(), 0, this.block_h.get(pos).getContent().length);
			newBlock = new Block(cont, BLOCKSIZE);

			newBlock.putContentBlock(data);
			this.block_h.set(pos, newBlock);

			data.setContent(Arrays.copyOfRange(data.getData(), (BLOCKSIZE - data.getPos()) , originalSpace));
			data.setPos(0);
			data.setSize(data.getData().length);

		}else{		
		
		data.setPos((data.getPos() % BLOCKSIZE));
		
		newBlock = this.block_h.get(pos);
		newBlock.putContentBlock(data);
		
		Data_t d = new Data_t(newBlock.getContent(), 0, newBlock.getContent().length);
		Block b = new Block(d, BLOCKSIZE);
		b.putContentBlock(data);
		this.block_h.set(pos, newBlock);
		}
		newBlock = null;
		ciclos--;
		pos++;
		
		
		}

	}

		


		public byte[] getContent(byte[] hash) {
		
			byte[] out = null;
				
			for(Block  i : block_h){
				
				if(i.getId().equals(hash)){
					out = i.getContent();
				}
			}
			return out;
		}
	
		public byte[] getContent(int pos , int size) {
			
			byte[] out = null;
			int dim = this.block_h.size() * BLOCKSIZE;
			
			ByteBuffer bb = ByteBuffer.allocate(dim);
			
			for(Block  i : block_h){	
				bb.put(i.getContent(), 0, BLOCKSIZE);
				}
			
			bb.get(out, pos, size);
			return out;
			}		
		
/*
		public static void main(String[] args) throws BufferOverflowException, NoSuchAlgorithmException{
			
			byte[] c = "aasdfgdfgsdfhshghdfga".getBytes();
			byte[] ccc = "Car".getBytes();
			byte[] cccc = "carrinho".getBytes();
			

			//pos 0
			Data_t d1 = new Data_t(c, 0, c.length);
			Data_t d2 = new Data_t(ccc, 5, ccc.length);
			Data_t d3 = new Data_t(cccc, 6, cccc.length);
			Data_t d4 = new Data_t(ccc, 22, ccc.length);

			System.out.println("criei bloco:");
			Block_k b = new Block_k(d1);
			//b.getContent();
			
			System.out.println("existem: " + b.block_h.size() + " blocos");
			System.out.println("block 0: " + new String (b.block_h.get(0).getContent()));
			System.out.println("block 1: " + new String (b.block_h.get(1).getContent()));			
			System.out.println("block 2: " + new String (b.block_h.get(2).getContent()));
				b.putContent(d2);
				
				System.out.println("block 0: " + new String (b.block_h.get(0).getContent()));
				System.out.println("block 1: " + new String (b.block_h.get(1).getContent()));			
				System.out.println("block 2: " + new String (b.block_h.get(2).getContent()));
				
				b.putContent(d3);
				
				System.out.println("block 0: " + new String (b.block_h.get(0).getContent()));
				System.out.println("block 1: " + new String (b.block_h.get(1).getContent()));			
				System.out.println("block 2: " + new String (b.block_h.get(2).getContent()));
				
				b.putContent(d4);
				
				System.out.println("block 0: " + new String (b.block_h.get(0).getContent()));
				System.out.println("block 1: " + new String (b.block_h.get(1).getContent()));			
				System.out.println("block 2: " + new String (b.block_h.get(2).getContent()));
			//System.out.println("conteudo:" +new String(b.getContent()));
			//b.addBlocks(5);

			//System.out.println("conteudo:" +new String(b.getContent()));
	}*/

}