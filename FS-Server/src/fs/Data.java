package fs;

public class Data{
     
    private int dataSize;
    private int pos;
    private byte[] content;

    public Data(int dataSize, int pos, byte[] cont) {
        this.dataSize = dataSize;
        this.pos = pos;
        this.content = cont;
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