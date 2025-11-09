package com.base.core;

import java.util.Arrays;

import com.base.util.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// Command information
public class Command {
    
    private String type; // What command was requested by the user? 
    private User user; // User that requested the action
    private String[] parameters; // Parameters of the command (if necessary)
    private String requestTime; // What time whas the command requested

    public Command(){}

    public String getType(){
        return type;
    }

    public void setType(String command) {
        this.type = command;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }


        // Builds the command object
    public void commandBuilder(User user, String type, String[] parameters){

        // Getting current time
        LocalDateTime now = LocalDateTime.now();

        // Formatting current datetime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String requestTime = now.format(formatter);

        // Setting the information
        this.user = user;
        this.type = type;
        this.parameters = parameters;         
        this.requestTime = requestTime;

        System.out.println("- Command info:");
        System.out.println("    Type: " + type);
        System.out.println("    User: " + this.user.getUserID());
        System.out.print("    Parameters: ");

        
        if (parameters.length > 0){

            for (int i = 0; i < this.parameters.length; i++){

                System.out.print("[" + i + "]" + "|" + this.parameters[i] + "| ");
            }

            System.out.println();

        } else {

            System.out.println("(NONE)");
        }

        System.out.println("    Request time: " + requestTime);

        System.out.println();
    }

    // Sets the command type
    public String commandType(MessageReceivedEvent messageEvent, boolean userExists, String[] parameters){

        // Transforms the message into a String
        String message = messageEvent.getMessage().getContentRaw().trim();   
        
        // Splits the string into COMMAND and PARAMETERS
        int spaceIndex = message.indexOf(" ");

        String type;

        // Command without parameters
        if (spaceIndex == -1){

            // The message is the type
            type = message;  

            // No parameters
            parameters = new String[0];
        
        // Command with parameters
        } else {

            // Gets the type from the complete message
            type = message.substring(0, spaceIndex);

            // Gets the parameters
            parameters = message.substring(spaceIndex + 1).trim().split(",");

            // Sanitizes each array positions
            for (int i = 0; i < parameters.length; i++){

                parameters[i] = parameters[i].trim();
            }      
        }

        // Removes the prefix from the TYPE, if it exists
        if (type.startsWith(Utils.PREFIX)){

            type = type.substring(Utils.PREFIX.length()).trim();
        }

        String fullCommand = Utils.getfullCommand(type);

        System.out.println("- Checking the command PROPRIETY and EXISTANCE");
        System.out.println("\n=========================================================================");
        System.out.println("- EXISTING TYPE: The command requested exists");
        System.out.println("- PROPER TYPE: The user can request this command (disconsidering its role)");
        System.out.println("=========================================================================\n");

        // Flags for IMPROPER commands

        boolean commandExists = true;

        // Checks if it is an EXISTING command 
        if (!Arrays.asList(Utils.publicCommands).contains(fullCommand)){

            type = Utils.typesException[0]; // unknown
            commandExists = false;

            System.out.println("- UNEXISTING command type!");
  
        } else {

            System.out.println("- EXISTING command type!");
        }

        boolean isFromGuild = messageEvent.isFromGuild();

        // Registered user
        if (userExists && commandExists){

            // User requested a DM ONLY command on a server
            if (
                
                // Is the message from a server?
                isFromGuild
            
            && (

                // Is it present in DM command lists?
                Utils.premiumDmList.contains(fullCommand) || Utils.freeDmList.contains(fullCommand)

            ) && !(
                
                // Is it NOT present in any server command lists?
                Utils.premiumServerList.contains(fullCommand) || Utils.freeServerList.contains(fullCommand)

            )){

                type = Utils.typesException[6]; // dm-only
            }

            // User requested a SERVER ONLY command on a dm
            else if (

                // Is the message from a dm?
                !isFromGuild
            
            && (
            
                // Is it present in server command lists?
                Utils.premiumServerList.contains(fullCommand) || Utils.freeServerList.contains(fullCommand)

            ) && !(

                // Is it NOT present in any DM command lists?
                Utils.premiumDmList.contains(fullCommand) || Utils.freeDmList.contains(fullCommand)

            )){

                type = Utils.typesException[7]; // server-only

            // User requested !register-user
            } else if (fullCommand.equals(Utils.publicCommands[0])){

                type = Utils.typesException[2]; // user-already-registered
            
            // User requested !register-server
            } else if (fullCommand.equals(Utils.publicCommands[1])){
                
                Server server = new Server();
                boolean serverExists = server.serverExists(messageEvent);

                // Server exists
                if (serverExists){

                    type = Utils.typesException[3]; // server-already-registered

                // Server doesn't exist
                } else {

                    // Command has parameters
                    if (parameters.length > 0){

                        type = Utils.typesException[4]; // non-parameterized
                    }
                }
            
            // User exists and requested other command than registration   
            } else {

                // Command can't have parameters
                if (Arrays.asList(Utils.nonParameterizedCommands).contains(fullCommand) && parameters.length > 0){

                    type = Utils.typesException[4]; // non-parameterized

                // Command must have parameters
                } else if (Arrays.asList(Utils.parameterizedCommands).contains(fullCommand) && parameters.length == 0){
                    
                    type = Utils.typesException[5]; // parameterized
                } 
            }
            
        // Not registered user
        } else if (!userExists && commandExists){

            // User requested any other command than !register-user
            if (!(fullCommand).equals(Utils.publicCommands[0]) && Arrays.asList(Utils.publicCommands).contains(fullCommand)){

                type = Utils.typesException[1]; // not-registered
            
            // User requested !register-user
            } else {

                // Command can't have parameters
                if (parameters.length > 0){

                    type = Utils.typesException[4]; // non-parameterized
                } 
            }
        }

        System.out.println("- Type: " + type);
        System.out.print("- Parameters: ");

        if (parameters.length > 0){

            for (int i = 0; i < parameters.length; i++){

                System.out.print("[" + i + "] " + "|" + parameters[i] + "| ");
            }

            System.out.println();

        } else {

            System.out.println("(NONE)");
        }

        // If it's a PROPER and EXISTING command, its type has already been correctly defined on the type/parameters split
        return type;
    }

    public void commandStorage() {

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting command insertion process...");
        System.out.println("[DB] User Discord ID (for lookup): " + this.getUser().getUserID());

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;

        try {
            // 1. Connecting
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Getting internal user ID
            String select = "SELECT id_user FROM User WHERE id = ?";
            psSelect = conn.prepareStatement(select);
            psSelect.setString(1, this.getUser().getUserID());
            rs = psSelect.executeQuery();

            int idUser = -1;
            if (rs.next()) {
                idUser = rs.getInt("id_user");
                System.out.println("[DB] Internal user ID found: " + idUser);
            } else {
                System.out.println("[DB] User not found in database. Aborting command insertion.");
                return;
            }

            // 3. Preparing INSERT
            String query = "INSERT INTO Command (id_user, type, parameters, request_time) VALUES (?, ?, ?, ?)";
            System.out.println("[DB] Prepared query: " + query);
            psInsert = conn.prepareStatement(query);
            psInsert.setInt(1, idUser);
            psInsert.setString(2, this.getType());
            psInsert.setString(3, String.join(",", this.getParameters()));
            psInsert.setString(4, this.getRequestTime());

            System.out.println("[DB] Parameters set:");
            System.out.println(" - id_user     : " + idUser);
            System.out.println(" - Type        : " + this.getType());
            System.out.println(" - Parameters  : " + String.join(",", this.getParameters()));
            System.out.println(" - Request Time: " + this.getRequestTime());

            // 4. Executing
            psInsert.executeUpdate();
            System.out.println("[DB] Command successfully inserted!");

        } catch (SQLException e) {
            System.out.println("[DB] Exception occurred while inserting command:");
            e.printStackTrace();

        } finally {
            
            try {

                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. Command storage complete.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }

        System.out.println("───────────────────────────────────────────────\n");
    }
}
