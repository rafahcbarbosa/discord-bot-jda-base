package com.base.interfaces;
import java.io.IOException;

public interface Registro {

    public void setId(int i);
    public int getId();
    public byte[] toByteArray() throws IOException;
    public void fromByteArray(byte[] b) throws IOException;
    public int getForeignKey() ;
    public String getString();
    public int getIntKey();

}
