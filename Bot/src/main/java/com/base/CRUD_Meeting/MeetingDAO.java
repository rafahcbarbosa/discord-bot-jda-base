
package com.base.CRUD_Meeting;

import com.base.GeneralStructures.Arquivo;
import com.base.GeneralStructures.ArvoreBMais;
import com.base.interfaces.Registro;
import java.util.ArrayList;
import com.base.Compression.*;

public class MeetingDAO {
    private static Arquivo<Meeting, IndiceMeeting> arqMeetings;
    static {
        try {

            arqMeetings = new Arquivo<>(
                    "meeting",
                    Meeting.class.getConstructor(),
                    IndiceMeeting.class.getConstructor());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean insertMeeting(Meeting meeting) throws Exception {
        return arqMeetings.create(meeting, meeting.getIdUser());
    }

    public static Meeting searchMeeting(int id) throws Exception {
        return arqMeetings.read(id);
    }

    public static boolean updateMeeting(Meeting meeting) throws Exception {
        return arqMeetings.update(meeting);
    }

    public static boolean deleteMeeting(int id) throws Exception {
        return arqMeetings.delete(id);
    }

    public static ArrayList<Meeting> searchByFK(int fkValue) throws Exception {
       
        return arqMeetings.searchByFK(fkValue);
    }

    public static ArrayList<Meeting> searchByName(String fkValue) throws Exception {
       
        return arqMeetings.searchByString(fkValue);
    }

    public static ArrayList<Meeting> getAll() throws Exception {
    
        return arqMeetings.getAll();
    }

    

    public static String fileToString() throws Exception {
       
        ArrayList<Meeting> meetings = arqMeetings.getAll();
        String total = "Meeting";

        for (int i = 0; i < meetings.size(); i++) {
            total += "|||||";
            total += meetings.get(i).getName();
            total += "|||||";
            total += meetings.get(i).getDescription();
            total += "|||||";
            total += meetings.get(i).getDate();
            total += "|||||";
            total += meetings.get(i).getStartTime();
            total += "|||||";
            total += meetings.get(i).getIdUser();
        }

        return total;
    }

    public static HuffmanEntry toHuffmanEntry() throws Exception {
        return Huffman.cod(fileToString());
    }

     public static void recreateDir() throws Exception{
        arqMeetings.createNewDir();
    }

     public static byte[] toLZWEntry() throws Exception {
        return LZW.codifica(fileToString().getBytes());
    }

}