package com.base.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

import com.base.forms.Builder;
import com.base.util.Utils;


public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){

        System.out.println("- Command event");

        String command = event.getName();

        // Registro
        if (command.equals(Utils.command[0])){

            Modal modal = Builder.registerFirst();
            event.replyModal(modal).queue();

            // Modal modal = Builder.registerSecond();
            // event.replyModal(modal).queue();

        // Reunião
        } else if (command.equals(Utils.command[1])){
            
            Modal modal = Builder.meetingFirst();
            event.replyModal(modal).queue();
        }
    }
}
