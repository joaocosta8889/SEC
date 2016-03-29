/*
 * ClientLibrary for the client 
 */
package library;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import services.Data;
import services.Service;

/**
 *
 * @author joaocosta
 */
public class ClientLibrary implements Library {

    //Nome e extensao dos ficheiros que armazenarao as cahves 
    private static final String PUBLIC_KEY_FILE = "Public.key";
    private static final String PRIVATE_KEY_FILE = "Private.key";

    //Aspectos remoto
    private Service servico;
    private Registry registro;

    public ClientLibrary(String enderecoDoDervidor, int portaDoServidor) {
        regista(enderecoDoDervidor, portaDoServidor);
    }

    public ClientLibrary() {
        try {
            this.registro = LocateRegistry.getRegistry("MBP-de-Joao.home", 3232);
            this.servico = (Service) (this.registro.lookup("FSService"));
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] fsInit() {
        byte[] id = null;
        try {
            KeyPair parDeChave = gerarParDeChave();
            id = this.servico.putK(null, null, parDeChave.getPublic());
        } catch (RemoteException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    /**
     * Escreve o conteudo contido em “contents”, de tamanho “size”, na posicao “pos”
     * do ficheiro associado ao cliente que o invoca. Se o tamanho for menor que pos+size
     * entao precisa ser aumentado (no bloco posterior). Se o tamanho do ficheiro for menor 
     * que "pos" entao precisa ser aumentado zero.
     *
     * @param pos Posicao do bloco em que se quer escrever
     *
     * @param size tamanho do conteudo a escrever no bloco
     *
     * @param contents O conteudo a escrever no bloco.
     */
    @Override
    public void fsWrite(int pos, int size, byte[] contents) {

        Data dado = new Data(size, pos, contents);
        try {
            this.servico.putH(dado);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] fsRead(byte[] id, int pos, int size, byte[] contents) {
        byte[] resultado = null;
        try {
            resultado = this.servico.get(id);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }
    
     public String getDataHora() {
        String tempo = null;
        try {
            tempo = this.servico.getDataHora();
        } catch (RemoteException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
         return tempo;
     }
    

    // Metodos auxiliares
    /**
     * Method that will called by the method fsInit() for generate the key and
     * save in client-side
     */
    private void regista(String enderecoDoDervidor, int portaDoServidor) {
        try {
            // Obter o registo do servico
            this.registro = LocateRegistry.getRegistry(
                    enderecoDoDervidor, portaDoServidor);
            // look up do objecto remoto
            this.servico = (Service) (this.registro.lookup("FSService"));

        } catch (RemoteException | NotBoundException e) {
            System.out.println("Falha ao registar o servico: " + e.getMessage());
        }
    }

    private KeyPair gerarParDeChave() {
        KeyPair keyPair = null;

        try {
            //GERAR CHAVE PUBLICA E PRIVADA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publickey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //RETIRAR OS PARAMETRO QUE FORMA O PAR DE CHAVES
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publickey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            
            guardaChaves(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());
            guardaChaves(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());
            
            System.out.println("Chaves geradas e armazenadas com sucesso...");

        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }

        return keyPair;
    }

    private void guardaChaves(String nomeDoFicheiro, BigInteger mod, BigInteger exp) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(nomeDoFicheiro);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(mod);
            oos.writeObject(exp);
        } catch (IOException ex) {
            System.out.println("Falha ao guardar: " + ex.getMessage());
        } finally {
            if (oos != null) {
                oos.close();
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private PublicKey readPublicKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Public Key
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | ClassNotFoundException ex) {
            System.out.println("Falha : " + ex.getMessage());
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }

    public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Private Key
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);
            return privateKey;
        } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println("Falha : " + ex.getMessage());
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }

    private byte[] cifrar(String data) throws IOException {
        System.out.println("---------ENCRYPTION STARTED---------");
        System.out.println("Data Before Encryption :" + data);
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            PublicKey pubKey = readPublicKeyFromFile(this.PUBLIC_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
            System.out.println("Encrypted Data : " + new String(encryptedData));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Falha :" + ex.getMessage());
        }
        System.out.println("-------------ENCRYPTION COMPLETED-------------");
        return encryptedData;
    }

    private void decifrar(byte[] data) throws IOException {
        System.out.println("---------------DECRYPTION STARTED----------------");
        byte[] decryptedData = null;
        try {
            PrivateKey privateKey = readPrivateKeyFromFile(this.PRIVATE_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(data);
            System.out.println("Decrypted data: " + new String(decryptedData));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(ClientLibrary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Realiza a assinatura do dado combinando o algoritmo MD5 com RSA
     *
     * @param data Dado a ser assinado
     *
     * @param privKey chave privada do proprietario do dado
     *
     * @return sig retorna a assinatura
     *
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static Signature makeSignature(byte[] data, PrivateKey privKey) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initSign(privKey);
        sig.update(data);
        return sig;
    }
}