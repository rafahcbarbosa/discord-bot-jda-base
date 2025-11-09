


package com.base.CRUD_Meeting;

import com.base.GeneralStructures.Arquivo;
import com.base.GeneralStructures.ArvoreBMais;
import com.base.interfaces.Registro;
import java.util.ArrayList;
public class MeetingDAO {

    private static Arquivo<Meeting, IndiceMeeting> arqMeetings;
    static {
        try {

            arqMeetings = new Arquivo<>(
                    "meeting",
                    Meeting.class.getConstructor(),
                    IndiceMeeting.class.getConstructor()
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insertMeeting(Meeting meeting) throws Exception {
        return arqMeetings.create(meeting, meeting.getIdEmployee());
    }

    public Meeting searchMeeting(int id) throws Exception {
        return arqMeetings.read(id);
    }

   

    public boolean updateMeeting(Meeting meeting) throws Exception {
        return arqMeetings.update(meeting);
    }

    public boolean deleteMeeting(int id) throws Exception {
        return arqMeetings.delete(id);
    }

    public ArrayList<Meeting> searchByFK(int fkValue) throws Exception {
        // chama diretamente o método do Arquivo que já retorna ArrayList<Meeting>
        return arqMeetings.searchByFK(fkValue);
    }
    public ArrayList<Meeting> searchByName(String fkValue) throws Exception {
        // chama diretamente o método do Arquivo que já retorna ArrayList<Meeting>
        return arqMeetings.searchByString(fkValue);
    }

}