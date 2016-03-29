/**
 * Representacao do dado armazenado no bloco.
 * 
 * Um dado, no bloco, eh representado pela juncao de: conteudo, seu tamanho e a 
 * posicao em que foi escrita.
 * 
 */

package fs_file;

/**
 *
 * @author joaocosta
 */
public class Data implements services.Data{
     
    private int dataSize;
    private int pos;
    private byte[] conteudo;

    public Data(int dataSize, int pos, byte[] conteudo) {
        this.dataSize = dataSize;
        this.pos = pos;
        this.conteudo = conteudo;
    }

    @Override
    public int getDataSize() {
        return dataSize;
    }

    @Override
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public byte[] getConteudo() {
        return conteudo;
    }

    @Override
    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return "Data{" + "dataSize=" + dataSize + ", pos=" + pos + ", conteudo=" + new String( conteudo )+ '}';
    }
}