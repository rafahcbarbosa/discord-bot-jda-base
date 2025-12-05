package com.base.listeners;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.base.CRUD_User.User;
import com.base.CRUD_User.UserDAO;
import com.base.Encryption.Encriptor;
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
            try {
                String name = event.getValue("nome").getAsString();
                String email = event.getValue("email").getAsString().trim();
                String phoneNumber = event.getValue("telefone").getAsString();
                String birthdate = event.getValue("nascimento").getAsString();
                String role = event.getValue("seleção-cargo").getAsString();
                String emailRegex = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";

                Employee existingEmployee = EmployeeDAO.searchByEmail(Encriptor.Criptografar(email));
                System.out.println("===== REGISTRATION FORM DATA =====");
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Birthdate: " + birthdate);
                System.out.println("Role: " + role);
                System.out.println("==================================");

                System.out.println(existingEmployee != null);

                if (existingEmployee != null) {
                    event.reply("```Email já utilizado!```")
                            .setEphemeral(true)
                            .queue();
                } else if (!Pattern.matches(emailRegex, email)) {
                    event.reply("```Email Inválido```")
                            .setEphemeral(true)
                            .queue();
                } else {

                    Employee employee = new Employee(name, phoneNumber, email, birthdate, role);
                    EmployeeDAO.insertEmployee(employee);

                    // Debug

                    System.out.println("===== DB SEARCH FORM DATA =====");
                    System.out.println("ID: " + employee.getName());
                    System.out.println("Name: " + employee.getName());
                    System.out.println("Email: " + employee.getEmail());
                    System.out.println("Phone Number: " + employee.getPhoneNumber());
                    System.out.println("Birthdate: " + employee.getBirthdate());
                    System.out.println("Role: " + employee.getRole());
                    System.out.println("==================================");
                    // Debug

                    event.reply("Funcionário foi registrado com sucesso!\n\n **ID:** " + employee.getId())
                            .setEphemeral(true)
                            .queue();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (modal.equals("registerSecond")) {
            try {
                String name = event.getValue("nome").getAsString();
                String email = event.getValue("email").getAsString().trim();
                String phoneNumber = event.getValue("telefone").getAsString();
                String birthdate = event.getValue("nascimento").getAsString();
                String role = event.getValue("seleção-cargo").getAsString();
                String emailRegex = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";

                Employee existingEmployee = EmployeeDAO.searchByEmail(Encriptor.Criptografar(email));
                System.out.println("===== REGISTRATION FORM DATA =====");
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Birthdate: " + birthdate);
                System.out.println("Role: " + role);
                System.out.println("==================================");

                System.out.println(existingEmployee != null);

                if (existingEmployee == null) {
                    event.reply("```Usuário não existente```")
                            .setEphemeral(true)
                            .queue();
                } else {

                    Employee employee = new Employee(existingEmployee.getId(), name, phoneNumber, email, birthdate,
                            role);

                    if (EmployeeDAO.updateEmployee(employee)) {
                        event.reply("Funcionário foi atualizado com sucesso!\n\n **ID:** " + employee.getId())
                                .setEphemeral(true)
                                .queue();

                    } else {
                        event.reply("Falha em update\n\n **ID:** " + employee.getId())
                                .setEphemeral(true)
                                .queue();

                    }

                    // Debug

                    System.out.println("===== DB SEARCH FORM DATA =====");
                    System.out.println("ID: " + employee.getName());
                    System.out.println("Name: " + employee.getName());
                    System.out.println("Email: " + employee.getEmail());
                    System.out.println("Phone Number: " + employee.getPhoneNumber());
                    System.out.println("Birthdate: " + employee.getBirthdate());
                    System.out.println("Role: " + employee.getRole());
                    System.out.println("==================================");
                    // Debug

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (modal.equals("meetingFirst")) {
            String dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
            String timeRegex = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";
            String meetingName = event.getValue("tema").getAsString();
            String description = event.getValue("descrição").getAsString();
            String date = event.getValue("data").getAsString();
            String startTime = event.getValue("início").getAsString();
            String name = event.getUser().getName();
            try {
                System.out.println(UserDAO.fileToString());
                User user = UserDAO.searchByName(name);
                if (user == null) {
                    event.reply("```User is not registered exist!```")
                            .setEphemeral(true)
                            .queue();
                } else if (!Pattern.matches(dateRegex, date)) {
                    event.reply("```Data Inválida```")
                            .setEphemeral(true)
                            .queue();
                } else if (!Pattern.matches(timeRegex, startTime)) {
                    event.reply("```Start Time Inválido```")
                            .setEphemeral(true)
                            .queue();
                } else {
                    MeetingDAO meetingDAO = new MeetingDAO();
                    // Cria o meeting
                    Meeting meeting = new Meeting();
                    meeting.setName(meetingName);
                    meeting.setDescription(description);
                    meeting.setDate(date);
                    meeting.setStartTime(startTime);
                    meeting.setIdUser(user.getId());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (modal.equals("meetingSecond")) {

            String dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
            String timeRegex = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";

            String meetingName = event.getValue("tema").getAsString();
            String description = event.getValue("descrição").getAsString();
            String date = event.getValue("data").getAsString();
            String startTime = event.getValue("início").getAsString();
            String smeetingId = event.getValue("idmeeting").getAsString();

            if (!smeetingId.matches("\\d+")) {
                event.reply("```Meeting ID deve ser um número válido```")
                        .setEphemeral(true).queue();
            } else {
                try {

                    String name = event.getUser().getName();
                    int meetingId = Integer.parseInt(smeetingId);
                    Meeting meeting = MeetingDAO.searchMeeting(meetingId);
                    User user = UserDAO.searchByName(name);

                    if (user.getId() == meeting.getIdUser()) {
                        event.reply("```User does not own this meeting```")
                                .setEphemeral(true)
                                .queue();
                    } else if (meeting == null) {
                        event.reply("```Meeting does not exist!```")
                                .setEphemeral(true).queue();
                    } else if (!Pattern.matches(dateRegex, date)) {
                        event.reply("```Data Inválida```")
                                .setEphemeral(true).queue();
                    } else if (!Pattern.matches(timeRegex, startTime)) {
                        event.reply("```Start Time Inválido```")
                                .setEphemeral(true).queue();
                    } else {

                        Meeting updated = new Meeting();
                        updated.setId(meetingId);
                        updated.setName(meetingName);
                        updated.setDescription(description);
                        updated.setDate(date);
                        updated.setStartTime(startTime);

                        MeetingDAO.updateMeeting(updated);

                        event.reply(
                                "```Reunião atualizada com sucesso!```\n" +
                                        "**ID:** " + updated.getId() + "\n" +
                                        "**Tema:** " + updated.getName() + "\n" +
                                        "**Descrição:** " + updated.getDescription())
                                .setEphemeral(true)
                                .queue();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
