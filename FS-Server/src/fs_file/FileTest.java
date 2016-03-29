/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fs_file;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author joaocosta
 */
public class FileTest {

    private static final String PUBLIC_KEY_FILE = "Public.key";
    private static final String PRIVATE_KEY_FILE = "Private.key";

    public static void main(String[] args) {
        File file = new File();
        try {
            System.out.println("------------GENERATE PUBLIC and PRIVATE KEY--------------");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); //1024 used for normal securities
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publickey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            System.out.println("------------PULLING OUT PARAMETERS WHICH MAKES KEYPAIR--------------");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publickey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            System.out.println("------------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES--------------");
            FileTest rsaObj = new FileTest();

            rsaObj.saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());
            rsaObj.saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());

            file.put_k(null,null,publickey);
            
            byte[] dado1 = "Entrada de teste final".getBytes();
            Data dado = new Data(dado1.length, 1, dado1);
            file.put_k(dado,Security.makeSignature(dado1, privateKey),publickey);
            
            byte[] dad2 = "Amar eh um dom de Deus...".getBytes();
            Data dado2 = new Data(dad2.length, 1, dad2);
            file.put_h(dado2);
            
            byte[] dad3 = "Amo a minha familia...".getBytes();
            Data dado3 = new Data(dad3.length, 1, dad3);
            file.put_h(dado3);
            
            System.out.println("Size : "+file.getBlocos().size());
            
            for(Block b : file.getBlocos()){
                System.out.println(new String(b.getDado().getConteudo()));
            }
            
        } catch (Exception ex) {
            System.out.println("Falha : " + ex.getMessage());
        }
    }

    private byte[] encryptData(String data) throws IOException {
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

    private void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            System.out.println("Generating " + fileName + "....");
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(mod);
            oos.writeObject(exp);
            System.out.println(fileName + " generated successfully");
        } catch (Exception ex) {
            System.out.println("Falha SAVE: " + ex.getMessage());
        } finally {
            if (oos != null) {
                oos.close();
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    private static PublicKey readPublicKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new java.io.File(fileName));
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

    public static PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new java.io.File(fileName));
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

    private void decryptData(byte[] data) throws IOException {
        System.out.println("---------------DECRYPTION STARTED----------------");
        byte[] decryptedData = null;
        try {
            PrivateKey privateKey = readPrivateKeyFromFile(this.PRIVATE_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(data);
            System.out.println("Decrypted data: " + new String(decryptedData));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Falha :" + ex.getMessage());
        }
    }
}
