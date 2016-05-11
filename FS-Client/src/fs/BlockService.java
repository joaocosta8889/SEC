package fs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.List;

public interface BlockService extends Remote{
	Block get(PublicKey id) throws RemoteException;
	boolean put_k(Data data, int timestamp, byte[] signature, PublicKey id) throws RemoteException;
}
  