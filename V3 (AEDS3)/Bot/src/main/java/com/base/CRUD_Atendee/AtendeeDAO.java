package com.base.CRUD_Atendee;



import java.util.ArrayList;
import com.base.GeneralStructures.Arquivo;

public class AtendeeDAO {

    private static Arquivo<Atendee, IndiceAtendee> arqAtendees;

    static {
        try {
            arqAtendees = new Arquivo<>(
                "atendee",
                Atendee.class.getConstructor(),
                IndiceAtendee.class.getConstructor()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CREATE
    public static boolean insertAtendee(Atendee atendee) throws Exception {
        return arqAtendees.create(atendee, atendee.getId());
    }

    // READ
    public static Atendee searchAtendee(int id) throws Exception {
        return arqAtendees.read(id);
    }

    // UPDATE
    public static boolean updateAtendee(Atendee atendee) throws Exception {
        return arqAtendees.update(atendee);
    }

    // DELETE
    public static boolean deleteAtendee(int id) throws Exception {
        return arqAtendees.delete(id);
    }

    // SEARCH by foreign key (FK)
    public static ArrayList<Atendee> searchByFK(int fkValue) throws Exception {
        return arqAtendees.searchByFK(fkValue);
    }

    // SEARCH by string field (like name)
    public static ArrayList<Atendee> searchByName(String name) throws Exception {
        return arqAtendees.searchByString(name);
    }
}
