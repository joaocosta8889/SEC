/*
 * Define a seguranca do dados> autenticacao e nao-repudio
 */
package fs_file;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author joaocosta
 */
public class Security {

    /**
     * Resume o conjunto de bytes passado por parametro utilizando o algoritmo
     * MD5
     * 
     * @param conteudo
     * O conteudo a ser resumido
     * 
     * @return hash
     * Retorna o texto resumido
     * 
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] makeHash(byte[] conteudo) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(conteudo);
        byte[] hash = messageDigest.digest();
        return hash;
    }

    /**
     * Realiza a assinatura do dado combinando o algoritmo MD5 com RSA
     * 
     * @param data
     * Dado a ser assinado
     * 
     * @param privKey
     * chave privada do proprietario do dado
     * 
     * @return sig
     * retorna a assinatura
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

    /**
     * Verifica se a assinatura eh valida sobre um determinado dado eh valida a partir da chave 
     * publica disponibilizada.
     * 
     * @param conteudo
     * dado assinado
     * 
     * @param signature
     * assinatura sobre o conteudo
     * 
     * @param pubKey
     * Chave publica do cliente
     * 
     * @return resultado
     * valor logico que afirma se eh ou nao eh valida.
     */
    public static boolean isValidaAssinatura(byte[] conteudo, Signature signature, PublicKey pubKey) {
        boolean resultado = false;
        try {
            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initVerify(pubKey);
            sig.update(conteudo);
            resultado = sig.verify(signature.sign());
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }
}
