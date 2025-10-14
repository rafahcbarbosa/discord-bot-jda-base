package com.base.listeners;

import com.base.util.Utils;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter{
    
    @Override
    public void onReady(ReadyEvent event){

        System.out.println("- ReadyEvent");
        TextChannel channel = event.getJDA().getTextChannelById(Utils.debugChannelID);

        if (channel != null){

            channel.sendMessage("Opa, tô online e pronto pro trabalho!").queue();

        } else{

            System.out.println("Channel not found!");
        }
    }
}
