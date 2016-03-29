/*
 * Um bloco, no contexto da aplicacao, representa uma conjunto quadrado de bytes. 
 * Todos os blocos têm o mesmo tamanho definido, têm um conteudo em si.
 */

package fs_file;

/**
 *
 * @author joaocosta
 */
public class Block {
    //tamanho do bloco por default, mas pode ser redefinido ao criar o ficheiro.
    public static int BLOCK_SIZE = 1024*10; //10MB
    //espaço para armazenamento do conteudo do bloco 
    private Data dado;
    //identificar do bloco (que sera um hash do conteudo armazenado no bloco)
    private byte[] identificador;

    /**
     * Cria o bloco vazio com o tamanho default ou re-definido no ficheiro 
     */
    public Block() {
        byte[] content = new byte[BLOCK_SIZE];
        this.dado = new Data(content.length,1,content); 
        this.identificador = null;
    }
    
    public Block(Data data) {
        this.dado = data; 
        this.identificador = null;
    }

    /**
     * Retorna o contudo do bloco
     * 
     * @return conteudo
     * Eh o conteudo armazenado no bloco
     */
    public Data getDado() {
        return dado;
    }

    /**
     * Seta o conteudo no bloco
     * 
     * @param conteudo
     * conteudo a ser armazenado no pbloco
     */
    public void setDado(Data conteudo) {
        this.dado = conteudo;
    }

    /**
     * Retorna o identificador do bloco
     * 
     * @return identificador
     * O identificador do bloco (que eh um hash do conteudo)
     */
    public byte[] getIdentificador() {
        return identificador;
    }

    /**
     * Seta o identificador do bloco
     * 
     * @param identificador 
     * Identificador do bloco (que eh o hash do bloco)
     */
    public void setIdentificador(byte[] identificador) {
        this.identificador = identificador;
    }
}
