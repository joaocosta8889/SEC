package fs;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BlockServer  extends UnicastRemoteObject implements BlockService {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<PublicKey, Block> blocks = new HashMap<PublicKey, Block>();
	
	//private ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();

	public static void main(String[] args) throws RemoteException {
		BlockService bs = new BlockServer();  
		Registry reg = LocateRegistry.createRegistry(8081);
		reg.rebind("FS", bs);	
		
		System.out.println("Running...");     
	}	
	
	protected BlockServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public byte[] get(PublicKey id) throws RemoteException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		 
		byte[] out = null;
		if(blocks.containsKey(id)) {
			
			Block block = blocks.get(id);
			byte[] blockCont = block.getBlockContent();
			byte[] signature = block.getSignature();	
			
			if(!verifySignature(blockCont, signature, id)) {
				throw new SignatureException("Security ERROR: integrity problem, data is corrupted!");
			}else{
				out = blockCont;
			}
		} else {
			throw new IllegalArgumentException("SERVER ERROR 404: Data Not Found");
		}
		return out;
	}
	
	@Override
	public PublicKey put_k(Data data, byte[] signature, PublicKey id) throws RemoteException, SignatureException, InvalidKeyException, NoSuchAlgorithmException{

		if(verifySignature(data.getDataContent(), signature, id)) {	

			if(blocks.containsKey(id)) {
				blocks.get(id).setBlockContent(data, signature);
			} else {
				throw new IllegalArgumentException("Publick Key Not Found");
			}
		} else {
			throw new SignatureException("Security ERROR: invalid signature, authentication failed!");
		}
		
		return id;
	}
	
	@Override
	public byte[] put_h(Data data) throws RemoteException, NoSuchAlgorithmException {
		return null;
	}
	
	
	@Override
	public boolean storePubKey(X509Certificate cert) throws RemoteException {

		PublicKey id = cert.getPublicKey();
		// se certificado ja existe
		if (blocks.containsKey(id)){
			return false;
		}
		Block newBlock = new Block();
		blocks.put(id, newBlock);

		return true;
	}

	@Override
	public List<PublicKey> readPubKeys() throws RemoteException {
		
		List<PublicKey> values = new ArrayList<PublicKey>();
		
		for(PublicKey pk : blocks.keySet()){
			values.add(pk);
			}
		
		return values;
	}
	
	private boolean verifySignature(byte[] data, byte[] signature, PublicKey public_key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initVerify(public_key);
        sig.update(data);
        boolean result = sig.verify(signature);
        
        return result;
	}
	
	private byte[] generateId(PublicKey public_key) throws NoSuchAlgorithmException {
		byte[] keyBytes = public_key.getEncoded();
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(keyBytes);
		byte[] new_id = messageDigest.digest();
		
		return new_id;
	}


}
