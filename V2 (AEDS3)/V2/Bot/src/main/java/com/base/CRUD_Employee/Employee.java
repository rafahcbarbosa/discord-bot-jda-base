package com.base.CRUD_Employee;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Employee implements Registro{

    public EmployeeDAO employeeDAO;
    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String birthdate;
    private String role;
    
    public Employee() throws Exception {

        this(-1, "", "", "", "", "");
    }

    public Employee(String name, String phoneNumber, String email, String birthdate, String role) throws Exception {

        this(-1, name, phoneNumber, email, birthdate, role);
    }

    public Employee(int id, String name, String phoneNumber, String email, String birhtdate, String role) throws Exception{

        this.employeeDAO = new EmployeeDAO();
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthdate = birhtdate;
        this.role = role;
    }

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


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getBirthdate() {
        return birthdate;
    }


    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }

    // Implementação do método toByteArray()
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.name);
        dos.writeUTF(this.phoneNumber);
        dos.writeUTF(this.email);
        dos.writeUTF(this.birthdate);
        dos.writeUTF(this.role);

        return baos.toByteArray();
    }

    // Implementação do método fromByteArray()
    public void fromByteArray(byte[] b) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.name = dis.readUTF();;
        this.phoneNumber = dis.readUTF();
        this.email = dis.readUTF();
        this.birthdate= dis.readUTF();
        this.role = dis.readUTF();

    }

    public Employee searchEmployee() throws Exception{

        return employeeDAO.searchEmployee(this.id);
    }

    public void insertEmployee() throws Exception{

        employeeDAO.insertEmployee(this);
    }

    public void updateEmployee() throws Exception{

        employeeDAO.updateEmployee(this);
    }

    public void deleteEmployee() throws Exception{

        employeeDAO.deleteEmployee(id);
    }
}


