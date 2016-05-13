package fs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

import javax.crypto.SecretKey;


public interface BlockService extends Remote{
	Block get(PublicKey id) throws RemoteException;
	void put_k(Data data, int timestamp, byte[] signature, PublicKey id, byte[] mac) throws RemoteException;
	void register(PublicKey id, SecretKey secret)  throws RemoteException;
}
  