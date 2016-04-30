package fs;

import java.io.Serializable;

public class Data implements Serializable {
    
	private static final long serialVersionUID = 1L;
     
    private int dataSize;
    private int pos;
    private byte[] content;

    public Data(int dataSize, int pos, byte[] cont) {
        this.dataSize = dataSize;
        this.pos = pos;
        this.content = cont;
    }

    
    public Data(int dataSize, int pos) {
        this.dataSize = dataSize;
        this.pos = pos;
        this.content = null;
    }
    
    public int getDataSize() {
        return dataSize;
    }

    public int getDataPos() {
        return pos;
    }

    public byte[] getDataContent() {
        return content;
    }
}