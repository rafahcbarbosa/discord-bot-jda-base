package com.base.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.base.core.Command;
import com.base.util.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


// !register-user class
public class RegisterUserCommand extends Command {
    
    public RegisterUserCommand(){}

    public void registerUserCommandBuilder(Command base){

        this.setType(base.getType());
        this.setUser(base.getUser());
        this.setParameters(base.getParameters());
        this.setRequestTime(base.getRequestTime());
    }

    public void exec(MessageReceivedEvent messageReceivedEvent){

        System.out.println("- Storing the User object...");
        this.getUser().userStorage();
        System.out.println("- User stored with success!");

        System.out.println("- Storing the Command object...");
        commandStorage();
        System.out.println("- Command stored with success!");

        System.out.println("- Storing RegisterUserCommand object...");
        storageRegisterUserCommand();
        System.out.println("- RegisterUserCommand stored with success!");

        messageReceivedEvent.getChannel().sendMessage("User **" + getUser().getUsername() + " **(___" + getUser().getUserID() + "___) registered with **success!**").queue();
    }

    public void storageRegisterUserCommand() {
      
        System.out.println("───────────────────────────────────────────────");
        System.out.println("[DB] Starting RegisterUserCommand insertion...");
        System.out.println("[DB] Discord User ID: " + this.getUser().getUserID());

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;

        try {

            // 1. Connecting
            System.out.println("[DB] Connecting to database...");
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.DB_USER, Utils.DB_PASSWORD);
            System.out.println("[DB] Connection established successfully!");

            // 2. Getting latest command ID for this user
            String select = """
                SELECT id_command
                FROM Command
                WHERE id_user = (
                    SELECT id_user
                    FROM User
                    WHERE id = ?
                )
                ORDER BY id_command DESC
                LIMIT 1
            """;
            
            psSelect = conn.prepareStatement(select);
            psSelect.setString(1, this.getUser().getUserID());
            rs = psSelect.executeQuery();

            int idCommand = -1;
            if (rs.next()) {

                idCommand = rs.getInt("id_command");
                System.out.println("[DB] Latest command ID found: " + idCommand);
                
            } else {
                
                System.out.println("[DB] No command found for this user. Aborting.");
                return;
            }

            // 3. Preparing INSERT
            String insert = "INSERT INTO RegisterUserCommand (id_command) VALUES (?)";
            System.out.println("[DB] Prepared query: " + insert);
            psInsert = conn.prepareStatement(insert);
            psInsert.setInt(1, idCommand);

            System.out.println("[DB] Parameter set: id_command = " + idCommand);

            // 4. Executing
            psInsert.executeUpdate();
            System.out.println("[DB] RegisterUserCommand successfully inserted!");

        } catch (SQLException e) {

            System.out.println("[DB] Exception occurred while inserting RegisterUserCommand:");
            e.printStackTrace();

        } finally {

            try {
                if (rs != null) rs.close();
                if (psSelect != null) psSelect.close();
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
                System.out.println("[DB] Resources closed. RegisterUserCommand storage complete.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("───────────────────────────────────────────────\n");
    }
}
