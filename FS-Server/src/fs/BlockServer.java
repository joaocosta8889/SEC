package fs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.HashMap;

public class BlockServer extends UnicastRemoteObject implements BlockService {

	private static final long serialVersionUID = 1L;
	
	private HashMap<PublicKey, Block> blocks = new HashMap<PublicKey, Block>();
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
		int N = 3; //replicas
		for(int i=0, port=8080; i<N; port++, i++) {
			if(i == 2)
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
			return null;
		}
		if(blocks.containsKey(id)) {
			Block block = blocks.get(id);
			return block;
		} else {
			throw new IllegalArgumentException("SERVER ERROR: Invalid ID");
		}
	}

	@Override
	public boolean put_k(Data data, int timestamp, byte[] signature, PublicKey id) throws RemoteException {
		if(crashed) {
			return false;
		}
		if(blocks.containsKey(id)) {
				blocks.get(id).setContent(data);
				blocks.get(id).setTimestamp(timestamp);
				blocks.get(id).setSignature(signature);
		} else {
			Block new_block = new Block(data, timestamp, signature);
			blocks.put(id, new_block);
		}	
		
		System.out.println("[Server " + port + "] Content: " + 
				new String(blocks.get(id).getContent()) + " Time: " + blocks.get(id).getTimestamp());
		
		return true;
	}
	
}
