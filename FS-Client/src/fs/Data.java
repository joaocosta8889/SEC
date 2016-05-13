
package fs;

import java.io.Serializable;

public class Data implements Serializable {
     
	private static final long serialVersionUID = 1L;
	
	private int size;
    private int pos;
    private byte[] content;

    public Data(byte[] cont, int pos, int size) {
        this.size = size;
        this.pos = pos;
        this.content = cont;
    }
    
    public Data(int size, int pos) {
        this.size = size;
        this.pos = pos;
        this.content = null;
    }

    public int getSize() {
        return size;
    }

    public int getPos() {
        return pos;
    }

    public byte[] getContent() {
        return content;
    }
}