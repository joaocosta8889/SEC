package fs;

import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;


public class FrontEnd {
	
	private PublicKey id;
	private KeyPair keys;
	private ArrayList<BlockService> servers = new ArrayList<BlockService>();
	private boolean hasWrote;
	
	private static final int N = 3; //number of replicas
	
	public FrontEnd(String[] ports) throws MalformedURLException, RemoteException, NotBoundException, NoSuchAlgorithmException {
		this.keys = generateKeys();
		this.id = keys.getPublic();
		
		//connect to each server
		for(String port : ports) {
			BlockService server  = (BlockService) Naming.lookup("//localhost:" + port + "/FS");
			servers.add(server);
		}
	}
	
	public PublicKey getId() {
		return this.id;
	}
	
	public byte[] read(PublicKey id, int pos, int nbytes, boolean readTime) throws InvalidKeyException, RemoteException, IllegalArgumentException, SignatureException, NoSuchAlgorithmException {  
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

		if(f < (N+f)/2) {
			if(readTime) {
				//reads the most recent timestamp
				return getMaxTime(readList);
			}
			//reads the freshest value
			byte[] bytes_read = getMaxVal(readList);
			byte[] out = new byte[pos+nbytes];
			for(int i=pos, j= 0; i < (pos + nbytes); i++, j++){
				out[j] = bytes_read[i];
			}
			
			return out;  
		} else {
			System.out.println("ERROR 503: service unavailable");
			return null;
		}	
	}
	
	public void write(byte[] content, int pos, int size) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, RemoteException  {
		
		int f = 0; //number of faults
		
		//get the most recent timestamp 
		int timestamp = 0;
		if(hasWrote) {
			timestamp = ByteBuffer.wrap(this.read(id, 0, 0, true)).getInt();
		}
		
		//write to each server replica
		Data data = new Data(size, pos, content);
        for(BlockService server : servers) {
        	byte[] auth_data = makeAuthData(content, timestamp);
        	byte[] signature = makeSignature(auth_data, this.keys.getPrivate());
        	Boolean ack = server.put_k(data, timestamp, signature, id);
        	if(ack) 
        		timestamp++;
        	else 
        		f++;   
        }
        
        if(f > (N+f)/2) {
        	System.out.println("WARNING: Writing may be incomplete");
        }
        
        this.hasWrote = true;
	}
	
	private KeyPair generateKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key_pair = keyGen.generateKeyPair();
		
		return key_pair;
	}
	
	private byte[] makeSignature(byte[] data, PrivateKey private_key) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		 Signature sig = Signature.getInstance("Sha1WithRSA");
	     sig.initSign(private_key);
	     sig.update(data);
	     byte[] new_signature = sig.sign();
	     
	     return new_signature;
	}
	
	private boolean verifySignature(byte[] data, byte[] signature, PublicKey public_key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withRSA");
       sig.initVerify(public_key);
       sig.update(data);
       boolean result = sig.verify(signature);
       
       return result;
	}
	
	private byte[] makeAuthData(byte[] content, int timestamp) {
		//convert timestamp into byte[]
		byte[] timeByte = ByteBuffer.allocate(4).putInt(timestamp).array();
		
		//append content with timestamp
		byte[] auth_data = new byte[content.length + timeByte.length];
		System.arraycopy(content, 0, auth_data, 0, content.length);
		System.arraycopy(timeByte, 0, auth_data, content.length, timeByte.length);
				
	    return auth_data;	
	}
	
	private byte[] getMaxVal(ArrayList<Block> list) {
		byte[] max_val = null;
		int max_time = 0;
		for(Block b : list) {
			if(b.getTimestamp() > max_time) {
				max_time = b.getTimestamp();
				max_val = b.getContent();
			}
		}	
		return max_val;
	}
	
	private byte[] getMaxTime(ArrayList<Block> list) {
		int max_time = 0;
		for(Block b : list) {
			if(b.getTimestamp() > max_time) {
				max_time = b.getTimestamp();
			}
		}	
		
		return ByteBuffer.allocate(4).putInt(max_time).array();
	}
	
	
}
