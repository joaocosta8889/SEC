package fs;

import java.nio.ByteBuffer;

public class Message {
	
	private Data data;
	private byte[] content;
	private int timestamp;
	
	
	public Message(Data data, int stamp){
		this.data = data;
		this.content = data.getDataContent();
		this.timestamp = stamp;
	}
	
	public byte[] getAuthData() {
		//convert timestamp into byte[]
		byte[] timeByte = ByteBuffer.allocate(4).putInt(this.timestamp).array();
		//append content with timestamp
		byte[] auth_data = new byte[this.content.length + timeByte.length];
		System.arraycopy(this.content, 0, auth_data, 0, this.content.length);
		System.arraycopy(timeByte, 0, auth_data, this.content.length, timeByte.length);
		
		return auth_data;	
	}

}
