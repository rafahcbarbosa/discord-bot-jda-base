package com.base.infra;

// === JDA - Bot initialization ===

// Builds the bot instance using your token
import net.dv8tion.jda.api.JDABuilder;

// JDA core bot instance, returned after build()
import net.dv8tion.jda.api.JDA;

// === JDA - Gateway Intents ===
// Grants permission to read message contents — required for onMessageReceived to work
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    // This method works on loop, administrated by the JDA library
    public static void main(String[] args) throws Exception {
        
        // TODO: Add bot token
        String token = "";

        // Creates a builder for the bot (builds the object based on the token)
        JDABuilder builder = JDABuilder.createDefault(token);

        //Giving the permissions to the bot (server and dm's)
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES);
        
        // Indicates that the class BotListener is the one responsable for receiving ALL Discord Events
        builder.addEventListeners(new BotListener());

        // Stars the bot based on the builder setup (iniciates the 24/7 loop)
        JDA jda = builder.build();

        // Waits for the bot to be 100% online
        jda.awaitReady();
    }
}
