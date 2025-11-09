package com.base.CRUD_AtendeeMeeting;

import com.base.GeneralStructures.Arquivo;
import java.util.ArrayList;

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

    public boolean insert(AtendeeMeeting obj) throws Exception {
        return arqAtendeeMeeting.create(obj,obj.getId());
    }

    public AtendeeMeeting read(int id) throws Exception {
        return arqAtendeeMeeting.read(id);
    }

    public boolean update(AtendeeMeeting obj) throws Exception {
        return arqAtendeeMeeting.update(obj);
    }

    public boolean delete(int id) throws Exception {
        return arqAtendeeMeeting.delete(id);
    }

    // Search by foreign keys ↓↓↓

    public ArrayList<AtendeeMeeting> searchByAtendee(int atendeeId) throws Exception {
        return arqAtendeeMeeting.searchByFK(atendeeId);
    }

    public ArrayList<AtendeeMeeting> searchByMeeting(int meetingId) throws Exception {
        return arqAtendeeMeeting.searchByInt(meetingId);
    }
}
