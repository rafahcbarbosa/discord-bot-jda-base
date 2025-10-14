package com.base.listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.base.CRUD_Employee.Employee;

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

            } catch (Exception e) {

                e.printStackTrace();
            }

        } else if (modal.equals("meetingFirst")){

            
            String meetingName = event.getValue("tema").getAsString();
            String description = event.getValue("descrição").getAsString();
            String date = event.getValue("data").getAsString();
            String startTime = event.getValue("início").getAsString();
            String finishTime = event.getValue("fim").getAsString();

            System.out.println("===== MEETING FORM DATA =====");
            System.out.println("Tema: " + meetingName);
            System.out.println("Descrição: " + description);
            System.out.println("Data: " + date);
            System.out.println("Horário de início: " + startTime);
            System.out.println("Horário de término: " + finishTime);
            System.out.println("==============================");
        }

        event.deferReply().queue();        
    }
}
