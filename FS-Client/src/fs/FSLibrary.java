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
	

	public void FS_init() throws NoSuchAlgorithmException, MalformedURLException, NotBoundException, RemoteException, CertificateException, PteidException {	
		
		this.block_service =  (BlockService) Naming.lookup("//localhost:8081/FS"); 
		
		init_pteid();
		X509Certificate cert = getCertificate(getCertificateBytes(0));
		block_service.storePubKey(cert);
	}
	
	public PublicKey FS_write(int pos, int syze, byte[] contents) throws InvalidKeyException, RemoteException, SignatureException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, CertificateException {
		
		Data cont = new Data(syze, pos, contents);
		
		byte[] signature = makeSignature(contents);
		X509Certificate cert = getCertificate(getCertificateBytes(0));
		this.block_service.put_k(contents, signature, cert.getPublicKey());
		
		return cert.getPublicKey();
		
	}
	
	public byte[] FS_read(PublicKey pk, int pos, int nbytes) throws RemoteException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IllegalArgumentException, SignatureException, NoSuchAlgorithmException {
		
		Data cont = new Data(nbytes, pos);
		
		byte[] bytes_read = block_service.get(pk);
		
		// filtra conteudo
		byte[] out = new byte[pos+nbytes];
		for(int i=pos, j= 0; i < (pos + nbytes); i++, j++){
			out[j] = bytes_read[i];
		}
				 
		return out;	
	}
	
	public List<PublicKey> FS_list() throws RemoteException {
		return block_service.readPubKeys();	
	}
	
	private void init_pteid() throws PteidException {
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
	
	private static byte[] makeSignature(byte[] data) throws PKCS11Exception, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
    	PKCS11 pkcs11 = null;
    	String osName = System.getProperty("os.name");
    	String libName = "libpteidpkcs11.so";
    	String javaVersion = System.getProperty("java.version");
    	
    	 if (-1 != osName.indexOf("Windows"))
             libName = "pteidpkcs11.dll";
         else if (-1 != osName.indexOf("Mac"))
             libName = "pteidpkcs11.dylib";
         Class<?> pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11");
         if (javaVersion.startsWith("1.5."))
         {
             Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
             pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, null, false });
         }
         else
         {
             Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
             pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, "C_GetFunctionList", null, false });
         }

    	
		long p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);        //Open the PKCS11 session
		
		pkcs11.C_Login(p11_session, 1, null);							//Token login							
        
        CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1];						//Get available keys
        attributes[0] = new CK_ATTRIBUTE();
        attributes[0].type = PKCS11Constants.CKA_CLASS;
        attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);
        pkcs11.C_FindObjectsInit(p11_session, attributes);
        long[] keyHandles = pkcs11.C_FindObjects(p11_session, 5);
        long signatureKey = keyHandles[0];			
        pkcs11.C_FindObjectsFinal(p11_session);
        
        CK_MECHANISM mechanism = new CK_MECHANISM();					//Initialize signature
        mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
        mechanism.pParameter = null;
        pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
        
        byte[] signature = pkcs11.C_Sign(p11_session, data); 			//Sign
		
	    return signature;
	}
	
}
