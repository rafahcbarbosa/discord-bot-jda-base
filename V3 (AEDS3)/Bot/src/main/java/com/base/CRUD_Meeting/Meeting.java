package com.base.CRUD_Meeting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.base.GeneralStructures.Arquivo;
import com.base.GeneralStructures.ArvoreBMais;
import com.base.interfaces.Registro;

public class Meeting implements Registro {

    // Arquivo e índice B+ compartilhados entre todas as instâncias

    private int id;
    private String name;
    private String description;
    private String date;
    private String startTime;
    private String endTime;
    private int idEmployee;

    
    public Meeting() {
        this(-1, "", "", "", "", "", 1);
    }

    public Meeting(String name, String description, String date, String startTime, String endTime, int idEmployee) {
        this(-1, name, description, date, startTime, endTime, idEmployee);
    }

    public Meeting(int id, String name, String description, String date, String startTime, String endTime,
            int idEmployee) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.idEmployee = idEmployee;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    // Serialização ↓↓↓
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeUTF(this.description);
        dos.writeUTF(this.date);
        dos.writeUTF(this.startTime);
        dos.writeUTF(this.endTime);
        dos.writeInt(this.idEmployee);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.name = dis.readUTF();
        this.description = dis.readUTF();
        this.date = dis.readUTF();
        this.startTime = dis.readUTF();
        this.endTime = dis.readUTF();
        this.idEmployee = dis.readInt();
    }
    public String getString(){
        return this.name;
    }
    public int getForeignKey() {
        return this.idEmployee;
    }
    public int getIntKey(){
        return this.id;
    }

    /* 
    public boolean insertMeeting() throws Exception {
        return arqMeetings.create(this, this.idEmployee);
    }

    public Meeting searchMeeting() throws Exception {
        return arqMeetings.read(this.id);
    }

    public Meeting searchMeeting(int id) throws Exception {
        return arqMeetings.read(id);
    }

    public boolean updateMeeting() throws Exception {
        return arqMeetings.update(this);
    }

    public boolean deleteMeeting() throws Exception {
        return arqMeetings.delete(this.id);
    }

    

    
        */

}
