package fs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class BlockServer extends UnicastRemoteObject implements BlockService {

	private static final long serialVersionUID = 1L;
	
	private HashMap<PublicKey, Block> blocks = new HashMap<PublicKey, Block>();
	private HashMap<PublicKey, SecretKey> secrets = new HashMap<PublicKey, SecretKey>();
	private int port;
	private boolean crashed = false;
	private boolean hasFault = false; //byzantine fault
	
	protected BlockServer(int port, boolean crashed) throws RemoteException, MalformedURLException {
		super();
		this.port = port;
		LocateRegistry.createRegistry(port);
		Naming.rebind("//localhost:" + port + "/FS", this);
		
		this.crashed = crashed;
		if(crashed) {
			System.out.println("[Server " + port + "] Not Functioning!");
		} else {
			System.out.println("[Server " + port + "] Running...");
		}	
	}

	public static void main(String[] args) throws NumberFormatException, RemoteException, MalformedURLException {
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Number of tolerated faults: ");
		while (!sc.hasNextInt()) 
			sc.next();
		int f = sc.nextInt();
				
		int N = 3*f + 1;
		
		for(int i=0, port=8080; i<N; port++, i++) {
			if(port == 8082)
				new BlockServer(port, true);
			else
				new BlockServer(port, false);
		}
		
	}
	
	public int getPort() {
		return port;
	}

	@Override
	public Block get(PublicKey id) throws RemoteException {	
		if(crashed) {
			while(true){}
		}
		if(blocks.containsKey(id)) {
			Block block = blocks.get(id);
			return block;
		} else {
			throw new IllegalArgumentException("SERVER ERROR: Invalid ID");
		}
	}

	@Override
	public void put_k(Data data, int timestamp, byte[] signature, PublicKey id, byte[] mac) throws RemoteException {
		if(crashed) {
			while(true);
		}
		
		try {
			if(!verifyMAC(mac, data.getContent(), secrets.get(id))) {
				throw new RemoteException("Authentication Failed!");
			}
		} catch (Exception e) {e.printStackTrace();}	
		
		if(blocks.containsKey(id)) {
				blocks.get(id).setContent(data);
				blocks.get(id).setTimestamp(timestamp);
				blocks.get(id).setSignature(signature);
		} else {
			Block new_block = new Block(timestamp, signature, id);
			new_block.setContent(data);
			blocks.put(id, new_block);
		}
	
		
		System.out.println("[Server " + port + "] Content: " + 
				new String(blocks.get(id).getContent()) + " Time: " + blocks.get(id).getTimestamp());
		
	}
	
	@Override
	public void register(PublicKey id, SecretKey secret) throws RemoteException {
		secrets.put(id, secret);
	}
	
    public static boolean verifyMAC(byte[] mac, byte[] bytes, SecretKey key) throws Exception {
    	
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		
		messageDigest.update(bytes);
		byte[] digest = messageDigest.digest();
		
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decipheredDigest = cipher.doFinal(mac);
		
		if (digest.length != decipheredDigest.length)
			return false;
		
		for (int i=0; i < digest.length; i++) {
			if (digest[i] != decipheredDigest[i])
				return false;
		}
		return true;
	
	}
	
}
