package fs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.List;

public interface BlockService extends Remote{
	Message get(PublicKey id) throws RemoteException, IllegalArgumentException, InvalidKeyException, SignatureException, NoSuchAlgorithmException;
	void put_k(Data data, byte[] signature, PublicKey public_key) throws RemoteException, SignatureException, NoSuchAlgorithmException, InvalidKeyException;
}
  