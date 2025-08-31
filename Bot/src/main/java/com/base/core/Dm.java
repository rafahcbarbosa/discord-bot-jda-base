package com.base.core;

import com.base.util.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Dm {

    private User user;

    public Dm(User user){

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Verifies the command requested and calls its execution method
    public boolean dmVerify(Command command, MessageReceivedEvent messageEvent){

        System.out.println("- CONTEXT: Dm");

        boolean isValid = true;

        String fullCommand = Utils.getfullCommand(command);

        // Source flag
        // User tried to request a SERVER ONLY command
        if (Utils.isServerOnlyCommand(command)){

            System.out.println("- (" + fullCommand + ") can be requested only in servers!");
            messageEvent.getChannel().sendMessage("**" + fullCommand + "** can be requested only in **" + "SERVERS**, not in dm's!").queue();
            
            isValid = false;
        }

        String role = user.getRole();

        // Free user
        if (role.equals(Utils.roles[0])){

            System.out.println("- Free user request!");

            // Role flag
            // User tried to request a PREMIUM DM command
            if (Utils.isPremiumDmCommand(command)){
                           
                System.out.println("- Only premium users can request \"" + fullCommand + "\"");
                messageEvent.getChannel().sendMessage("Only **" + "PREMIUM** users can request **" + fullCommand + "**").queue();

                isValid = false;
            }

            // Requests flag
            // Requests limit overflow
            if (user.getRequests() > Utils.MAX_REQUESTS){

                System.out.println("- This user has already reached his daily requests limit!");
                messageEvent.getChannel().sendMessage("This user has already reached his **DAILY REQUESTS** limit!").queue();

                isValid = false;
            }

            // Treating the command requested
            if (isValid){

                // TODO: Treat the commands available for this role

                // if (fullCommand.equals(Utils.freeDmCommands[?]))

            }

        // Premium user
        } else if (role.equals(Utils.roles[1])){

            System.out.println("- Premium user request!");

            // Treating the command requested
            if (isValid){

                // TODO: Treat the commands available for this role

                // if (fullCommand.equals(Utils.premiumDmCommands[?]))

            }
        }

        return isValid;
    }
}
