package com.base.util;

import java.util.Arrays;
import java.util.List;

import com.base.core.Command;
import com.base.core.User;
import com.base.core.Server;
import com.base.core.Member;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// Class that contains global data
public class Utils {
    
    // TODO: Add bot and database info
    
    // Bot name
    public static final String name = "";

    // Prefix for all bot commands
    public static final String PREFIX = "";

    // Database connection credentials
    public static final String DB_URL = "";
    public static final String DB_USER = "";
    public static final String DB_PASSWORD = "";

    // SQL Connection command
    // conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
    
    // Maximum daily requests for a free user
    public static final int MAX_REQUESTS = 10;
    
    // TODO: Add commands here

    // All bot commands
    public static final String[] publicCommands = {
        
        Utils.PREFIX + "register-user",             // [0]
        Utils.PREFIX + "register-server",           // [1]
       };

    // Non parameterized commands
    public static final String[] nonParameterizedCommands = {

        Utils.PREFIX + "register-user",             // [0]
        Utils.PREFIX + "register-server",           // [1]
    };

    // Parameterized commands
    public static final String[] parameterizedCommands = {


    };

    // DM commands
    public static final String[] premiumDmCommands = {
        
        Utils.PREFIX + "register-user",             // [0]

    };

    public static final String[] freeDmCommands = {

        Utils.PREFIX + "register-user",             // [0]
    
    };

    // Server commands
    public static final String[] premiumServerCommands = {
        
        Utils.PREFIX + "register-server",           // [0]
        Utils.PREFIX + "register-user",             // [1]

    };

    public static final String[] freeServerCommands = {

        Utils.PREFIX + "register-server",           // [0]
        Utils.PREFIX + "register-user",             // [1]
    
    };

    // Roles
    public static final String[] roles = {
        
        "free",                                     // [0]
        "premium",                                  // [1] 
    };

    // Command arrays as lists
    public static final List<String> premiumDmList = Arrays.asList(premiumDmCommands);
    public static final List<String> freeDmList = Arrays.asList(freeDmCommands);
    public static final List<String> premiumServerList = Arrays.asList(premiumServerCommands);
    public static final List<String> freeServerList = Arrays.asList(freeServerCommands);

    // Commands types exceptions
    public static final String[] typesException = {
        
        "unknown",                                  // [0]
        "not-registered",                           // [1]
        "user-already-registered",                  // [2]
        "server-already-registered",                // [3]
        "non-parameterized",                        // [4]
        "parameterized",                            // [5]
        "dm-only",                                  // [6]
        "server-only"                               // [7]
    };

    // Full command
    public static String getfullCommand(String type){

        String fullcommand = PREFIX + type;
        return fullcommand;
    }

    public static String getfullCommand(Command command){

        String fullcommand = PREFIX + command.getType();
        return fullcommand;
    }

    // Booleans for requests treatment

    // Is this a SERVER ONLY command (either premium or free)?
    public static boolean isServerOnlyCommand(Command command) {

        String fullCommand = getfullCommand(command);

        return (

            // Is it present in server command lists?
            premiumServerList.contains(fullCommand) || freeServerList.contains(fullCommand)

        ) && !(

            // Is it NOT present in any DM command lists?
            premiumDmList.contains(fullCommand) || freeDmList.contains(fullCommand)

        );
    }

    // Is this a DM ONLY command (either premium or free)?
    public static boolean isDmOnlyCommand(Command command) {

        String fullCommand = getfullCommand(command);

        return (

            // Is it present in DM command lists?
            premiumDmList.contains(fullCommand) || freeDmList.contains(fullCommand)

        ) && !(
            
            // Is it NOT present in any server command lists?
            premiumServerList.contains(fullCommand) || freeServerList.contains(fullCommand)

        );
    }

    // Is this a PREMIUM SERVER command?
    public static boolean isPremiumServerCommand(Command command) {

        String fullCommand = getfullCommand(command);

        return (

            // Is it present in premium server list?
            premiumServerList.contains(fullCommand)
            &&
            // Is it NOT present in free server list?
            !freeServerList.contains(fullCommand)

        );
    }

    // Is this a PREMIUM DM command?
    public static boolean isPremiumDmCommand(Command command) {

        String fullCommand = getfullCommand(command);

        return (

            // Is it present in premium DM list?
            premiumDmList.contains(fullCommand)
            &&
            // Is it NOT present in free DM list?
            !freeDmList.contains(fullCommand)

        );
    }

    // DEBUG - Prints a formatted highlight box with a custom message
    public static void printHighlight(String message) {

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                            MESSAGE RECEIVED                          ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ " + message);
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    public static void updateInfo(MessageReceivedEvent messageEvent){

        User user = new User();
        Server server = new Server();
        Member member = new Member();

        boolean userExists = user.userExists(messageEvent);
        boolean userUpdate = user.userBuilder(userExists, messageEvent);

        if (userUpdate){

            System.out.println("- Updating User...");
            user.userStorage();
        }

        boolean serverExists = server.serverExists(messageEvent);
        boolean serverUpdate = server.serverBuilder(serverExists, messageEvent);

        if (serverUpdate){

            System.out.println("- Updating Server...");
            server.serverStorage();
        }

        boolean memberExists = member.memberExists(messageEvent);
        boolean memberUpdate = member.memberBuilder(memberExists, server, user, messageEvent);

        if (serverExists && (!memberExists || memberUpdate)){

            System.out.println("- Updating Member...");
            member.memberStorage();
        }
    }
}