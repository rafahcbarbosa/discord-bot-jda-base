package com.base.CRUD_AtendeeMeeting;

import com.base.GeneralStructures.Arquivo;
import java.util.ArrayList;
import com.base.Compression.*;

public class AtendeeMeetingDAO {

    private static Arquivo<AtendeeMeeting, IndiceAtendeeMeeting> arqAtendeeMeeting;

    static {
        try {
            arqAtendeeMeeting = new Arquivo<>(
                    "atendeeMeeting",
                    AtendeeMeeting.class.getConstructor(),
                    IndiceAtendeeMeeting.class.getConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CRUD ↓↓↓

    public static boolean insert(AtendeeMeeting obj) throws Exception {
        return arqAtendeeMeeting.create(obj, obj.getId());
    }

    public static AtendeeMeeting read(int id) throws Exception {
        return arqAtendeeMeeting.read(id);
    }

    public static boolean update(AtendeeMeeting obj) throws Exception {
        return arqAtendeeMeeting.update(obj);
    }

    public static boolean delete(int id) throws Exception {
        return arqAtendeeMeeting.delete(id);
    }

    // Search by foreign keys ↓↓↓

    public static ArrayList<AtendeeMeeting> searchByAtendee(int atendeeId) throws Exception {
        return arqAtendeeMeeting.searchByFK(atendeeId);
    }

    public static ArrayList<AtendeeMeeting> searchByMeeting(int meetingId) throws Exception {
        return arqAtendeeMeeting.searchByInt(meetingId);
    }

    public static String fileToString() throws Exception {
        ArrayList<AtendeeMeeting> list = arqAtendeeMeeting.getAll();
        String total = "AtendeeMeeting";

        for (int i = 0; i < list.size(); i++) {
            total += "|||||";
            total += list.get(i).getIdAtendee();
            total += "|||||";
            total += list.get(i).getIdMeeting();
        }

        return total;
    }

    public static ArrayList<AtendeeMeeting> getAll() throws Exception {
        // chama diretamente o método do Arquivo que já retorna ArrayList<AtendeeMeeting>
        return arqAtendeeMeeting.getAll();
    }

    public static HuffmanEntry toHuffmanEntry() throws Exception {
        return Huffman.cod(fileToString());
    }

}
