package fs;

import java.nio.ByteBuffer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class FrontEnd {
	
	private PublicKey id;
	private KeyPair keys;
	private SecretKey secret;
	private ArrayList<BlockService> servers = new ArrayList<BlockService>();
	boolean hasWrote;
	
	private int f; //number of tolerated faults
	private int N; //number of replicas
	
	public FrontEnd(int faults) throws Exception {
		this.f = faults;
		this.N = 3*f + 1;
		
		//generate client's keys
		this.keys = generateKeys();
		this.id = keys.getPublic();
		this.secret = generateSecret();
		this.hasWrote = false;
		
		//connect to each server
		for(int i=0, port=8080; i<N; port++, i++) {
			BlockService server  = (BlockService) Naming.lookup("//localhost:" + port + "/FS");
			server.register(id, secret);
			servers.add(server);
		}
	}
	
	public PublicKey getId() {
		return this.id;
	}
	
	public byte[] read(PublicKey id, int pos, int nbytes, boolean readTime) throws InvalidKeyException, RemoteException, IllegalArgumentException, SignatureException, NoSuchAlgorithmException {  
		ArrayList<Future<Block>> replies = new ArrayList<Future<Block>>();
		ExecutorService exe = Executors.newCachedThreadPool();
		for(BlockService server : servers) {
			ServerRequest request = new ServerRequest(server, id);
			replies.add(exe.submit(request));
		}
		
		ArrayList<Block> blocks = new ArrayList<Block>();
		for(Future<Block> reply : replies) {
			try {		
				blocks.add(reply.get(2, TimeUnit.SECONDS));
			} catch (InterruptedException | ExecutionException e) {						
				e.printStackTrace();
			} catch (TimeoutException e) {
				//ignore
			}
		}
		exe.shutdown();
		
		int ackList = 0;
		ArrayList<Block> clean_blocks = new ArrayList<Block>();
		for(Block block : blocks) {
			if(verifySignature(block.getAuthData(), block.getSignature(), id)) {
				ackList++;
				clean_blocks.add(block);
			}
		}
		
		if(ackList > (N+f)/2) {
			if(readTime)
				//reads the most recent timestamp
				return getMaxTime(clean_blocks);
			//reads the freshest value
			byte[] bytes_read = getMaxVal(clean_blocks);
			byte[] val = new byte[pos + nbytes];
			for(int i=pos, j= 0; i < (pos + nbytes); i++, j++){
				val[j] = bytes_read[i];
			}
			
			return val;  
		}
		
		throw new RemoteException("ERROR 503: Service Unavailable");	
	}
	
	public void write(byte[] content, int pos, int size) throws Exception  {	
		int timestamp = 0;
		if(hasWrote) {
			timestamp = ByteBuffer.wrap(this.read(this.id, 0, 0, true)).getInt(); 		//get most updated timestamp value
		}		

		Data data = new Data(content, pos, size);
		
		ArrayList<Future<Block>> replies = new ArrayList<Future<Block>>();
    	ExecutorService exe = Executors.newCachedThreadPool();
		for(BlockService server : servers) {
	    	byte[] auth_data = makeAuthData(content, timestamp);
	    	byte[] signature = makeSignature(auth_data, this.keys.getPrivate());
	    	byte[] mac = makeMAC(content, secret);
	    	replies.add(exe.submit(new ServerRequest(server, data, timestamp, signature, this.id, mac)));
	    	timestamp++;
	    }
		
		int ackList = 0;
		for(Future<Block> reply : replies) {
			try {
				reply.get(2, TimeUnit.SECONDS);
				ackList++;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				//ignore
			}
		}
		exe.shutdown();
		
		if(ackList <= (N+f)/2) {
			throw new RemoteException("ERROR 503: Service Unavailable");
		}	
        this.hasWrote = true;
	}
	
	private KeyPair generateKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key_pair = keyGen.generateKeyPair();
		
		return key_pair;
	}
	
	private SecretKey generateSecret() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
	    keyGen.init(56);
	    SecretKey key = keyGen.generateKey();
	
	    return key;
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
	
	public byte[] makeMAC(byte[] bytes, SecretKey key) throws Exception {

        MessageDigest messageDigest = MessageDigest.getInstance("MD5"); 
        messageDigest.update(bytes);
        
        byte[] digest = messageDigest.digest();

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] mac = cipher.doFinal(digest);

        return mac;
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
