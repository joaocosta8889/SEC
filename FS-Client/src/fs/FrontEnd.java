package fs;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import quorum.Block;
import quorum.BlockService;

public class FrontEnd {

	// PARA UM SERVER
//	private BlockService block_service;
	private BlockService[] block_service;
	private KeyPair user_keys; 
	
	private static final int NR_FAULTS = 3;
	
	public FrontEnd(KeyPair user_keys) {
		block_service = new BlockService[NR_FAULTS];
		
		try {
	
		
			String localhost = "//localhost";
			for(int i = 0, j=8081; i < NR_FAULTS; i++, j++){
				String lookUpForServer = localhost.concat(":" + j + "/FS"); 
				block_service[i] =  (BlockService) Naming.lookup(lookUpForServer); 
			}
			
			// PARA UM SERVER
//			block_service =  (BlockService) Naming.lookup("//localhost:8081/FS"); 
			
			
			boolean ack[] = {false, false, false};
			int out = 0;
			
			// PROTOCOLO
			// protocolo aqui tambem no caso de receber menos de tres ack
			while(out < NR_FAULTS){
				
				for(int i = 0; i < NR_FAULTS; i++)
					ack[i] = block_service[i].storePubKey(user_keys.getPublic());

			
				for(int i = 0; i < NR_FAULTS; i++){
					if (ack[i])
						out++;
				}
			}
			
			// PARA UM SERVER
//			boolean ack = block_service.storePubKey(user_keys.getPublic());
			
			
			} catch (RemoteException | MalformedURLException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		// PROTOCOLO
		// se ja recebeu nr suficiente de ack
		// alterar if debaixo
		if(true) {
			System.out.println("Welcome new client!");
			System.out.println("Your public key is now stored at the server, and available to every reader.");
		} 
			
		System.out.println("[ Inicialization Complete! ]");	
	}

	
	public void put_k(Data dataĈontent, byte[] signature, PublicKey publicKey) throws RemoteException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException, SignatureException {
		
		
		for(int i = 0; i < NR_FAULTS; i++)
			block_service[i].put_k(dataĈontent, signature, publicKey);
		
		// PARA UM SERVER
//		block_service.put_k(dataĈontent, signature, publicKey);
	}
	
	public byte[] get(PublicKey id, boolean readTime) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, RemoteException {  
		
		int f = 0; //number of faults
		
		//read from each server replica 
		ArrayList<Block> readList = new ArrayList<Block>();
		for(BlockService server : servers) {
			Block block = server.get(id);
			if(block != null) {
				if(verifySignature(block.getAuthData(), block.getSignature(), id)) {
					readList.add(block);				
				} else
					f++;
			} else 
				f++;
		}

		//choose the freshest reply
		if(f < (N+f)/2) {
			if(readTime) {
				return getMaxTime(readList);
			}
			return getMaxVal(readList);
		} else {
			System.out.println("ERROR 503: service unavailable");
			return null;
		}	
	}
	
	public List<PublicKey> readPubKeys() throws RemoteException {
		
		ArrayList<List<PublicKey>> keys = new ArrayList<List<PublicKey>>();
		
		for(int i = 0; i < NR_FAULTS; i++)
			keys.add(block_service[i].readPubKeys());
		
		// PROTOCOLO
		// alterar return debaixo
		return keys.get(0);
		
		// PARA UM SERVER
//		return  block_service.readPubKeys();
	}
}
