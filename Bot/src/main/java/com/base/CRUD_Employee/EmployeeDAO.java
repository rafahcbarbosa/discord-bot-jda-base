package com.base.CRUD_Employee;

import com.base.GeneralStructures.Arquivo;
import java.util.ArrayList;
import com.base.Compression.*;
import com.base.Encryption.*;;

public class EmployeeDAO {

    private static Arquivo<Employee, IndiceEmployee> arqEmployees;

    static {
        try {
            arqEmployees = new Arquivo<>(
                    "employees",
                    Employee.class.getConstructor(),
                    IndiceEmployee.class.getConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean insertEmployee(Employee obj) throws Exception {

        obj.setEmail(Encriptor.Criptografar(obj.getEmail()));
        obj.setPhoneNumber(Encriptor.Criptografar(obj.getPhoneNumber()));
        return arqEmployees.create(obj, obj.getId());
    }

    public static Employee searchEmployee(int id) throws Exception {
        return arqEmployees.read(id);
    }

    public static boolean updateEmployee(Employee obj) throws Exception {
        return arqEmployees.update(obj);
    }

    public static boolean deleteEmployee(int id) throws Exception {
        return arqEmployees.delete(id);
    }

    public static Employee searchByEmail(String email) throws Exception {
        ArrayList<Employee> employees = arqEmployees.searchByString(email);
        if (employees.isEmpty()){
            return null;
        }
        return employees.get(0);
    }

    public static String fileToString() throws Exception {
        ArrayList<Employee> list = arqEmployees.getAll();
        String total = "Employee";

        for (int i = 0; i < list.size(); i++) {
            Employee emp = list.get(i);
            total += "|||||";
            total += emp.getName();
            total += "|||||";
            total += emp.getPhoneNumber();
            total += "|||||";
            total += emp.getEmail();
            total += "|||||";
            total += emp.getBirthdate();
            total += "|||||";
            total += emp.getRole();
        }

        return total;
    }

    public static ArrayList<Employee> getAll() throws Exception {
        return arqEmployees.getAll();
    }

    public static HuffmanEntry toHuffmanEntry() throws Exception {
        return Huffman.cod(fileToString());
    }

    public static void recreateDir() throws Exception {
        arqEmployees.createNewDir();
    }

    public static byte[] toLZWEntry() throws Exception {
        return LZW.codifica(fileToString().getBytes());
    }

    /*
     * public static LZWEntry toLZWEntry() throws Exception {
     * return LZW.
     * }
     */
}