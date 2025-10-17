package com.base.CRUD_Meeting;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.base.interfaces.RegistroArvoreBMais;

public class IndiceFk implements RegistroArvoreBMais<IndiceFk> {

    private int id;
    private long pos;

    public IndiceFk() {
        this(-1, -1);
    }

    public IndiceFk(int id, long pos) {
        this.id = id;
        this.pos = pos;
    }

  
    @Override
    public long getPos() {
        return this.pos;
    }

    @Override
    public void setPos(long pos) {
        this.pos = pos;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public short size() {
        // int (4 bytes) + long (8 bytes) = 12 bytes
        return 12;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeLong(pos);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        pos = dis.readLong();
    }

    @Override
    public int compareTo(IndiceFk obj) {
        return Integer.compare(this.id, obj.id);
    }

    @Override
    public IndiceFk clone() {
        try {
            return (IndiceFk) super.clone();
        } catch (CloneNotSupportedException e) {
            // nunca deve acontecer, pois a classe é clonável
            return new IndiceFk(this.id, this.pos);
        }
    }

    @Override
    public String toString() {
        return id + " | " + pos;
    }
}
