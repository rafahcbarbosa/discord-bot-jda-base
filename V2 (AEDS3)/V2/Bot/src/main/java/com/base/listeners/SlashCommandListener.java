package com.base.listeners;

import java.util.ArrayList;

import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Meeting.Meeting;
import com.base.forms.Builder;
import com.base.util.Utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;


public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){

        System.out.println("- Command event");

        String command = event.getName();
            
        try{
            // registro
            if (command.equals(Utils.command[0])){

                Modal modal = Builder.registerFirst();
                event.replyModal(modal).queue();

                // Modal modal = Builder.registerSecond();
                // event.replyModal(modal).queue();

            // criar-reunião
            } else if (command.equals(Utils.command[1])){
                
                Modal modal = Builder.meetingFirst();
                event.replyModal(modal).queue();
            
            // deletar-registro
            } else if (command.equals(Utils.command[4])){

                Employee employee = new Employee();
                int id = event.getOption("id").getAsInt();                          
                employee = employee.employeeDAO.searchEmployee(id);

                if (employee != null){

                    employee.deleteEmployee();

                    event.reply("Funcionário **(" + id + ")** excluído com sucesso!")
                        .setEphemeral(true)
                        .queue();

                    event.deferReply();
                    

                } else {
                    
                    event.reply("O funcionário **(" + id + ")** não existe!")
                        .setEphemeral(true)
                        .queue();
                        
                    event.deferReply();

                }

            // deletar-reunião
            } else if (command.equals(Utils.command[5])){

                Meeting meeting = new Meeting();
                int id = event.getOption("id").getAsInt();                          
                meeting = meeting.searchMeeting(id);

                if (meeting != null){

                    meeting.deleteMeeting();

                    event.reply("Reunião **(" + id + ")** excluída com sucesso!")
                        .setEphemeral(true)
                        .queue();

                    event.deferReply();
                    

                } else {
                    
                    event.reply("A reunião **(" + id + ")** não existe!")
                        .setEphemeral(true)
                        .queue(); 

                    event.deferReply();
                    
                }

            // mostrar-registro
            } else if (command.equals(Utils.command[6])){
                
                int id = event.getOption("id").getAsInt();
                Employee employee = new Employee().employeeDAO.searchEmployee(id);

                if (employee != null){

                    String info = """
                        ```INFORMAÇÕES DO FUNCIONÁRIO```
                        - **Nome:** %s
                        - **Telefone:** %s
                        - **E-mail:** %s
                        - **Data de Nascimento:** %s
                        - **Cargo:** %s
                        """.formatted(
                            employee.getName(),
                            employee.getPhoneNumber(),
                            employee.getEmail(),
                            employee.getBirthdate(),
                            employee.getRole()
                        );

                    event.reply(info)
                        .setEphemeral(true)
                        .queue();

                    event.deferReply();
                    

                } else {

                    event.reply("O funcionário **(" + id + ")** não existe!")
                        .setEphemeral(true)
                        .queue(); 

                    event.deferReply();

                }

            // mostrar-reunião
            } else if (command.equals(Utils.command[7])){

                int id = event.getOption("id").getAsInt();
                Meeting meeting = new Meeting().searchMeeting(id);

                if (meeting != null){

                    String info = """
                        ```INFORMAÇÕES DA REUNIÃO```
                        - **Tema:** %s
                        - **Descrição:** %s
                        - **Data:** %s
                        - **Início:** %s
                        - **Término:** %s
                        - **ID do Funcionário:** %s
                        """.formatted(
                            meeting.getName(),
                            meeting.getDescription(),
                            meeting.getDate(),
                            meeting.getStartTime(),
                            meeting.getEndTime(),
                            meeting.getIdEmployee()
                        );

                    event.reply(info)
                        .setEphemeral(true)
                        .queue();

                    event.deferReply();
                    
                } else {

                    event.reply("A reunião **(" + id + ")** não existe!")
                        .setEphemeral(true)
                        .queue(); 

                    event.deferReply();
                }
            
            // mostrar-todas-reuniões
            } else if (command.equals(Utils.command[8])){


                int id = event.getOption("id").getAsInt();
                Meeting meeting = new Meeting();

                ArrayList<Meeting> meetingArrayList = meeting.searchByFK(id);

                if (meetingArrayList.isEmpty()) {

                    event.reply("O usuário **(" + id + ")** não possui reuniões!")
                        .setEphemeral(true)
                        .queue(); 

                        event.deferReply();

                } else {

                    System.out.println("```TODAS REUNIÕES DO USUÁRIO (" + id + ")```");

                    for (Meeting m : meetingArrayList) {

                        String info = """
                            ```INFORMAÇÕES DA REUNIÃO %d```
                            - **Tema:** %s
                            - **Descrição:** %s
                            - **Data:** %s
                            - **Início:** %s
                            - **Término:** %s
                            - **ID do Funcionário:** %s
                            """.formatted(
                                m.getId(),
                                m.getName(),
                                m.getDescription(),
                                m.getDate(),
                                m.getStartTime(),
                                m.getEndTime(),
                                m.getIdEmployee()
                            );

                        event.reply(info)
                        .setEphemeral(true)
                        .queue();

                        event.deferReply();
                    }
                }
            }

            event.deferReply();

        } catch (Exception e){

            e.printStackTrace();
        }
    }
}
