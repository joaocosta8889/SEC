package fs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface BlockService extends Remote{
	Block get(PublicKey id) throws RemoteException;
	boolean put_k(Data data, int timestamp, byte[] signature, PublicKey id) throws RemoteException;
}
  