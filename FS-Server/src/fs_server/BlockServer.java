/*Block server
 * 
 */

package fs_server;

import fs_file.Data;
import fs_file.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaocosta
 */
public class BlockServer extends UnicastRemoteObject implements services.Service{
     
    private final int porta;
    private String    endereco;
    private Registry  registro;    // rmi registry para lookup do objecto remoto.
    private File file;

    public BlockServer() throws RemoteException{
        try{
            // Obter o endereco do host
            this.endereco = (InetAddress.getLocalHost()).toString();
        }catch(UnknownHostException e){
            System.out.println("Não pode obter o endereço INET. Falha: "+
                    e.getMessage());
        }
        
        //Porta por default
        this.porta = 3232;
        System.err.println("Endereco do servidor: "+endereco
                +", porta de serviço: "+porta);
        
        try{
            this.registro = LocateRegistry.createRegistry(porta);
            this.registro.rebind("FSService", this);
        }catch(RemoteException e){
            throw e;
        }
        this.file = new File();
    }

    public static void main(String args[]){
        try{
            BlockServer s = new BlockServer();
        }catch (RemoteException e) {
            System.out.println("Falha : "+e.getMessage());
            System.exit(1);
        }
     }

    @Override
    public byte[] get(byte[] id) throws RemoteException{
        byte[] conteudo = null;
        try {
            conteudo = this.file.get(id);
        } catch (Exception ex) {
            Logger.getLogger(BlockServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conteudo;
    }

    @Override
    public byte[] putK(Data data, Signature signature, PublicKey publicKey) throws RemoteException {
        return this.file.put_k(data, signature, publicKey);
    }

    @Override
    public byte[] putH(Data data) throws RemoteException {
        return this.file.put_h(data);
    }
    
    //Metodos de teste

    @Override
    public String getDataHora() throws RemoteException {
        SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return d.format(Calendar.getInstance().getTime());
    }
    
    package pt.ulisboa.tecnico.blockkeyserver;

    import java.io.IOException;
    import java.util.Arrays;


    public class Block {
    	
    	private byte[] id;
    	private byte memoria[];
        private int conta;
        
        public Block() {
            this(64);
        }
        
        public Block(int tamanho) {
            if (tamanho < 0) {
                throw new IllegalArgumentException(
                		"O tamanho do bloco eh negativo: " + tamanho);
            }
            memoria = new byte[tamanho];
        }

        private void verificaCapacidade(int capacidadeMinima) {
            // Estouro a capacidade especificada ?
            if (capacidadeMinima - memoria.length > 0)
            	aumenta(capacidadeMinima);
        }
        
        private void aumenta(int capacidadeMinima) {
            int capacidadeAntiga = memoria.length;
            int novaCapacidade = capacidadeAntiga << 1; //doubra a capacidade
            
            if (novaCapacidade - capacidadeMinima < 0){ //se nova capacidade for menor que a minima
            	novaCapacidade = capacidadeMinima; // entao considero a minima
            }
            
            if (novaCapacidade < 0) {
                if (capacidadeMinima < 0) // transbordou
                    throw new OutOfMemoryError();
                novaCapacidade = Integer.MAX_VALUE;
            }
            memoria = Arrays.copyOf(memoria, novaCapacidade);
        }
        
        public synchronized byte[] escreve(byte dado[], int posicao, int tamanho) 
        {
            if ((posicao < 0) || (tamanho < 0)) 
            {
                throw new IndexOutOfBoundsException();
            }
            verificaCapacidade(conta + tamanho);
            if(conta < posicao)
            {
            	for (int i = conta; i < posicao-1; i++)
            		memoria[i] = '0';//Depois substituirei por (byte)0
            }
            copiarDado(dado, 0, memoria, posicao - 1, tamanho);
            conta = tamanho + posicao - 1;
            //retorna o id do bloco que o hash do conteudo no bloco
            this.id = this.getBytes();
            return this.id;
        }
        
        public synchronized void reinicia() {
            conta = 0;
        }
        
        public void copiarDado(byte orig[], int inicioOrig, byte dest[], int inicioDest, int nBytes){
        	for(int i = inicioOrig, j = inicioDest, k = 0; k < nBytes; i++,j++, k++)
        		dest[j] = orig[i];
        }
        
        public synchronized byte[] getBytes(){
            return Arrays.copyOf(memoria, conta);
        }

        public synchronized int tamanho() {
            return conta;
        }
        
        public synchronized String getString() {
            return new String(memoria, 0, conta);
        }

        public void close() throws IOException {
        }
    }

}
