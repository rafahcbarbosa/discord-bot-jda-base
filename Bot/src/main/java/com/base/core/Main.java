package com.base.core;

import com.base.util.Utils;
import com.base.listeners.BotListener;
import com.base.listeners.ModalListener;
import com.base.listeners.ReadyListener;
import com.base.listeners.SlashCommandListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        JDA jda = JDABuilder.createDefault(Utils.apiToken)
                            .enableIntents

                                (GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.DIRECT_MESSAGES,
                                GatewayIntent.GUILD_MEMBERS)

                            .addEventListeners
                                
                                (new BotListener(),
                                new ReadyListener(),
                                new ModalListener(),
                                new SlashCommandListener())
                                
                            .build();

        jda.awaitReady();

    }
}
