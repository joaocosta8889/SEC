package fs;

import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BlockServer  extends UnicastRemoteObject implements BlockService {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<PublicKey, Block> blocks = new HashMap<PublicKey, Block>();
	private List<PublicKey> keys = new ArrayList<PublicKey>();

	
	protected BlockServer(int port) throws RemoteException {
		Registry reg = LocateRegistry.createRegistry(port);
		reg.rebind("FS", this);	
		
		System.out.println("Running " + port + " ...");   
	}
	
	@Override
	public byte[] get(PublicKey id) throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		
		if(blocks.containsKey(id)) {
			byte[] data = blocks.get(id).getBlockContent();
			byte[] signature = blocks.get(id).getSignature();	
					
			boolean signed = verifySignature(data, signature, id);
			if(signed) {
				return data;
			} else {
				throw new SignatureException("Security ERROR: integrity problem, data is corrupted!");
			}
		} else {
			throw new IllegalArgumentException("SERVER ERROR: Invalid ID");
		}
	}
	
	@Override
	public void put_k(Data data, byte[] signature, PublicKey id) throws RemoteException, SignatureException, InvalidKeyException, NoSuchAlgorithmException{

		boolean signed = verifySignature(data.getDataContent(), signature, id);
		
		if(signed) {
			
			if(blocks.containsKey(id)) {
				blocks.get(id).setBlockContent(data, signature);
			} else {
				Block new_block = new Block();
				blocks.put(id, new_block);
				blocks.get(id).setBlockContent(data, signature);
			}
		} else {
			throw new SignatureException("[ Security ERROR: Authentication Failed! ]");
		}
	}
	
	@Override
	public boolean storePubKey(PublicKey pubkey) throws RemoteException {

		if(!keys.contains(pubkey)) {
			this.keys.add(pubkey);
			return true;
		}

		return false;
	} 

	@Override
	public List<PublicKey> readPubKeys() throws RemoteException {
		
		return keys;
	}
	
	private boolean verifySignature(byte[] data, byte[] signature, PublicKey public_key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(public_key);
        sig.update(data);
        boolean result = sig.verify(signature);
        
        return result;
	}

}
