package fs;

import pteidlib.PTEID_Certif;
import pteidlib.PteidException;
import pteidlib.pteid;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;


public class FSLibrary {

	private BlockService block_service;
	private Cipher cipher;
	private KeyPair keys;
	
	public PublicKey getId(){
		return keys.getPublic();
	}
	
	public void FS_init() throws NoSuchAlgorithmException, MalformedURLException, NotBoundException, RemoteException, CertificateException, PteidException {	
		
		this.block_service =  (BlockService) Naming.lookup("//localhost:8081/FS"); 
		init_pteid();
		
		X509Certificate certificate = getCertificate(getCertificateBytes(0));
		block_service.storePubKey(certificate);
	}
	
	public void FS_write(int pos, int syze, byte[] contents) throws InvalidKeyException, RemoteException, SignatureException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, CertificateException {
		
		
		byte[] encoded =  encrypt(contents);
		Data cont = new Data(syze, pos, encoded);
		
		keys = generateKeys();
		byte[] signature = makeSignature(encoded, keys.getPrivate());
		X509Certificate certificate = getCertificate(getCertificateBytes(0));
		this.block_service.put_k(cont, signature, certificate.getPublicKey());
		
	}
	
	public byte[] FS_read(PublicKey pk, int pos, int nbytes) throws RemoteException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IllegalArgumentException, SignatureException, NoSuchAlgorithmException {
		
		Data cont = new Data(nbytes, pos);
		
		byte[] bytes_read = block_service.get(pk);
		byte[] decoded = decrypt(bytes_read);
		
		// filtra conteudo
		byte[] out = new byte[pos+nbytes];
		for(int i=pos, j= 0; i < (pos + nbytes); i++, j++){
			out[j] = decoded[i];
		}
				 
		return out;	
	}
	
	public List<PublicKey> FS_list() throws RemoteException {
		return block_service.readPubKeys();	
	}

	private KeyPair generateKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key_pair = keyGen.generateKeyPair();
		
		return key_pair;
	}

	private byte[] generateId() throws NoSuchAlgorithmException {
		byte[] keyBytes = this.keys.getPublic().getEncoded();
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(keyBytes);
		byte[] new_id = messageDigest.digest();
		
		return new_id;
	}
	
	private byte[] makeSignature(byte[] data, PrivateKey private_key) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		 Signature sig = Signature.getInstance("MD5WithRSA");
	     sig.initSign(private_key);
	     sig.update(data);
	     byte[] new_signature = sig.sign();
	     
	     return new_signature;
	}
	
	private byte[] encrypt(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.keys.getPrivate());
        byte[] encoded_bytes =  this.cipher.doFinal(data);
        
        return encoded_bytes;
	}
	
	private byte[] decrypt(byte[] encrypted_data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		this.cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());
        byte[] decoded_bytes = this.cipher.doFinal(encrypted_data); 
        
        return decoded_bytes;
	}
	
	private static void init_pteid() throws PteidException {
		System.loadLibrary("pteidlibj");
		pteid.Init("");
		pteid.SetSODChecking(false);
	}
	
	public static X509Certificate getCertificate(byte[] certificateEncoded) throws CertificateException {
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(certificateEncoded);
        X509Certificate certitificate = (X509Certificate) f.generateCertificate(in);
        
        return certitificate;
	}
	
	private static byte[] getCertificateBytes(int n) {
		 
		byte[] certificate_bytes = null;
		
		try {
		    PTEID_Certif[] certs = pteid.GetCertificates();
		    certificate_bytes = certs[n].certif; 
		    pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD); 
		} catch (PteidException e) {
		    e.printStackTrace();
		}
		
		return certificate_bytes;
	}
	
}
