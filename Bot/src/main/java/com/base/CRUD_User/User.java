package com.base.CRUD_User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.base.GeneralStructures.Arquivo;
import com.base.GeneralStructures.ArvoreBMais;
import com.base.interfaces.Registro;

public class User implements Registro {

    

    private int id;
    private String name;
    private String cargo;

    

    public User() {
        this(-1, "", "");
    }

    public User(String name, String cargo) {
        this(-1, name, cargo);
    }

    public User(int id, String name, String cargo) {
        this.id = id;
        name = name.trim();
        this.name = name;
        this.cargo = cargo;
    }

    // Getters e setters ↓↓↓
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.trim();
        this.name = name;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    
    // Serialização ↓↓↓
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeUTF(this.cargo);
       
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.name = dis.readUTF();
        this.cargo = dis.readUTF();
    
    }

    // CRUD ↓↓↓
    

    public int getForeignKey() {
        return this.id;
    }
     public int getIntKey() {
        return this.id;
    }
    public String getString(){
        return this.name;
    }

}
