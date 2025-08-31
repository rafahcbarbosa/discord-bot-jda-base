package com.base.infra;
import java.util.Arrays;

import com.base.core.*;
import com.base.util.Utils;

// === JDA - Event system ===

// Base interface for handling any Discord event (mandatory for custom listeners)
import net.dv8tion.jda.api.hooks.EventListener;

// Generic superclass for all events (used in centralized event handling)
import net.dv8tion.jda.api.events.GenericEvent;

// === JDA - Message events ===

// Triggered when a message is received in a text channel or DM
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;


// application ID: 1393255950743375982
// public key: 61927889bca62f4fb8b6b3b90efc8fe2c636d86c015ed53ea651ead19b97471f
// API token: MTM5MzI1NTk1MDc0MzM3NTk4Mg.GdbJTI.JTDdcwGCvDQNdngS5Td9DBROl74KunCF7ECwlo

// Class that listens the events that happen on Discord. This is how the bot can interact with users (and vice-versa)
// It extends ListenerAdapter, a class wich has all the necessary methods for the user-bot communication.
// This class contains empty methods (Adapter Class), so we need to override only the ones we're using

class BotListener implements EventListener {

    // Re-writes ("@overrides") the onEvent method, already signed on EventListener class
    // Method that receives ALL Discord events and treat them correctly
    @Override
    public void onEvent(GenericEvent event){

        // Online verification
        if (event instanceof ReadyEvent){

            // DEBUG CMD
            System.out.println("- " + Utils.name + " is online!");

            ReadyEvent readyEvent = (ReadyEvent) event;

            // Specific channel to send this message (for debugging purposes only)
            String channelId = "1391108241106141224"; 
            TextChannel channel = readyEvent.getJDA().getTextChannelById(channelId);

            // DEBUG BOT
            if (channel != null){

                channel.sendMessage("Hello, I'm online and ready to work!").queue();

            } else{

                System.out.println("Channel not found!");
            }
        }

        // Commands treatment (starts when ANY type of message is received, from anywhere on Discord - that the bot has access)
        if (event instanceof MessageReceivedEvent){

            // Casting from GenericEvent to MessageReceivedEvent
            MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            
            // Ignores if it is a bot request (prevents LOOPING!)
            if (messageEvent.getAuthor().isBot()){  
                
                return;
            }

            // Transforming the object messageEvent (which has all user information) into a String
            String message = messageEvent.getMessage().getContentRaw();

            Utils.printHighlight(message);

            // Updating data base info (if necessary)
            Utils.updateInfo(messageEvent);

            // Checking if the message is a command or not
            if (!message.startsWith(Utils.PREFIX)){

                System.out.println("- Message doesn't have the PREFIX, so it's NOT a command. Request ignored!");

                return;  
            }

            // From now, the message is a command request. However, we need to check if it is VALID OR NOT and if the user can really do this request!
            System.out.println("- The message has the PREFIX" + " (" + Utils.PREFIX + ")");

            // Core variables
            Command command = new Command();
            User user = new User();
            String[] parameters = {};
            String type;
            boolean userExists;
        
            // Checking if the user EXISTS or NOT in the database (based on messageEvent info)
            userExists = user.userExists(messageEvent);
  
            System.out.println("- User exists in the database? (" + String.valueOf(userExists).toUpperCase() + ")");

            // Getting the command type (based on messageEvent and userExists)
            type = command.commandType(messageEvent, userExists, parameters); 

            // Now, we need to check if the command is VALID OR NOT (based on the command type)
            // Is the command type an EXCEPTION?

            boolean isCommandValid = true;

            // Valid command
            if (!Arrays.asList(Utils.typesException).contains(type)){

                System.out.println("- PROPER command");
                System.out.println("- VALID command!");

                // Building the User object 
                user.userBuilder(userExists, messageEvent);

                // Builds the command object
                command.commandBuilder(user, type, parameters);

            // Invalid command
            } else {

                isCommandValid = false;

                System.out.println("- IMPROPER command");
                System.out.println("- INVALID command!");

                // TODO: Add invalid commands treatment

                // User requested an UNEXISTING command (!unknown)
                if (type.equals(Utils.typesException[0])){

                    System.out.println("- User requested an unknown commad!");
                    messageEvent.getChannel().sendMessage("You requested an **UNKNOWN** command! Please, try again.").queue();
                }
                
                // User is NOT REGISTERED and tried to use a command (!not-registered)
                else if (type.equals(Utils.typesException[1])){

                    System.out.println("- User is NOT registered and requested a command than (" + Utils.publicCommands[0] + ")");
                    messageEvent.getChannel().sendMessage("You are **NOT** registered yet! Please, try **" + Utils.publicCommands[0] + "** to get access to **ALL** my features.").queue();
                }

                // User tried to use "!register-user", but is ALREADY REGISTERED (!user-already-registered)
                else if (type.equals(Utils.typesException[2])){
                    
                    System.out.println("- User is ALREADY registered and requested (" + Utils.publicCommands[0] + ") command");
                    messageEvent.getChannel().sendMessage("You are **ALREADY** registered!").queue();
                }

                // User tried to use "!register-server", but it is ALREADY REGISTERED (!server-already-registered)
                else if (type.equals(Utils.typesException[3])){

                    System.out.println("- Server is ALREADY registered and requested (" + Utils.publicCommands[1] + ") command");
                    messageEvent.getChannel().sendMessage("This server is **ALREADY** registered!").queue();
                }

                // User added parameters to a non parameterized command (!non-parameterized)
                else if (type.equals(Utils.typesException[4])){

                    messageEvent.getChannel().sendMessage("This command **CAN'T** have **PARAMETERS!**").queue();
                }

                // User didn't add parameters to a parameterized command (!parameterized)
                else if (type.equals(Utils.typesException[5])){

                    messageEvent.getChannel().sendMessage("This command **MUST** have **PARAMETERS!**").queue();
                }

                // User requested a DM only command on a server (!dm-only)
                else if (type.equals(Utils.typesException[6])){

                    messageEvent.getChannel().sendMessage("This command can be requested only in **DM's**, not in servers!").queue();
                }

                // User requested a SERVER only command on a dm (!server-only)               
                else if (type.equals(Utils.typesException[7])){

                    messageEvent.getChannel().sendMessage(" This command can be requested only in **SERVERS**, not in dm's!").queue();
                }
            }

            // At this point, the command object is FULLY built!
            
            if (isCommandValid){
                
                // Now, the command is 100% VALID and has been specified. We just need to EXECUTE it!                
                Source.sourceCheck(messageEvent, command);
            }
        }
    }
}