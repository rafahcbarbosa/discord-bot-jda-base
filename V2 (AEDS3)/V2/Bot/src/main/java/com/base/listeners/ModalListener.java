package com.base.listeners;

import java.util.ArrayList;

import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Meeting.Meeting;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalListener extends ListenerAdapter{
    
    @Override
    public void onModalInteraction (ModalInteractionEvent event){

        System.out.println("- InteractionEvent");

        String modal = event.getModalId();

        if (modal.equals("registerFirst")){

            String name = event.getValue("nome").getAsString();
            String email = event.getValue("email").getAsString();
            String phoneNumber = event.getValue("telefone").getAsString();
            String birthdate = event.getValue("nascimento").getAsString();
            String role = event.getValue("seleção-cargo").getAsString();

            System.out.println("===== REGISTRATION FORM DATA =====");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Phone Number: " + phoneNumber);
            System.out.println("Birthdate: " + birthdate);
            System.out.println("Role: " + role);
            System.out.println("==================================");


            try {

                Employee employee = new Employee(name, email, phoneNumber, birthdate, role);
                employee.insertEmployee();

                // Debug
                Employee employee2 = employee.searchEmployee();

                System.out.println("===== DB SEARCH FORM DATA =====");
                System.out.println("ID: " + employee2.getName());
                System.out.println("Name: " + employee2.getName());
                System.out.println("Email: " + employee2.getEmail());
                System.out.println("Phone Number: " + employee2.getPhoneNumber());
                System.out.println("Birthdate: " + employee2.getBirthdate());
                System.out.println("Role: " + employee2.getRole());
                System.out.println("==================================");
                // Debug
                

                event.reply("Você foi registrado com sucesso!\n\n **ID:** " + employee.getId())
                .setEphemeral(true)
                .queue();
    
            } catch (Exception e) {

                e.printStackTrace();
            }

        } else if (modal.equals("meetingFirst")) {

            String meetingName = event.getValue("tema").getAsString();
            String description = event.getValue("descrição").getAsString();
            String date = event.getValue("data").getAsString();
            String startTime = event.getValue("início").getAsString();
            String finishTime = event.getValue("fim").getAsString();

            try {
                int idEmployee = 1;

                // Cria o meeting
                Meeting meeting = new Meeting();
                meeting.setName(meetingName);
                meeting.setDescription(description);
                meeting.setDate(date);
                meeting.setStartTime(startTime);
                meeting.setEndTime(finishTime);
                meeting.setIdEmployee(idEmployee);

                // "Serializa" e "desserializa" na hora (simula insert + read)
                byte[] bytes = meeting.toByteArray();
                Meeting meetingCopy = new Meeting();
                meetingCopy.fromByteArray(bytes);

                // Lista local para armazenar meetings
                ArrayList<Meeting> meetings = new ArrayList<>();
                meetings.add(meetingCopy);

                // Imprime na moral do cavalo
                System.out.println("===== MEETING FORM DATA =====");
                for (Meeting m : meetings) {
                    System.out.println("ID: " + m.getId() +
                                    " | Tema: " + m.getName() +
                                    " | Descrição: " + m.getDescription() +
                                    " | Data: " + m.getDate() +
                                    " | Início: " + m.getStartTime() +
                                    " | Término: " + m.getEndTime() +
                                    " | ID Employee: " + m.getIdEmployee());
                }
                System.out.println("==============================");

                event.reply("```Reunião criada com sucesso!```\n**ID:** " + meeting.getId() + "\n**Tema:** " + meetingName + "\n**Descrição:** " + description)
                .setEphemeral(true)
                .queue();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
