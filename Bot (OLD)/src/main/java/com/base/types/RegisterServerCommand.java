package com.base.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.base.core.Command;
import com.base.core.Server;
import com.base.util.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


// !register-server class
public class RegisterServerCommand extends Command {
    
    private Server server;

    public RegisterServerCommand(){}

    public void registerServerCommandBuilder(Command base, Server server){

        this.setType(base.getType());
        this.setUser(base.getUser());
        this.setParameters(base.getParameters());
        this.setRequestTime(base.getRequestTime());
        this.setServer(server);    
        server.setRole(Utils.roles[0]);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lastUpdateTime = now.format(formatter);
    
        server.setLastUpdateTime(lastUpdateTime);

    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void exec(MessageReceivedEvent messageReceivedEvent){

        // Storing all the information on the database
        System.out.println("- Storing Server object...");
        server.serverStorage();
        System.out.println("- Server stored with success!");

        System.out.println("- Storing Command object...");
        commandStorage();
        System.out.println("- Command stored with success!");

        System.out.println("- Storing RegisterServerCommand object...");
        storageRegisterServerCommand();
        System.out.println("- RegisterServerCommand stored with success!");

        messageReceivedEvent.getChannel().sendMessage("Server **" + getServer().getName() + " **(___" + getServer().getServerID() + "___) registered with **success!**").queue();

    }

    public void storageRegisterServerCommand() {

        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting RegisterServerCommand insertion...");
        System.out.println("[DB] Discord User ID: " + this.getUser().getUserID());
        System.out.println("[DB] Discord Server ID: " + this.getServer().getServerID());

        Connection conn = null;
        PreparedStatement psCommand = null;
        PreparedStatement psServer = null;
        PreparedStatement psInsert = null;
        ResultSet rsCommand = null;
        ResultSet rsServer = null;

        try {

            // 1. Connecting
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Get internal id_user
            String queryCommand = """
                SELECT id_command FROM Command
                WHERE id_user = (SELECT id_user FROM User WHERE id = ?)
                ORDER BY id_command DESC LIMIT 1
            """;
            psCommand = conn.prepareStatement(queryCommand);
            psCommand.setString(1, this.getUser().getUserID());
            rsCommand = psCommand.executeQuery();

            int idCommand = -1;

            if (rsCommand.next()) {

                idCommand = rsCommand.getInt("id_command");
                System.out.println("[DB] Found id_command: " + idCommand);

            } else {

                System.out.println("[DB] No command found for this user. Aborting.");
                return;
            }

            // 3. Get internal id_server
            String queryServer = "SELECT id_server FROM Server WHERE id = ?";
            psServer = conn.prepareStatement(queryServer);
            psServer.setString(1, this.getServer().getServerID());
            rsServer = psServer.executeQuery();

            int idServer = -1;
            if (rsServer.next()) {

                idServer = rsServer.getInt("id_server");
                System.out.println("[DB] Found id_server: " + idServer);

            } else {

                System.out.println("[DB] No server found with this Discord ID. Aborting.");
                return;
            }

            // 4. Insert into RegisterServerCommand
            String insert = "INSERT INTO RegisterServerCommand (id_command, id_server) VALUES (?, ?)";
            System.out.println("[DB] Prepared query: " + insert);
            psInsert = conn.prepareStatement(insert);
            psInsert.setInt(1, idCommand);
            psInsert.setInt(2, idServer);

            System.out.println("[DB] Parameters set:");
            System.out.println(" - id_command: " + idCommand);
            System.out.println(" - id_server: " + idServer);

            psInsert.executeUpdate();
            System.out.println("[DB] RegisterServerCommand successfully inserted!");

        } catch (SQLException e) {

            System.out.println("[DB] Exception occurred while inserting RegisterServerCommand:");
            e.printStackTrace();

        } finally {

            try {

                if (rsCommand != null) rsCommand.close();
                if (rsServer != null) rsServer.close();
                if (psCommand != null) psCommand.close();
                if (psServer != null) psServer.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. RegisterServerCommand storage complete.");

            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }

        System.out.println("───────────────────────────────────────────────\n");
    }
}
