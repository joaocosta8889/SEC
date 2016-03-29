/**
 * Representacao do dado armazenado no bloco.
 * 
 * Um dado, no bloco, eh representado pela juncao de: conteudo, seu tamanho e a 
 * posicao em que foi escrita.
 * 
 */

package services;

/**
 *
 * @author joaocosta
 */
public class Data{
     
    private int dataSize;
    private int pos;
    private byte[] conteudo;

    public Data(int dataSize, int pos, byte[] conteudo) {
        this.dataSize = dataSize;
        this.pos = pos;
        this.conteudo = conteudo;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public String toString() {
        return "Data{" + "dataSize=" + dataSize + ", pos=" + pos + ", conteudo=" + new String( conteudo )+ '}';
    }
}