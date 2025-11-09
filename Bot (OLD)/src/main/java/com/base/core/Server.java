package com.base.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.base.types.RegisterServerCommand;
import com.base.util.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Server{   

    private String serverID;
    private String role;
    private String name;
    private String lastUpdateTime;

    public Server(){}

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // Builds a Server object based on a messageEvent
    public boolean serverBuilder(boolean serverExists, MessageReceivedEvent messageEvent) {

        boolean update = false;

        // If the server is on the database, collect the information from it!
        if (serverExists) {

            System.out.println("───────────────────────────────────────────────");
            System.out.println("[DB] Starting server construction from database...");
            System.out.println("[DB] Incoming server ID: " + messageEvent.getGuild().getId());

            try {

                // Attempting connection
                System.out.println("[DB] Connecting to database...");
                Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);

                System.out.println("[DB] Connection established successfully!");

                // Preparing SQL query
                String query = """
                    SELECT 
                        s.id AS discord_id,
                        s.name,
                        s.role,
                        s.last_update_time
                    FROM Server s
                    WHERE s.id = ?
                """;

                System.out.println("[DB] Prepared query: " + query);
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, messageEvent.getGuild().getId());
                System.out.println("[DB] Parameter 1 set as: " + messageEvent.getGuild().getId());

                // Executing query
                ResultSet rs = ps.executeQuery();
                System.out.println("[DB] Query executed. Analyzing result...");

                if (rs.next()) {

                    System.out.println("[DB] Server found in database!");

                    // Extracting values from database
                    String dbServerID   = rs.getString("discord_id");
                    String dbName       = rs.getString("name");
                    String dbRole       = rs.getString("role");
                    String dbLastUpdate = rs.getString("last_update_time");

                    // Getting values from the incoming message
                    String msgName     = messageEvent.getGuild().getName();

                    // Checking if synchronization is needed
                    boolean needsUpdate = !Objects.equals(dbName, msgName);

                    if (needsUpdate) {

                        System.out.println("[DB] Server data is outdated. Synchronizing...");

                        // Getting current time
                        LocalDateTime now = LocalDateTime.now();

                        // Formatting current datetime
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String nowFormatted = now.format(formatter);

                        // Updating values with message data
                        setServerID(dbServerID);
                        setName(msgName);
                        setRole(dbRole);
                        setLastUpdateTime(nowFormatted);

                        System.out.println("[DB] Server successfully updated in the database!");

                        update = true;

                    } else {
                        
                        System.out.println("[DB] Server data is up-to-date. No changes needed.");

                        // Assigning values normally
                        setServerID(dbServerID);
                        setName(dbName);
                        setRole(dbRole);
                        setLastUpdateTime(dbLastUpdate);
                    }

                } else {

                    System.out.println("[DB] ERROR: Server marked as 'exists' but not found in the database!");
                }

                // Closing resources
                rs.close();
                ps.close();
                conn.close();
                System.out.println("[DB] Resources closed. Server building complete.");

            } catch (SQLException e) {

                System.out.println("[DB] Exception occurred while building server:");
                e.printStackTrace();
            }

            System.out.println("───────────────────────────────────────────────\n");

        // If the server is not in the database, use messageEvent values
        } else {

            System.out.println("- Getting the server attributes from the MESSAGE...");

            serverID = messageEvent.getGuild().getId();
            name = messageEvent.getGuild().getName();
            role = Utils.roles[0]; // free

            // Getting current time
            LocalDateTime now = LocalDateTime.now();

            // Formatting current datetime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastUpdateTime = now.format(formatter);
        }

        // Logging final server data
        System.out.println("- Server info:");
        System.out.println("    ID: " + serverID);
        System.out.println("    Name: " + name);
        System.out.println("    Role: " + role);
        System.out.println("    Last update time: " + lastUpdateTime);
        System.out.println();

        return update;
    }


    // Verifies if the server exists in the database based on the messageEvent
    public boolean serverExists(MessageReceivedEvent messageEvent) {

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting server verification process...");

        boolean serverExists = false;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // Getting server ID from the incoming message
            String serverID = messageEvent.getGuild().getId();
            System.out.println("[DB] Incoming server ID: " + serverID);

            // 1. Connecting to the database
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Preparing the SQL query
            String query = "SELECT * FROM Server WHERE id = ?";
            System.out.println("[DB] Prepared query: " + query);
            ps = conn.prepareStatement(query);
            ps.setString(1, serverID);
            System.out.println("[DB] Parameter 1 set as: " + serverID);

            // 3. Executing the query
            rs = ps.executeQuery();
            System.out.println("[DB] Query executed. Analyzing result...");

            if (rs.next()) {

                System.out.println("[DB] Server found in database!");
                serverExists = true;

            } else {

                System.out.println("[DB] Server not found!");
            }

        } catch (SQLException e) {

            System.out.println("[DB] Exception occurred while verifying server existence:");
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. Verification complete.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }

        System.out.println("[DB] Final result: serverExists = " + serverExists);
        System.out.println("───────────────────────────────────────────────\n");

        return serverExists;
    }


    public boolean serverVerify(Server server, Command command, User user, boolean serverExists, MessageReceivedEvent messageEvent){

        System.out.println("- CONTEXT: Server");

        boolean isValid = true;
        String fullCommand = Utils.getfullCommand(command);
        boolean isRegisterServer = fullCommand.equals(Utils.publicCommands[1]);

        // Building the member object
        Member member = new Member();
        boolean memberExists = member.memberExists(messageEvent);

        System.out.println("- Building Member object...");
        member.memberBuilder(memberExists, server, user, messageEvent);

        // NOT registered server
        if (!serverExists){

            // !register-server           
            if (isRegisterServer){

                System.out.println("- Executing (" + Utils.publicCommands[1] + ")...");

                boolean isAdmin = member.isAdmin(messageEvent);

                System.out.println("- Is the user an admin? (" + String.valueOf(isAdmin).toUpperCase() + ")");

                if (isAdmin){

                    // Executing
                    RegisterServerCommand registerServerCommand = new RegisterServerCommand();
                    registerServerCommand.registerServerCommandBuilder(command, server);
                    registerServerCommand.exec(messageEvent);
                    System.out.println("- Server registered with success!");

                } else {

                    messageEvent.getChannel().sendMessage("You need **ADMIN** permission to do that!").queue();
                    isValid = false;
                }

            // All other commands  
            } else {

                messageEvent.getChannel().sendMessage("This server is **NOT** registered yet! Please, try **" + Utils.publicCommands[1] + "** to get access to **ALL** my features.").queue();

                isValid = false;
            }

        // Registered server (free or premium)
        } else {     

            // If the Server and the User are already registered, we need to check the CONTEXT role to see if it is a valid request!

            // Source flag
            // User tried to request a DM ONLY command
            if (Utils.isDmOnlyCommand(command)){

                System.out.println("- (" + fullCommand + ") can be requested only in dm's!");
                messageEvent.getChannel().sendMessage("**" + fullCommand + "** can be requested only in **" + "DM's**, not in servers!").queue();

                isValid = false;
            }
    
            // Free server
            if (isValid && role.equals(Utils.roles[0])){

                System.out.println("- Free server request!");

                // Role flag
                // User tried to request a PREMIUM SERVER command
                if (Utils.isPremiumServerCommand(command)){
                            
                    System.out.println("- Only premium servers can request \"" + fullCommand + "\"");
                    messageEvent.getChannel().sendMessage("Only **" + "PREMIUM** servers can request **" + fullCommand + "**").queue();

                    isValid = false;
                }

                // Requests limit overflow
                if (command.getUser().getRequests() > Utils.MAX_REQUESTS){

                    System.out.println("- This user has already reached his daily requests limit!");
                    messageEvent.getChannel().sendMessage("This user has already reached his **DAILY REQUESTS** limit!").queue();

                    isValid = false;
                }

                // Treating the command requested
                // TODO: Treat the commands available for this role

                if (isValid){

                    // if (fullCommand.equals(Utils.freeServerCommands[?])){}

                }

            // Premium server
            } else if (isValid && role.equals(Utils.roles[1])){

                System.out.println("- Premium server request!");

                // Treating the command requested
                // TODO: Treat the commands available for this role

                // if (fullCommand.equals(Utils.premiumServerCommands[?])){}
            }
        }

        // Stores the member if it doesn't exist (for first-time requests)
        if (!memberExists && isValid){

            member.memberStorage();
        }

        return isValid;
    }

    public void serverStorage() {
       
        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting Server storage process...");

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;

        try {

            // 1. Getting server info from Server object

            System.out.println("[DB] Server info:");
            System.out.println(" - Discord ID      : " + serverID);
            System.out.println(" - Name            : " + name);
            System.out.println(" - Role            : " + role);
            System.out.println(" - Last Update Time: " + lastUpdateTime);

            // 2. Connecting to DB
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 3. Checking if server is already registered
            String select = "SELECT id_server FROM Server WHERE id = ?";
            psSelect = conn.prepareStatement(select);
            psSelect.setString(1, serverID);
            rs = psSelect.executeQuery();

            if (rs.next()) {
                System.out.println("[DB] Server is already registered. Aborting insertion.");
                return;
            }

            // 4. Preparing INSERT
            String insert = """
                INSERT INTO Server (
                    id, name, role, last_update_time
                ) VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    name = VALUES(name),
                    role = VALUES(role),
                    last_update_time = VALUES(last_update_time)
            """;

            System.out.println("[DB] Prepared query: " + insert);

            psInsert = conn.prepareStatement(insert);
            psInsert.setString(1, serverID); 
            psInsert.setString(2, name);
            psInsert.setString(3, role);
            psInsert.setString(4, lastUpdateTime);

            System.out.println("[DB] Parameters set:");
            System.out.println(" - id               : " + serverID);
            System.out.println(" - name             : " + name);
            System.out.println(" - role             : " + role);
            System.out.println(" - last_update_time : " + lastUpdateTime);

            // 5. Executing
            psInsert.executeUpdate();
            System.out.println("[DB] Server successfully inserted!");

        } catch (SQLException e) {
            System.out.println("[DB] Exception occurred while inserting Server:");
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. Server storage complete.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }

        System.out.println("───────────────────────────────────────────────\n");
    }
}

