package com.base.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.base.util.Utils;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// User information
public class User {

    private String userID; // Discord user id
    private String dmID; // Discord user dm channel id
    private String username; // Discord user username
    private String tag; // Discord user tag
    private String avatarURL; // Discord user avatar URL
    private boolean isBot; // If the user is a bot or not
    private String role; // Defines user privileges (free, premium, admin)
    private int requests; // How many requests have been made in the last 24 hours from the first request?
    private String firstRequestTime; // Time of the first request
    private String lastUpdateTime; // When the profile was last updated

    

    public String getUserID() {
        return userID;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getTag() {
        return tag;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }


    public String getAvatarURL() {
        return avatarURL;
    }


    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }


    public boolean getIsBot() {
        return isBot;
    }


    public void setIsBot(boolean isBot) {
        this.isBot = isBot;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public int getRequests() {
        return requests;
    }


    public void setRequests(int requests) {
        this.requests = requests;
    }


    public String getFirstRequestTime() {
        return firstRequestTime;
    }

    public void setFirstRequestTime(String firstRequestTime) {
        this.firstRequestTime = firstRequestTime;
    }

    
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime){

        this.lastUpdateTime = lastUpdateTime;
    }

    
    public String getDmID() {
        return dmID;
    }


    public void setDmID(String dmID) {
        this.dmID = dmID;
    }

    // Builds an User object based on a messageEvent
    public boolean userBuilder(boolean userExists, MessageReceivedEvent messageEvent) {

        boolean update = false;

        // If the user is on the database, collect the information from it!
        if (userExists){

            System.out.println("───────────────────────────────────────────────");
            System.out.println("[DB] Starting user construction from database...");
            System.out.println("[DB] Incoming user ID: " + messageEvent.getAuthor().getId());

            try {

                // Attempting connection
                System.out.println("[DB] Connecting to database...");
                Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);

                System.out.println("[DB] Connection established successfully!");

                // Preparing SQL query
                String query = """
                    SELECT 
                        u.id AS discord_id,
                        u.username,
                        u.tag,
                        u.avatar_url,
                        u.is_bot,
                        u.role,
                        u.requests,
                        u.first_request_time,
                        u.last_update_time,
                        u.id_dm
                    FROM User u
                    WHERE u.id = ?
                """;

                System.out.println("[DB] Prepared query: " + query);
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, messageEvent.getAuthor().getId());
                System.out.println("[DB] Parameter 1 set as: " + messageEvent.getAuthor().getId());

                // Executing query
                ResultSet rs = ps.executeQuery();
                System.out.println("[DB] Query executed. Analyzing result...");

                if (rs.next()) {

                    System.out.println("[DB] User found in database!");

                    // Extracting values from database
                    String dbUserID     = rs.getString("discord_id");
                    String dbUsername   = rs.getString("username");
                    String dbTag        = rs.getString("tag");
                    String dbAvatarURL  = rs.getString("avatar_url");
                    boolean dbIsBot     = rs.getBoolean("is_bot");
                    String dbRole       = rs.getString("role");
                    int dbRequests      = rs.getInt("requests");
                    String dbFirstTime  = rs.getString("first_request_time");
                    String dbLastTime   = rs.getString("last_update_time");
                    String dbDmID       = rs.getString("id_dm");

                    // Getting values from the incoming message
                    String msgUsername  = messageEvent.getAuthor().getName();
                    String msgTag       = messageEvent.getAuthor().getGlobalName();
                    String msgAvatarURL = messageEvent.getAuthor().getAvatarUrl();
                    boolean msgIsBot    = messageEvent.getAuthor().isBot();
                    String msgDmID      = messageEvent.getAuthor().openPrivateChannel().submit().join().getId();

                    // Checking if synchronization is needed
                    boolean needsUpdate =

                        !Objects.equals(dbUsername, msgUsername) ||
                        !Objects.equals(dbTag, msgTag) ||
                        !Objects.equals(dbAvatarURL, msgAvatarURL) ||
                        dbIsBot != msgIsBot ||
                        !Objects.equals(dbDmID, msgDmID);

                    if (needsUpdate){
                        
                        System.out.println("[DB] User data is outdated. Synchronizing...");

                        // Getting current time
                        LocalDateTime now = LocalDateTime.now();

                        // Formatting current datetime
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String nowFormatted = now.format(formatter);

                        // Updating values with message data
                        userID           = dbUserID;
                        username         = msgUsername;
                        tag              = msgTag;
                        avatarURL        = msgAvatarURL;
                        isBot            = msgIsBot;
                        role             = dbRole;
                        requests         = dbRequests;
                        firstRequestTime = dbFirstTime;
                        lastUpdateTime   = nowFormatted;
                        dmID             = msgDmID;

                        update = true;

                        System.out.println("[DB] User successfully updated in the database!");

                    } else {

                        System.out.println("[DB] User data is up-to-date. No changes needed.");

                        // Assigning values normally
                        userID           = dbUserID;
                        username         = dbUsername;
                        tag              = dbTag;
                        avatarURL        = dbAvatarURL;
                        isBot            = dbIsBot;
                        role             = dbRole;
                        requests         = dbRequests;
                        firstRequestTime = dbFirstTime;
                        lastUpdateTime   = dbLastTime;
                        dmID             = dbDmID;
                    }

                } else {

                    System.out.println("[DB] ERROR: User marked as 'exists' but not found in the database!");
                }

                // 6. Closing resources
                rs.close();
                ps.close();
                conn.close();
                System.out.println("[DB] Resources closed. User building complete.");

            } catch (SQLException e) {

                System.out.println("[DB] Exception occurred while building user:");
                e.printStackTrace();
            }

            System.out.println("───────────────────────────────────────────────\n");

        // If the user is not in the data base, use default values
        } else {

            System.out.println("- Getting the user atributes attributes from the MESSAGE...");

            userID = messageEvent.getAuthor().getId();
            username = messageEvent.getAuthor().getName();
            tag = messageEvent.getAuthor().getGlobalName();
            avatarURL = messageEvent.getAuthor().getEffectiveAvatarUrl();
            isBot = messageEvent.getAuthor().isBot();
            role = Utils.roles[0]; // free
            requests = 0;

            // Getting current time
            LocalDateTime now = LocalDateTime.now();

            // Formatting current datetime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            firstRequestTime = now.format(formatter);
            lastUpdateTime = now.format(formatter);

            // Creating the Dm object and setting it as a User object attribute
            PrivateChannel privateChannel = messageEvent.getAuthor()
                .openPrivateChannel()
                .submit()
                .join(); // This will block until the DM channel is opened

            dmID = privateChannel.getId();
        }

        System.out.println("- User info:");
        System.out.println("    ID: " + userID);
        System.out.println("    Username: " + username);
        System.out.println("    Tag: " + tag);
        System.out.println("    Avatar URL: " + avatarURL);
        System.out.println("    Is Bot: (" + String.valueOf(isBot).toUpperCase() + ")");
        System.out.println("    Role: " + role);
        System.out.println("    Request Count: " + requests);
        System.out.println("    First Request Time: " + firstRequestTime);
        System.out.println("    Last Update Time: " + lastUpdateTime);
        System.out.println("    Dm ID: " + dmID);

        System.out.println();

        return update;
    }


    
    // Verifies if the user is on the database based on the user ID
    public boolean userExists(MessageReceivedEvent messageEvent) {

        boolean userExists = false;
        String userId = messageEvent.getAuthor().getId();

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting user verification process...");
        System.out.println("[DB] Incoming user ID: " + userId);

        try {

            // 1. Attempting connection
            System.out.println("[DB] Connecting to database...");
            Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);

            System.out.println("[DB] Connection established successfully!");

            // 2. Preparing SQL query
            String query = "SELECT * FROM User WHERE id = ?";
            System.out.println("[DB] Prepared query: " + query);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            System.out.println("[DB] Parameter 1 set as: " + userId);

            // 3. Executing query
            ResultSet rs = ps.executeQuery();
            System.out.println("[DB] Query executed. Analyzing result...");

            if (rs.next()) {

                userExists = true;
                System.out.println("[DB] User found in database!");
                System.out.println("[DB] ID: " + rs.getString("id"));
                System.out.println("[DB] Role: " + rs.getString("role"));

            } else {

                System.out.println("[DB] User not found!");
            }

            // 4. Closing resources
            rs.close();
            ps.close();
            conn.close();
            System.out.println("[DB] Resources closed. Verification complete.");

        } catch (SQLException e) {

            System.out.println("[DB] Exception occurred:");
            e.printStackTrace();
        }

        System.out.println("[DB] Final result: userExists = " + userExists);
        System.out.println("───────────────────────────────────────────────\n");

        return userExists;
    }

    public void userStorage() {

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting user insertion process...");
        System.out.println("[DB] Incoming user ID: " + userID );

        try {
            
            // 1. Attempting connection
            System.out.println("[DB] Connecting to database...");
            Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");


            // 2. Preparing SQL INSERT query
           String query = """
                INSERT INTO User (
                    id, username, tag, avatar_url, is_bot,
                    role, requests, first_request_time,
                    last_update_time, id_dm
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    username = VALUES(username),
                    tag = VALUES(tag),
                    avatar_url = VALUES(avatar_url),
                    is_bot = VALUES(is_bot),
                    role = VALUES(role),
                    requests = VALUES(requests),
                    last_update_time = VALUES(last_update_time),
                    id_dm = VALUES(id_dm)
            """;

            System.out.println("[DB] Prepared query: " + query);
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, userID);
            ps.setString(2, username);
            ps.setString(3, tag);
            ps.setString(4, avatarURL);
            ps.setBoolean(5, isBot);
            ps.setString(6, role);
            ps.setInt(7, requests);
            ps.setString(8, firstRequestTime);
            ps.setString(9, lastUpdateTime);
            ps.setString(10, dmID);

            System.out.println("[DB] Parameters set:");
            System.out.println(" - ID                  : " + userID);
            System.out.println(" - Username            : " + username);
            System.out.println(" - Tag                 : " + tag);
            System.out.println(" - Avatar              : " + avatarURL);
            System.out.println(" - Is Bot              : " + isBot);
            System.out.println(" - Role                : " + role);
            System.out.println(" - Requests            : " + requests);
            System.out.println(" - First request time  : " + firstRequestTime);
            System.out.println(" - Last update time    : " + lastUpdateTime);
            System.out.println(" - Dm ID               : " + dmID);

            // 3. Executing insertion
            ps.executeUpdate();
            System.out.println("[DB] User successfully inserted!");

            // 4. Cleanup
            ps.close();
            conn.close();
            System.out.println("[DB] Resources closed. User storage complete.");

        } catch (SQLException e) {
            System.out.println("[DB] Exception occurred while inserting user:");
            e.printStackTrace();
        }

        System.out.println("───────────────────────────────────────────────\n");
    }
}
