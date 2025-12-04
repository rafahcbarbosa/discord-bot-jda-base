package com.base.listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter{
   
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {}
    public void onModalInteraction(ModalInteractionEvent event) {}
    public void onButtonInteraction(ButtonInteractionEvent event) {}
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {}
    public void onReady(ReadyEvent event) {}
}