package com.base.CRUD_Atendee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.base.GeneralStructures.Arquivo;
import com.base.GeneralStructures.ArvoreBMais;
import com.base.interfaces.Registro;

public class Atendee implements Registro {

    // Arquivo e índice B+ compartilhados entre todas as instâncias
    private static Arquivo<Atendee, IndiceAtendee> arqAtendees;

    private int id;
    private String name;
    private String cargo;

    

    public Atendee() {
        this(-1, "", "");
    }

    public Atendee(String name, String cargo) {
        this(-1, name, cargo);
    }

    public Atendee(int id, String name, String cargo) {
        this.id = id;
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
