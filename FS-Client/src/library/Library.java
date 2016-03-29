
package library;

/**
 * @author joaocosta
 */
public interface Library {
    public byte[] fsInit();
    public void fsWrite(int pos, int size, byte[] contents);
    public byte[] fsRead(byte[] id, int pos, int size, byte[] contents);
}