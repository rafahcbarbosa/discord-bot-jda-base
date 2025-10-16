package com.base.listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Meeting.Meeting;
import java.util.ArrayList;

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
            try {
                int idEmployee = 1;
                    // TODO: handle exception
                 Meeting meeting = new Meeting(meetingName,description,date,startTime,finishTime,idEmployee);
                 Meeting meeting2 = new Meeting(meetingName,description,date,startTime,finishTime,idEmployee);
                 Meeting meeting3 = new Meeting(meetingName,description,date,startTime,finishTime,idEmployee);
                meeting.insertMeeting();
                
                ArrayList<Meeting> meetings4 = meeting.searchByFK(1);

                for (Meeting meeting5: meetings4) {
                    System.out.println("===== MEETING FORM DATA =====");
                    System.out.println("Tema: " + meeting5.getName());
                    System.out.println("Descrição: " + meeting5.getDescription());
                    System.out.println("Data: " +  meeting5.getDate());
                    System.out.println("Horário de início: " + meeting5.getStartTime());
                    System.out.println("Horário de término: " + meeting5.getEndTime());
                    System.out.println("==============================");
                
                }
                    // usa meeting5 normalmente
                    System.out.println("===== MEETING FORM DATA =====");
                    System.out.println("Tema: " + meetings4.get(0).getName());
                    System.out.println("Descrição: " + meeting2.getDescription());
                    System.out.println("Data: " +  meeting2.getDate());
                    System.out.println("Horário de início: " + meeting2.getStartTime());
                    System.out.println("Horário de término: " + meeting2.getEndTime());
                    System.out.println("==============================");
                
                System.out.println("teste");
                
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("teste");
            }
        }

        event.deferReply().queue();        
    }
}
