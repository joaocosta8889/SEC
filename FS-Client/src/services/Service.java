/*
 * Service
 */

package services;

//import java.io.BufferedReader;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.Signature;

//import java.io.BufferedWriter;

/**
 *
 * @author joaocosta
 */
public interface Service extends Remote{
    
    public byte[] get(byte[] id) throws RemoteException;
    public byte[] putK(Data data, Signature signature, PublicKey publicKey)
            throws RemoteException;
    public byte[] putH(Data data) throws RemoteException;
    
    //Methods of the test
    public String getDataHora() throws RemoteException;
    
}
