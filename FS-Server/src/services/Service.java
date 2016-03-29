/*
 * Service
 */

package services;

import fs_file.Data;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.Signature;

/**
 *
 * @author joaocosta
 */
public interface Service extends Remote{
    
    public byte[] get(byte[] id) throws RemoteException;
    public byte[] putK(Data data, Signature signature, PublicKey publicKey)
            throws RemoteException;
    public byte[] putH(Data data) throws RemoteException;
    
    //Metodo de teste
    public String getDataHora() throws RemoteException;
    
}
