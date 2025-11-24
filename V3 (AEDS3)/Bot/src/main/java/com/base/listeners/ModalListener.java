package com.base.listeners;

import java.util.ArrayList;
import java.util.Scanner;

import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Employee.EmployeeDAO;
import com.base.CRUD_Meeting.Meeting;
import com.base.CRUD_Meeting.MeetingDAO;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        System.out.println("- InteractionEvent");

        String modal = event.getModalId();

        if (modal.equals("registerFirst")) {

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
            String idEmployee = event.getValue("fim").getAsString();
            Scanner sc = new Scanner(idEmployee);
            try {
                if (sc.hasNextInt()) {
                    MeetingDAO meetingDAO = new MeetingDAO();
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    // Cria o meeting
                    Meeting meeting = new Meeting();
                    meeting.setName(meetingName);
                    meeting.setDescription(description);
                    meeting.setDate(date);
                    meeting.setStartTime(startTime);
                    meeting.setIdEmployee(Integer.parseInt(idEmployee));
                    Employee user = employeeDAO.searchEmployee(Integer.parseInt(idEmployee));
                    if (user == null) {
                        event.reply("```Employee does not exist!```")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        // "Serializa" e "desserializa" na hora (simula insert + read)
                        byte[] bytes = meeting.toByteArray();
                        Meeting meetingCopy = new Meeting();
                        meetingCopy.fromByteArray(bytes);
                        MeetingDAO.insertMeeting(meeting);

                        // Lista local para armazenar meetings

                        event.reply("```Reunião criada com sucesso!```\n**ID:** " + meeting.getId() + "\n**Tema:** "
                                + meetingName + "\n**Descrição:** " + description)
                                .setEphemeral(true)
                                .queue();
                    }
                }else{
                    event.reply("```Employeeid must be an integer```")
                                .setEphemeral(true)
                                .queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
