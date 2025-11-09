package com.base.CRUD_AtendeeMeeting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.base.interfaces.Registro;

public class AtendeeMeeting implements Registro {

    private int id;
    private int idAtendee;
    private int idMeeting;

    public AtendeeMeeting() {
        this(-1, -1, -1);
    }

    public AtendeeMeeting(int idAtendee, int idMeeting) {
        this(-1, idAtendee, idMeeting);
    }

    public AtendeeMeeting(int id, int idAtendee, int idMeeting) {
        this.id = id;
        this.idAtendee = idAtendee;
        this.idMeeting = idMeeting;
    }

    // Getters e setters ↓↓↓

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAtendee() {
        return idAtendee;
    }

    public void setIdAtendee(int idAtendee) {
        this.idAtendee = idAtendee;
    }

    public int getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(int idMeeting) {
        this.idMeeting = idMeeting;
    }

    // Serialização ↓↓↓

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeInt(this.idAtendee);
        dos.writeInt(this.idMeeting);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.idAtendee = dis.readInt();
        this.idMeeting = dis.readInt();
    }

    // CRUD helpers ↓↓↓

    @Override
    public int getForeignKey() {
        return this.idAtendee;
    }

    public int getIntKey() {
        return this.idMeeting;
    }

    @Override
    public String getString() {
        return String.format("AtendeeMeeting[id=%d, atendee=%d, meeting=%d]", id, idAtendee, idMeeting);
    }
}
