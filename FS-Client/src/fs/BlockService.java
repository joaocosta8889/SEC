package fs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface BlockService extends Remote{
	byte[] get(PublicKey id) throws RemoteException, IllegalArgumentException, InvalidKeyException, SignatureException, NoSuchAlgorithmException;
	PublicKey put_k(Data data, byte[] signature, PublicKey public_key) throws RemoteException, SignatureException, NoSuchAlgorithmException, InvalidKeyException;
	byte[] put_h(Data data) throws RemoteException, NoSuchAlgorithmException;
	boolean storePubKey(X509Certificate certificate) throws RemoteException;
	List<PublicKey> readPubKeys() throws RemoteException;
}
  