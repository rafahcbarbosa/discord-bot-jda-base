package com.base.infra;

import com.base.core.Command;
import com.base.core.Dm;
import com.base.core.Server;
import com.base.types.RegisterUserCommand;
import com.base.util.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// At this point, the command have already been validated as EXISTING and PROPRER for the user
public class Source {
    
    public static void sourceCheck(MessageReceivedEvent messageEvent, Command command){

        boolean execution = false;
        String fullCommand = Utils.getfullCommand(command);

        boolean isRegisterUser = (fullCommand).equals(Utils.publicCommands[0]);
        boolean isFromGuild = messageEvent.isFromGuild();

        // User registration has MAXIMUM PRIORITY
        // !register-user
        if (isRegisterUser){

            System.out.println("- Executing (" + Utils.publicCommands[0] + ")...");

            // Executing
            RegisterUserCommand registerUserCommand = new RegisterUserCommand();
            registerUserCommand.registerUserCommandBuilder(command);
            registerUserCommand.exec(messageEvent);
            
            execution = true;

            System.out.println("- User registered with success!");
        
        // ALL other commands
        } else {

            System.out.println("- Checking the command source (SERVER or DM)..."); 

            System.out.println("\n=========================");
            System.out.println("- TRUE: Server message");
            System.out.println("- FALSE: DM message");
            System.out.println("=========================\n");

            // Checking the message source (SERVER or DM)
            System.out.println("- Is the message from a SERVER? (" + String.valueOf(isFromGuild).toUpperCase() + ")");

            // Server message       
            if (isFromGuild){

                // Building the Server object
                Server server = new Server();

                boolean serverExists = server.serverExists(messageEvent);
                System.out.println("- Is this Server registered in the database? (" + String.valueOf(serverExists).toUpperCase() + ")");
                
                server.serverBuilder(serverExists, messageEvent);   

                execution = server.serverVerify(server, command, command.getUser(), serverExists, messageEvent);

            // DM message
            } else if (!isFromGuild){
                
                Dm dm = new Dm(command.getUser());

                execution = dm.dmVerify(command, messageEvent);     
            } 
        }

        // Checking if the command was executed with success
        if (execution){

            System.out.println("- (" + fullCommand + ") executed with success!");

        } else {

            System.out.println("- Couldn't execute (" + fullCommand + ")");
        }
    }
}
