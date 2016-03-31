package main;

import java.security.PublicKey;
import java.util.List;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import fs.FSLibrary;

/*
 * TESTE DE UTILIZAÇÃO DA FSLIBRARY
 * 
 * Existe apenas um cliente e é portador de um cartão de cidadão.
 * 
 */
public class App1 {

	public static void main(String[] args) throws Exception {
		
		FSLibrary client1 = new FSLibrary();
		
		/*O cliente insere pela primeira vez
		 *o seu cartão, e escreve no seu ficheiro. 
		 */
		client1.FS_init();
		String text = "Estou a fazer o projeto de SEC";
		client1.FS_write(0, text.length(), text.getBytes());   
		
		//O cliente lê uma parte do seu ficheiro
		List<PublicKey> keys = client1.FS_list();
		byte[] data_read = client1.FS_read(keys.get(0), 27, 3);
		System.out.println(new String(data_read));
		client1.FS_exit();
		
		//O cliente retira o seu cartão.
		
	}
	
}
