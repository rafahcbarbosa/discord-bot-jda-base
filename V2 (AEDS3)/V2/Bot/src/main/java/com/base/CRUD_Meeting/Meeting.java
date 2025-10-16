package com.base.CRUD_Meeting;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;


import com.base.CRUD_Employee.Employee;

import com.base.GeneralStructures.Arquivo;
import com.base.interfaces.Registro;

import com.base.CRUD_Meeting.IndiceMeeting;


public class Meeting implements Registro{

    public Arquivo<Meeting, IndiceMeeting,IndiceFk> arqMeetings;

    private int id;
    private String name;
    private String description;
    private String date;
    private String startTime;
    private String endTime;
    private int idEmployee;
    
    public Meeting()throws Exception{
        this(-1,"","","","","",1);
    }
    public Meeting(String name,String description,String date, String startTime,String endTime,int idEmployee)throws Exception{
        this(-1,name,description,date,startTime,endTime,idEmployee);
    }

    public Meeting(int id,String name,String description,String date, String startTime,String endTime,int idEmployee) throws Exception{
        arqMeetings = new Arquivo<>("Meetings", Meeting.class.getConstructor(),IndiceMeeting.class.getConstructor());
        this.id = id;
        this.description = description;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.idEmployee = idEmployee;
    }
    

    /**
     * @return int return the id
     */
    public int getId() {
        return id;
    }

    public void setIdEmployee(int id){
        this.idEmployee = id;
    }

    
    public int getIdEmployee(){
        return this.idEmployee;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return String return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return String return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return String return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    


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
        this.name = dis.readUTF();;
        this.description = dis.readUTF();
        this.date = dis.readUTF();
        this.startTime= dis.readUTF();
        this.endTime = dis.readUTF();
        this.idEmployee = dis.readInt();

    }

    public Meeting searchMeeting() throws Exception {
        return arqMeetings.read(this.id);
    }

    public boolean insertMeeting() throws Exception {

        
        return arqMeetings.create(this);
    }

    public boolean updateMeeting() throws Exception {
        return arqMeetings.update(this);
    }

    public boolean deleteMeeting() throws Exception {
        return arqMeetings.delete(this.id);
    }
}