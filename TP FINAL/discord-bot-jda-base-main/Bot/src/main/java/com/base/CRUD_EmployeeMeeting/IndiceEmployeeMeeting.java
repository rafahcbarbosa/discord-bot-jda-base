package com.base.CRUD_EmployeeMeeting;
import com.base.interfaces.RegistroHashExtensivel;
import java.io.*;

public class IndiceEmployeeMeeting implements RegistroHashExtensivel<IndiceEmployeeMeeting> {

    private int id;
    private long pos;

    public IndiceEmployeeMeeting() {}

    public IndiceEmployeeMeeting(int id, long pos) {
        this.id = id;
        this.pos = pos;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public short size() {
        
        return (short) (Integer.BYTES + Long.BYTES);
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
}