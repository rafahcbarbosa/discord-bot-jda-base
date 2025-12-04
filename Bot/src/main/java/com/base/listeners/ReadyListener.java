package com.base.listeners;

import com.base.util.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event){

        System.out.println("- ReadyEvent");
        TextChannel channel = event.getJDA().getTextChannelById(Utils.debugChannelID);

        if (channel != null){

            channel.sendMessage("Opa, tô online e pronto pro trabalho!").queue();
    
        } else{

            System.out.println("Channel not found!");
        }
        
        Guild server = event.getJDA().getGuildById(Utils.serverID);

        if (server != null){

            server.updateCommands().addCommands(

                // registro
                Commands.slash(Utils.command[0], Utils.description[0]),

                // criar-reunião
                Commands.slash(Utils.command[1], Utils.description[1]),

                // // atualizar-registro
                // Commands.slash(Utils.command[2], Utils.description[2]),

                
                Commands.slash(Utils.command[3], Utils.description[3]),
                //     .addOption(OptionType.STRING, "atualizar-reunião", "ID da reunião"),

                // deletar-registro
                Commands.slash(Utils.command[4], Utils.description[4])
                        .addOption(OptionType.STRING, "id", "ID do registro", true),

                // deletar-reunião
                Commands.slash(Utils.command[5], Utils.description[5])
                    .addOption(OptionType.STRING, "id", "ID da reunião", true),
                    
                // mostrar-registro
                Commands.slash(Utils.command[6], Utils.description[6])
                    .addOption(OptionType.STRING, "id", "ID do registro", true),

                // mostrar-reunião
                Commands.slash(Utils.command[7], Utils.description[7])
                    .addOption(OptionType.STRING, "id", "ID da reunião", true),

                // mostrar-todas-reuniões
                Commands.slash(Utils.command[8], Utils.description[8])
                    .addOption(OptionType.STRING, "id", "ID da reunião", true),

                // criar-membro
                Commands.slash(Utils.command[9], Utils.description[9]),

                // mostrar-membro
                Commands.slash(Utils.command[10], Utils.description[10])
                    .addOption(OptionType.STRING, "id", "ID do membro", true),

                // adicionar-membroReunião
                Commands.slash(Utils.command[11], Utils.description[11])
                    .addOption(OptionType.STRING, "id_employee", "ID do membro", true)
                    .addOption(OptionType.STRING, "id_meeting", "ID da reunião", true),

                // mostrar-membrosReunião
                Commands.slash(Utils.command[12], Utils.description[12])
                    .addOption(OptionType.STRING, "id", "ID da reunião", true),

                // mostrar-reuniõesDeMembro
                Commands.slash(Utils.command[13], Utils.description[13])
                    .addOption(OptionType.STRING, "id", "ID do membro", true),

                Commands.slash(Utils.command[14], Utils.description[14]),
                Commands.slash(Utils.command[15], Utils.description[15]),
                Commands.slash(Utils.command[16], Utils.description[16]),
                Commands.slash(Utils.command[17], Utils.description[17]),
                Commands.slash(Utils.command[18], Utils.description[18])
                .addOption(OptionType.STRING,"email","Email do employee",true),
                Commands.slash(Utils.command[19], Utils.description[19])
                .addOption(OptionType.STRING,"nome","Nome do User",true),
    
               /*  Commands.slash(Utils.command[16], Utils.description[16]).addOption(OptionType.String,"id",true),*/


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
