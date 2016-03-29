/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

/**
 *
 * @author joaocosta
 */
public interface Data {
    
    public int getDataSize();

    public void setDataSize(int dataSize);

    public int getPos();

    public void setPos(int pos);

    public byte[] getConteudo();

    public void setConteudo(byte[] conteudo);

    @Override
    public String toString() ;
}
