/**
 * Um ficheiro, no conetxto da aplicação, é um conjunto de blocos identificados.
 * O ficheiro tem um identificador, a chave publica do proprietario e um
 * conjunto de bloco de tamanho ilimitado.
 */
package fs_file;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaocosta
 */
public class File {

    //identificador do ficheiro (que será o hash da chave pública do cliente)
    private byte[] identificador;
    //conjunto de bloco associado ao ficheiro
    private List<Block> blocos;
    //chave publica do proprietario do ficheiro
    PublicKey chavePublica;
    //unidade basica
    private static final int BYTE = 1024;

    /**
     * Inicializa o ficheiro especificando o tamanho de cada bloco
     *
     * @param tamanhoDoBloco Valor para re-definir o tamanho do ficheiro. Este
     * valor eh dado em MB.
     *
     * @exception Exception O tamanho do bloco de ser superior a 10MB.
     *
     * @throws Exception O tamanho do bloco de ser superior a 10MB.
     */
    public File(int tamanhoDoBloco) throws Exception {
        int tamanhoEmByte = tamanhoDoBloco * BYTE;

        if (tamanhoEmByte >= 10240) {
            Block.BLOCK_SIZE = tamanhoEmByte;
        } else {
            throw new Exception("O tamanho do bloco de ser superior a 10MB.");
        }

        this.blocos = new ArrayList<Block>();
        this.identificador = null;
    }

    /**
     * Inicializa o ficheiro com as configuracoes por default.
     */
    public File() {
        this.blocos = new ArrayList<Block>(1);
        this.identificador = null;
    }

    public List<Block> getBlocos() {
        return this.blocos;
    }
    
    /**
     * A implementação deve garantir que a integridade dos dados retornados é
     * preservada, i.e., o conteúdo do bloco de dados são os mesmos que foram
     * dados como entrada para a operação de "colocar". Um erro deve ser
     * devolvido em condições inesperadas, ou seja, se o servidor não retornou
     * um bloco com garantias de integridade.
     *
     * @param id Identificador do bloco
     *
     * @return conteudo retorna o conteudo do bloco que estava armazenado com o
     * identificador id
     * @throws java.security.NoSuchAlgorithmException
     */
    public byte[] get(byte[] id) throws NoSuchAlgorithmException, Exception {
        if (this.blocos.isEmpty()) {
            return null;
        }

        Block bloco = this.getBlock(id);

        if (Arrays.equals(bloco.getIdentificador(),
                Security.makeHash(bloco.getDado().getConteudo()))) {
            return bloco.getDado().getConteudo();
        } else {
            throw new Exception("O conteudo do bloco foi modificado");
        }
    }

    /**
     * Dado um id retorna o respectivo block
     *
     * @param id identificador de um determinado block
     *
     * @return o block que tem o id
     */
    private Block getBlock(byte[] id) {
        for (Block bloco : this.blocos) {
            if (Arrays.equals(bloco.getIdentificador(), id)) {
                return bloco;
            }
        }
        return null;
    }

    /**
     * Armazena o bloco de chave publica com o conteudo contido em 'data',
     * assinado com a assinatura contida em 'signature', que pode ser avalidada
     * utilizando a chave publica contida em 'publicKey'.
     *
     * @param data primeiro conteudo a ser inserido, que por defeito sera
     * armazenado no primeiro bloco e na primeira posicao.
     *
     * @param signature A assinaturo do cliente sobre o documento
     *
     * @param publicKey chave publica do cliente
     *
     * @return id Retorna o identificador do ficheiro. Este identificador eh o
     * hash da chave publica do cliente
     */
    public byte[] put_k(Data data, Signature signature, PublicKey publicKey) {
        byte[] id = null;
        try {
            byte[] pubicKeyEncoded = publicKey.getEncoded();
            if (data != null) {
                //Este dado foi de facto assinato pelo cliente
                if (Security.isValidaAssinatura(data.getConteudo(), signature, publicKey)) {
                    //se ainda nao tem identificador
                    if (this.identificador == null) {
                        //seto o identificador do ficheiro
                        id = (this.identificador = Security.makeHash(pubicKeyEncoded));
                        //guardo a chave publica
                        this.chavePublica = publicKey;
                    }
                    //armazena o dado no bloco
                    put_h(data);
                }
            } else {
                id = (this.identificador = Security.makeHash(pubicKeyEncoded));
                this.chavePublica = publicKey;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    /**
     * Armazena um bloco de conteúdo hash com os conteúdos contidos 'data'.
     *
     * @param data conteudo a ser armazenado no bloco
     *
     * @return id O ID associado com o bloco de dados recém-criado que é
     * retornado é um hash do bloco.
     */
    public byte[] put_h(Data data) {

        int blockSize = Block.BLOCK_SIZE, indiceCumulativo = 0;
        int tamanho = data.getDataSize() + data.getPos();
        int bytesRestante = tamanho % blockSize;
        int blockNumber = ((tamanho / blockSize) + (((bytesRestante) != 0) ? 1 : 0));

        Block novoBlock = null;
        byte[] id = null;

        for (int i = 0, j = 0; (i < blockNumber) && (j < data.getConteudo().length); i++) {

            novoBlock = new Block();

            for (int escrever = 0;
                    ((escrever < blockSize) && (indiceCumulativo < tamanho)); 
                                                                        escrever++) {

                if (indiceCumulativo >= data.getPos()) {

                    novoBlock.getDado().getConteudo()[escrever] = data.getConteudo()[j++];

                }  // else que havia eh desnecessario

                indiceCumulativo++;

            }

            try {
                //Seta os restantes elementos 
                id = Security.makeHash(novoBlock.getDado().getConteudo());
                novoBlock.getDado().setDataSize(novoBlock.getDado().getConteudo().length);
                novoBlock.getDado().setPos(i);
                novoBlock.setIdentificador(id);

            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Adiciona o novo bloco apenas se corresponde a posicao valida para tal  
            if (indiceCumulativo >= data.getPos()) {
                blocos.add(novoBlock);
                
                //A linha a seguir deve ser eliminada, esta apenas para teste
                System.out.println(novoBlock.getIdentificador());
            }
        }

        //Para conteudo que ocupa mais de um bloco tera constragimento.
        return id;

    }
}
