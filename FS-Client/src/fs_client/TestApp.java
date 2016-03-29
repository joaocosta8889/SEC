/*
 * Part number one of the project of Highly Dependable Systems
 * year, 2016.
 */
package fs_client;

import library.ClientLibrary;

/**
 * @author joaocosta
 * @version 0.1 Description: This is app test of Files System, SEC project
 */
public class TestApp {

    public static void main(String[] args) {
        
        ClientLibrary cliente = new ClientLibrary();
        
        cliente.fsInit();
        
        byte[] contents = "Eu amo a minha familia...".getBytes();
        cliente.fsWrite(1, contents.length, contents);
        
        /*
        System.out.println("Saida : "+new String(
                cliente.fsRead(id, 0, size, contents)
        ));*/
        
        System.err.println("Tempo real: "+cliente.getDataHora());
        
    }
}
