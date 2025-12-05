package com.base.CRUD_EmployeeMeeting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.base.interfaces.Registro;

public class EmployeeMeeting implements Registro {

    private int id;
    private int idEmployee;
    private int idMeeting;

    public EmployeeMeeting() {
        this(-1, -1, -1);
    }

    public EmployeeMeeting(int idEmployee, int idMeeting) {
        this(-1, idEmployee, idMeeting);
    }

    public EmployeeMeeting(int id, int idEmployee, int idMeeting) {
        this.id = id;
        this.idEmployee = idEmployee;
        this.idMeeting = idMeeting;
    }

    // Getters e setters ↓↓↓

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
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
        dos.writeInt(this.idEmployee);
        dos.writeInt(this.idMeeting);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.idEmployee = dis.readInt();
        this.idMeeting = dis.readInt();
    }

    // CRUD helpers ↓↓↓

    @Override
    public int getForeignKey() {
        return this.idEmployee;
    }

    public int getIntKey() {
        return this.idMeeting;
    }

    @Override
    public String getString() {
        return String.format("EmployeeMeeting[id=%d, atendee=%d, meeting=%d]", id, idEmployee, idMeeting);
    }
}
