package com.base.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.base.util.Utils;

// Roles
import net.dv8tion.jda.api.entities.Role;
// Message event
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Member {
    
    private Server server;
    private User user;
    private String nickname;
    private String[] roles;
    private String serverAvatarURL;
    private String joinedAt;
    private String lastUpdateTime;

    public Member(){}

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getServerAvatarURL() {
        return serverAvatarURL;
    }
    
    public void setServerAvatarURL(String serverAvatarURL) {
        this.serverAvatarURL = serverAvatarURL;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }
        
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    // Builds a Member object based on a messageEvent and database state
    public boolean memberBuilder(boolean memberExists, Server server, User user, MessageReceivedEvent messageEvent) {

        boolean update = false;

        // Extract raw info from the messageEvent (always needed)
        net.dv8tion.jda.api.entities.Member memberRaw = messageEvent.getMember();

        String msgNickname = memberRaw.getEffectiveName();
        String msgAvatarURL = memberRaw.getEffectiveAvatar().getUrl();

        List<String> rolesRaw = memberRaw.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String msgRolesString = String.join(";", rolesRaw);
        String[] msgRolesArray = rolesRaw.toArray(new String[0]);

        OffsetDateTime joinedAtRaw = memberRaw.getTimeJoined();
        LocalDateTime localJoinedAt = joinedAtRaw.toLocalDateTime();
        DateTimeFormatter formatterJoinedAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String msgJoinedAt = localJoinedAt.format(formatterJoinedAt);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatterNow = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowFormatted = now.format(formatterNow);

        if (memberExists) {

            System.out.println("───────────────────────────────────────────────");
            System.out.println("[DB] Starting member construction from database...");
            System.out.println("[DB] Incoming user ID: " + user.getUserID());
            System.out.println("[DB] Incoming server ID: " + server.getServerID());

            try {

                // 1. Connecting to the database
                System.out.println("[DB] Connecting to database...");
                Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
                System.out.println("[DB] Connection established successfully!");

                // 2. Preparing SQL query
                String query = """
                    SELECT 
                        m.nickname,
                        m.roles,
                        m.avatar_url,
                        m.joined_at,
                        m.last_update_time
                    FROM Member m
                    WHERE m.id_user = (
                        SELECT id_user FROM User WHERE id = ?)
                    AND m.id_server = (
                        SELECT id_server FROM Server WHERE id = ?)
                """;

                System.out.println("[DB] Prepared query:\n" + query);
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, user.getUserID());
                ps.setString(2, server.getServerID());
                System.out.println("[DB] Parameter 1 set as: " + user.getUserID());
                System.out.println("[DB] Parameter 2 set as: " + server.getServerID());

                // 3. Executing query
                ResultSet rs = ps.executeQuery();
                System.out.println("[DB] Query executed. Analyzing result...");

                if (rs.next()) {

                    System.out.println("[DB] Member found in database!");

                    // Extract values from database
                    String dbNickname       = rs.getString("nickname");
                    String dbRolesString    = rs.getString("roles");
                    String dbAvatarURL      = rs.getString("avatar_url");
                    String dbJoinedAt       = rs.getString("joined_at");
                    String dbLastUpdateTime = rs.getString("last_update_time");

                    boolean needsUpdate =

                        !Objects.equals(dbNickname, msgNickname) ||
                        !Objects.equals(dbAvatarURL, msgAvatarURL) ||
                        !Objects.equals(dbRolesString, msgRolesString);

                    if (needsUpdate) {

                        System.out.println("[DB] Member data is outdated. Synchronizing...");
                        this.nickname = msgNickname;
                        this.roles = msgRolesArray;
                        this.serverAvatarURL = msgAvatarURL;
                        this.joinedAt = msgJoinedAt;
                        this.lastUpdateTime = nowFormatted;

                        update = true;

                    } else {

                        System.out.println("[DB] Member data is up-to-date. No changes needed.");
                        this.nickname = dbNickname;
                        this.roles = dbRolesString != null ? dbRolesString.split(";") : new String[0];
                        this.serverAvatarURL = dbAvatarURL;
                        this.joinedAt = dbJoinedAt;
                        this.lastUpdateTime = dbLastUpdateTime;
                    }

                } else {

                    System.out.println("[DB] ERROR: Member marked as 'exists' but not found in the database!");
                    this.nickname = "(undefined)";
                    this.roles = new String[0];
                    this.serverAvatarURL = "null";
                    this.joinedAt = "1970-01-01 00:00:00";
                    this.lastUpdateTime = nowFormatted;
                }

                // Closing resources
                rs.close();
                ps.close();
                conn.close();
                System.out.println("[DB] Resources closed. Member building complete.");

            } catch (SQLException e) {

                System.out.println("[DB] Exception occurred while building member:");
                e.printStackTrace();
            }

            System.out.println("───────────────────────────────────────────────\n");

        } else {

            System.out.println("- Member not found. Creating new one based on message...");
            nickname = msgNickname;
            roles = msgRolesArray;
            serverAvatarURL = msgAvatarURL;
            joinedAt = msgJoinedAt;
            lastUpdateTime = nowFormatted;
        }

        // Final attribution (from the messageEvent)
        this.server = server;
        this.user = user;

        // Logging
        System.out.println("Member info:");
        System.out.println("    Server: " + this.server.getName() + " (ID: " + this.server.getServerID() + ")");
        System.out.println("    User: " + this.user.getUsername() + " (ID: " + this.user.getUserID() + ")");
        System.out.println("    Nickname: " + nickname);
        System.out.print("    Roles: ");

        if (roles.length > 0) {

            for (int i = 0; i < roles.length; i++) {

                System.out.print("[" + i + "] |" + roles[i] + "| ");
            }

            System.out.println();

        } else {

            System.out.println("(none)");
        }
        
        System.out.println("    Server Avatar URL: " + serverAvatarURL);
        System.out.println("    Joined at: " + joinedAt);
        System.out.println("    Last update time: " + lastUpdateTime);
        System.out.println();

        return update;
    }

    // Verifies if the member exists in the database based on the messageEvent
    public boolean memberExists(MessageReceivedEvent messageEvent) {

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting member verification process...");

        boolean memberExists = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Getting user and server Discord IDs from the message
            String discordUserID = messageEvent.getAuthor().getId();
            String discordServerID = messageEvent.getGuild().getId();

            // 1. Connecting to database
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Preparing the query using subqueries to find the foreign keys
            String query = """
                SELECT * FROM Member
                WHERE id_user = (
                    SELECT id_user FROM User WHERE id = ?)
                AND id_server = (
                    SELECT id_server FROM Server WHERE id = ?)
            """;

            System.out.println("[DB] Prepared query:\n" + query);

            ps = conn.prepareStatement(query);
            ps.setString(1, discordUserID);
            ps.setString(2, discordServerID);
            System.out.println("[DB] Parameter 1 set as: " + discordUserID);
            System.out.println("[DB] Parameter 2 set as: " + discordServerID);

            // 3. Executing the query
            rs = ps.executeQuery();
            System.out.println("[DB] Query executed. Analyzing result...");

            if (rs.next()) {
                System.out.println("[DB] Member found in database!");
                memberExists = true;
            } else {
                System.out.println("[DB] Member not found.");
            }

        } catch (SQLException e) {
            System.out.println("[DB] Exception occurred while verifying member existence:");
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

        System.out.println("[DB] Final result: memberExists = " + memberExists);
        System.out.println("───────────────────────────────────────────────\n");

        return memberExists;
    }


    public boolean isAdmin(MessageReceivedEvent messageEvent) {

        net.dv8tion.jda.api.entities.Member member = messageEvent.getMember();
        boolean isOwner = messageEvent.getGuild().getOwnerId().equals(member.getId());
        boolean hasAdmin = member.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR);

        return isOwner || hasAdmin;
    }

    public void memberStorage(){

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting Member insertion...");
        System.out.println("[DB] Discord User ID: " + this.user.getUserID());
        System.out.println("[DB] Discord Server ID: " + this.server.getServerID());

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psServer = null;
        PreparedStatement psInsert = null;
        ResultSet rsUser = null;
        ResultSet rsServer = null;

        try {

            // 1. Connecting
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Fetching id_user from User table
            String queryUser = "SELECT id_user FROM User WHERE id = ?";
            psUser = conn.prepareStatement(queryUser);
            psUser.setString(1, this.user.getUserID());
            rsUser = psUser.executeQuery();

            int idUser = -1;
            if (rsUser.next()) {
                idUser = rsUser.getInt("id_user");
                System.out.println("[DB] id_user found: " + idUser);
            } else {
                System.out.println("[DB] ERROR: User not found in database. Aborting.");
                return;
            }

            // 3. Fetching id_server from Server table
            String queryServer = "SELECT id_server FROM Server WHERE id = ?";
            psServer = conn.prepareStatement(queryServer);
            psServer.setString(1, this.server.getServerID());
            rsServer = psServer.executeQuery();

            int idServer = -1;
            if (rsServer.next()) {
                idServer = rsServer.getInt("id_server");
                System.out.println("[DB] id_server found: " + idServer);
            } else {
                System.out.println("[DB] ERROR: Server not found in database. Aborting.");
                return;
            }

            // 4. Preparing insert into Member
            String insert = """
                INSERT INTO Member (
                    id_user, id_server, nickname, roles,
                    avatar_url, joined_at, last_update_time
                ) VALUES (?, ?, ?, ?, ?, ?, NOW())
                ON DUPLICATE KEY UPDATE
                    nickname = VALUES(nickname),
                    roles = VALUES(roles),
                    avatar_url = VALUES(avatar_url),
                    joined_at = VALUES(joined_at),
                    last_update_time = NOW()
            """;

            System.out.println("[DB] Prepared query:\n" + insert);
            psInsert = conn.prepareStatement(insert);
            psInsert.setInt(1, idUser);
            psInsert.setInt(2, idServer);
            psInsert.setString(3, this.nickname);
            psInsert.setString(4, String.join(",", this.roles));
            psInsert.setString(5, this.serverAvatarURL);
            psInsert.setString(6, this.joinedAt);

            // 5. Logging parameters
            System.out.println("[DB] Parameters set:");
            System.out.println("    - id_user         = " + idUser);
            System.out.println("    - id_server       = " + idServer);
            System.out.println("    - nickname        = " + this.nickname);
            System.out.println("    - roles           = " + String.join(",", this.roles));
            System.out.println("    - serverAvatarURL = " + this.serverAvatarURL);
            System.out.println("    - joinedAt        = " + this.joinedAt);

            // 6. Executing insert
            psInsert.executeUpdate();
            System.out.println("[DB] Member successfully inserted!");

        } catch (SQLException e) {

            System.out.println("[DB] Exception occurred while inserting Member:");
            e.printStackTrace();

        } finally {

            try {

                if (rsUser != null) rsUser.close();
                if (rsServer != null) rsServer.close();
                if (psUser != null) psUser.close();
                if (psServer != null) psServer.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. Member storage complete.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }

        System.out.println("───────────────────────────────────────────────\n");
    }

}

