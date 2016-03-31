package main;

import java.security.PublicKey;
import java.util.List;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import fs.FSLibrary;

/*
 * TESTE DE UTILIZAÇÃO DA FSLIBRARY
 * 
 * Cada cliente é portador do seu proprio cartão de cidadão.
 * 
 */
public class App2 {

	public static void main(String[] args) throws Exception {
		
		FSLibrary client1 = new FSLibrary();
		FSLibrary client2 = new FSLibrary();
		
		/*O cliente 1 insere o seu cartão, 
		 *e escreve no seu ficheiro. 
		 */
		client1.FS_init();
		String text = "Estou a fazer o projeto de SEC";
		client1.FS_write(0, text.length(), text.getBytes());
		client1.FS_exit();   
		
		//O cliente 1 retira o seu cartão.
		
		/*O cliente 2 insere o seu cartão (tem de ser rapido),
		 * e lê o conteudo do ficheiro do cliente 1;
		 */
		Thread.sleep(10000);  
		System.out.println("--------------------------------------------------------------------");
		
		client2.FS_init();
		List<PublicKey> keys = client2.FS_list();
		byte[] data_read = client2.FS_read(keys.get(0), 0, text.length());
		System.out.println(new String(data_read));
		client2.FS_exit();
		
		//O cliente 2 retira o seu cartão.
		
	}
	
}
