package com.base.CRUD_User;

import java.util.ArrayList;

import com.base.Compression.Huffman;
import com.base.Compression.HuffmanEntry;

import com.base.Compression.LZW;
import com.base.GeneralStructures.Arquivo;

public class UserDAO {

    private static Arquivo<User, IndiceUser> arqUsers;

    static {
        try {
            arqUsers = new Arquivo<>(
                    "user",
                    User.class.getConstructor(),
                    IndiceUser.class.getConstructor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CREATE
    public static boolean insertUser(User atendee) throws Exception {
        return arqUsers.create(atendee,atendee.getId());
    }

    // READ
    public static User searchUser(int id) throws Exception {
        return arqUsers.read(id);
    }

    // UPDATE
    public static boolean updateUser(User atendee) throws Exception {
        return arqUsers.update(atendee);
    }

    // DELETE
    public static boolean deleteUser(int id) throws Exception {
        return arqUsers.delete(id);
    }

    public static boolean exists(String name) throws Exception {
        User user = searchByName(name);

        if(user == null){
            return false;
        }
        return true;
    }

    // SEARCH by foreign key (FK)
    public static ArrayList<User> searchByFK(int fkValue) throws Exception {
        return arqUsers.searchByFK(fkValue);
    }

    // SEARCH by string field (like name)
    public static User searchByName(String name) throws Exception {
        ArrayList<User> users = arqUsers.searchByString(name);
        if (users.isEmpty())
            return null;
        return users.get(0);
    }

    // GET ALL
    public static ArrayList<User> getAll() throws Exception {
        return arqUsers.getAll();
    }

    // REPRESENT FILE AS STRING
    public static String fileToString() throws Exception {
        ArrayList<User> atendees = arqUsers.getAll();
        StringBuilder sb = new StringBuilder("User");

        for (User e : atendees) {
            sb.append("|||||");
            sb.append(e.getName());
            sb.append("|||||");
            sb.append(e.getCargo());
        }

        return sb.toString();
    }

    // RECREATE DIRECTORY
    public static void recreateDir() throws Exception {
        arqUsers.createNewDir();
    }

    // HUFFMAN ENTRY
    public static HuffmanEntry toHuffmanEntry() throws Exception {
        return Huffman.cod(fileToString());
    }

    // LZW ENTRY
    public static byte[] toLZWEntry() throws Exception {
        return LZW.codifica(fileToString().getBytes());
    }

}
