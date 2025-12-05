package com.base.CRUD_EmployeeMeeting;

import com.base.GeneralStructures.Arquivo;
import java.util.ArrayList;
import com.base.Compression.*;

public class EmployeeMeetingDAO {

    private static Arquivo<EmployeeMeeting, IndiceEmployeeMeeting> arqEmployeeMeeting;

    static {
        try {
            arqEmployeeMeeting = new Arquivo<>(
                    "atendeeMeeting",
                    EmployeeMeeting.class.getConstructor(),
                    IndiceEmployeeMeeting.class.getConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CRUD ↓↓↓

    public static boolean insert(EmployeeMeeting obj) throws Exception {
        return arqEmployeeMeeting.create(obj, obj.getId());
    }

    public static EmployeeMeeting read(int id) throws Exception {
        return arqEmployeeMeeting.read(id);
    }

    public static boolean update(EmployeeMeeting obj) throws Exception {
        return arqEmployeeMeeting.update(obj);
    }

    public static boolean delete(int id) throws Exception {
        return arqEmployeeMeeting.delete(id);
    }

    

    public static ArrayList<EmployeeMeeting> searchByEmployee(int EmployeeId) throws Exception {
        return arqEmployeeMeeting.searchByFK(EmployeeId);
    }

    public static ArrayList<EmployeeMeeting> searchByMeeting(int meetingId) throws Exception {
        return arqEmployeeMeeting.searchByInt(meetingId);
    }

    public static String fileToString() throws Exception {
        ArrayList<EmployeeMeeting> list = arqEmployeeMeeting.getAll();
        String total = "EmployeeMeeting";

        for (int i = 0; i < list.size(); i++) {
            total += "|||||";
            total += list.get(i).getIdEmployee();
            total += "|||||";
            total += list.get(i).getIdMeeting();
        }

        return total;
    }

    public static ArrayList<EmployeeMeeting> getAll() throws Exception {
        
        return arqEmployeeMeeting.getAll();
    }

    public static HuffmanEntry toHuffmanEntry() throws Exception {
        return Huffman.cod(fileToString());
    }
    public static void recreateDir() throws Exception{
        arqEmployeeMeeting.createNewDir();
    }

     public static byte[] toLZWEntry() throws Exception {
        return LZW.codifica(fileToString().getBytes());
    }

}
