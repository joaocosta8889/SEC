package fs;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import javax.crypto.*;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;


public class FSLibrary {

	private BlockService block_service;
	private KeyPair user_keys; 
	
	public void FS_init() throws NoSuchAlgorithmException, MalformedURLException, NotBoundException, RemoteException, CertificateException {	
		
		System.out.println("[ Inicializing Library... ]");
		
		this.generateKeys();
		
		this.block_service =  (BlockService) Naming.lookup("//localhost:8081/FS"); 
		
		boolean ack = block_service.storePubKey(user_keys.getPublic());
		
		if(ack) {
			System.out.println("Welcome new client!");
			System.out.println("Your public key is now stored at the server, and available to every reader.");
		} 
			
		System.out.println("[ Inicialization Complete! ]");	
	}
	
	public void FS_write(int pos, int size, byte[] contents) throws RemoteException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException, SignatureException {
		
		System.out.println("[ Writing Data...]");
		Data cont = new Data(size, pos, contents);
		
		byte[] signature = makeSignature(contents);
		this.block_service.put_k(cont, signature, user_keys.getPublic());
		
		System.out.println("[ Writing Complete ]");
		
	}
	
	public byte[] FS_read(PublicKey id, int pos, int nbytes) throws RemoteException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IllegalArgumentException, NoSuchAlgorithmException, SignatureException {
		
		byte[] bytes_read = block_service.get(id);
		
		byte[] out = new byte[pos+nbytes];
		for(int i=pos, j= 0; i < (pos + nbytes); i++, j++){
			out[j] = bytes_read[i];
		}
				 
		return out;	
	}
	
	public List<PublicKey> FS_list() throws RemoteException {
		return  block_service.readPubKeys();
	}
	
	
	private byte[] makeSignature(byte[] data) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		 Signature sig = Signature.getInstance("Sha1WithRSA");
	     sig.initSign(user_keys.getPrivate());
	     sig.update(data);
	     byte[] new_signature = sig.sign();
	     
	     return new_signature;
	}
	
	private void generateKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key_pair = keyGen.generateKeyPair();
		
		user_keys = key_pair;
	}
	
	public PublicKey getId(){
		return user_keys.getPublic();
	}
	
}
