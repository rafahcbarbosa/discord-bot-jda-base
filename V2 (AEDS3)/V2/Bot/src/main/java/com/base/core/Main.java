package com.base.core;

import com.base.util.Utils;
import com.base.listeners.BotListener;
import com.base.listeners.ModalListener;
import com.base.listeners.ReadyListener;
import com.base.listeners.SlashCommandListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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

        Guild server = jda.getGuildById(Utils.serverID);

        if (server != null){

            server.updateCommands().addCommands(

                Commands.slash(Utils.command[0], Utils.description[0]),
                Commands.slash(Utils.command[1], Utils.description[1]),
                Commands.slash("kill", "Desliga o bot (somente dono)").setDefaultPermissions(DefaultMemberPermissions.DISABLED)

            ).queue(

                success -> System.out.println("- Comandos registrados na guild!"),
                error -> error.printStackTrace()
            );

        } else {

            System.out.println("- Servidor não encontrado!");
        }
    }
}
