package tests;

import fs.Block;
import fs.Data;

public class TestBlock {
	
	public static void main(String[] args) throws Exception {
		
		// Para experimentar, tornar metodo writeBlock na classe Block private e descomentar o codigo abaixo
		/*
		byte[] firstEx = "Rui".getBytes();
		byte[] secondEx = "Peresidente".getBytes();
		
		Data d1 = new Data(firstEx.length, 0, firstEx);
		Data d2 = new Data(secondEx.length, 2, secondEx);
		Data d3 = new Data(firstEx.length, 5, firstEx);
		
		Block b1 = new Block();
		
		
		System.out.println(">>>>> B1 <<<<<<");
		b1.setBlockContent(d1, firstEx);
		byte[] b1cont = b1.getBlockContent();
		for(int i= 0; i < b1cont.length; i++) {
			System.out.println("" + b1cont[i]);
		}
		
		System.out.println(">>>>> B2 <<<<<<");
		b1.setBlockContent(d2, firstEx);
		b1cont = b1.getBlockContent();
		for(int i= 0; i < b1cont.length; i++) {
			System.out.println("" + b1cont[i]);
		}
		
		System.out.println(">>>>> B3 <<<<<<");
		b1.setBlockContent(d3, firstEx);
		b1cont = b1.getBlockContent();
		for(int i= 0; i < b1cont.length; i++) {
			System.out.println("" + b1cont[i]);
		}
		*/
	}

}
