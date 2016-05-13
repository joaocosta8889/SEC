package fs;

import java.security.PublicKey;
import java.util.concurrent.Callable;

public class ServerRequest implements Callable<Block> {
	
	private BlockService server;
	private Data data;
	private int timestamp;
	private byte[] signature;
	private PublicKey id;	
	private byte[] mac;
	private boolean isRead;
 	

	public ServerRequest(BlockService server, Data data, int timestamp, byte[] signature, PublicKey id,  byte[] mac) {
		this.server = server;
		this.data = data;
		this.timestamp = timestamp;
		this.signature = signature;
		this.id = id;
		this.mac = mac;
		this.isRead = false;
	}
	
	public ServerRequest(BlockService server, PublicKey id) {
		this.server = server;
		this.id = id;
		this.isRead = true;
	}

	@Override
	public Block call() throws Exception {	
		if(isRead) {
			return server.get(id);
		}
		server.put_k(data, timestamp, signature, id, mac);
		return null;
	}

}
