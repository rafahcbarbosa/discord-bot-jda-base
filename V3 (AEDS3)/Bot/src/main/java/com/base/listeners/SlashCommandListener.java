package com.base.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Comparator;

import com.base.CRUD_Atendee.Atendee;
import com.base.CRUD_Atendee.AtendeeDAO;
import com.base.CRUD_AtendeeMeeting.AtendeeMeeting;
import com.base.CRUD_AtendeeMeeting.AtendeeMeetingDAO;
import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Meeting.Meeting;
import com.base.CRUD_Meeting.MeetingDAO;
import com.base.Compression.Huffman;
import com.base.Compression.HuffmanEntry;
import com.base.forms.Builder;
import com.base.util.Utils;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        System.out.println("- Command event");

        String command = event.getName();

        try {
            // registro
            if (command.equals(Utils.command[0])) {

                Modal modal = Builder.registerFirst();
                event.replyModal(modal).queue();

                // Modal modal = Builder.registerSecond();
                // event.replyModal(modal).queue();

                // criar-reunião
            } else if (command.equals(Utils.command[1])) {

                Modal modal = Builder.meetingFirst();
                event.replyModal(modal).queue();

                // deletar-registro
            } else if (command.equals(Utils.command[4])) {

                Employee employee = new Employee();
                int id = event.getOption("id").getAsInt();
                employee = employee.employeeDAO.searchEmployee(id);

                if (employee != null) {

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
            } else if (command.equals(Utils.command[5])) {
                MeetingDAO meetingDAO = new MeetingDAO();
                Meeting meeting = new Meeting();
                int id = event.getOption("id").getAsInt();
                meeting = meetingDAO.searchMeeting(id);

                if (meeting != null) {

                    meetingDAO.deleteMeeting(id);

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
            } else if (command.equals(Utils.command[6])) {

                int id = event.getOption("id").getAsInt();
                Employee employee = new Employee().employeeDAO.searchEmployee(id);

                if (employee != null) {

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
                            employee.getRole());

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
            } else if (command.equals(Utils.command[7])) {

                int id = event.getOption("id").getAsInt();
                Meeting meeting = MeetingDAO.searchMeeting(id);

                if (meeting != null) {

                    String info = "INFORMAÇÕES DA REUNIÃO\n" +
                            "- Tema: " + meeting.getName() + "\n" +
                            "- Descrição: " + meeting.getDescription() + "\n" +
                            "- Data: " + meeting.getDate() + "\n" +
                            "- Início: " + meeting.getStartTime() + "\n" +
                            "- ID do Funcionário: " + meeting.getIdEmployee();

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
            } else if (command.equals(Utils.command[8])) {
                MeetingDAO meetingDAO = new MeetingDAO();
                int id = event.getOption("id").getAsInt();
                Meeting meeting = new Meeting();

                ArrayList<Meeting> meetingArrayList = meetingDAO.searchByFK(id);

                if (meetingArrayList.isEmpty()) {

                    event.reply("O usuário **(" + id + ")** não possui reuniões!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();

                } else {

                    System.out.println("```TODAS REUNIÕES DO USUÁRIO (" + id + ")```");

                    for (Meeting m : meetingArrayList) {
                        System.out.println("ID: " + m.getId() +
                                " | Tema: " + m.getName() +
                                " | Descrição: " + m.getDescription() +
                                " | Data: " + m.getDate() +
                                " | Início: " + m.getStartTime() +
                                " | ID Employee: " + m.getIdEmployee());
                    }
                    System.out.println("==============================");

                    String info = "";

                    for (Meeting m : meetingArrayList) {
                        info += "INFORMAÇÕES DA REUNIÃO " + m.getId() + "\n"
                                + "- Tema: " + m.getName() + "\n"
                                + "- Descrição: " + m.getDescription() + "\n"
                                + "- Data: " + m.getDate() + "\n"
                                + "- Início: " + m.getStartTime() + "\n"
                                + "- ID do Funcionário: " + m.getIdEmployee() + "\n\n";
                    }

                    event.reply(info)
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }

            } else if (command.equals(Utils.command[9])) {

                String name = event.getUser().getName();
                List<Role> roleList = event.getMember().getRoles();
                String role;

                if (roleList != null) {

                    role = roleList.get(0).getName();

                } else {

                    role = "(SEM CARGO)";
                }

                Atendee atendee = new Atendee(name, role);

                AtendeeDAO.insertAtendee(atendee);

                event.reply("Membro registrado com sucesso!\n- **ID:** " + atendee.getId())
                        .setEphemeral(true)
                        .queue();

            } else if (command.equals(Utils.command[10])) {

                int id = event.getOption("id").getAsInt();

                Atendee atendee = AtendeeDAO.searchAtendee(id);

                String info = """
                        ```INFORMAÇÕES DO MEMBRO```
                        - **Nome:** %s
                        - **Cargo:** %s
                        """.formatted(
                        atendee.getName(),
                        atendee.getCargo());

                event.reply(info)
                        .setEphemeral(true)
                        .queue();

            } else if (command.equals(Utils.command[11])) {

                int idAtendee = event.getOption("id_atendee").getAsInt();
                Atendee atendee = AtendeeDAO.searchAtendee(idAtendee);

                int idMeeting = event.getOption("id_meeting").getAsInt();
                Meeting meeting = MeetingDAO.searchMeeting(idMeeting);

                AtendeeMeeting atendeeMeeting = new AtendeeMeeting(idAtendee, idMeeting);

                if (meeting != null || atendee != null) {

                    AtendeeMeetingDAO.insert(atendeeMeeting);

                    String info = """
                            ```INFORMAÇÕES DA REUNIÃO```
                            - **Tema:** %s
                            - **Descrição:** %s
                            - **Data:** %s
                            - **Início:** %s
                            - **Término:** %s
                            - **ID do Funcionário:** %s

                            ```INFORMAÇÕES DO MEMBRO```
                            - **Nome:** %s
                            - **Cargo:** %s
                            """.formatted(
                            meeting.getName(),
                            meeting.getDescription(),
                            meeting.getDate(),
                            meeting.getStartTime(),
                            meeting.getIdEmployee(),
                            atendee.getName(),
                            atendee.getCargo());

                    event.reply(info)
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();

                } else {

                    event.reply("Um dos elementos não existe!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }

            } else if (command.equals(Utils.command[12])) {

                int id = event.getOption("id").getAsInt();
                Meeting meeting = MeetingDAO.searchMeeting(id);

                if (meeting != null) {

                    System.out.println("ENTROU");

                    ArrayList<AtendeeMeeting> atendeeMeetingArrayList = AtendeeMeetingDAO.searchByMeeting(id);
                    String info = "";

                    if (atendeeMeetingArrayList.size() == 0) {

                        event.reply("Essa reunião possui membros!")
                                .setEphemeral(true)
                                .queue();

                        event.deferReply();

                    } else {

                        for (AtendeeMeeting m : atendeeMeetingArrayList) {

                            Atendee atendee = AtendeeDAO.searchAtendee(m.getIdAtendee());

                            info += """
                                    ```INFORMAÇÕES DO MEMBRO```
                                    - **Nome:** %s
                                    - **Cargo:** %s\n
                                    """.formatted(
                                    atendee.getName(),
                                    atendee.getCargo());
                        }

                        event.reply(info)
                                .setEphemeral(true)
                                .queue();

                        event.deferReply();
                    }

                } else {

                    event.reply("A reunião **" + id + "** não existe!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }

            } else if (command.equals(Utils.command[13])) {

                int id = event.getOption("id").getAsInt();
                Atendee atendee = AtendeeDAO.searchAtendee(id);

                if (atendee != null) {

                    ArrayList<AtendeeMeeting> atendeeMeetingArrayList = AtendeeMeetingDAO.searchByMeeting(id);
                    String info = "";

                    if (atendeeMeetingArrayList.size() == 0) {

                        event.reply("Essa reunião possui membros!").queue();
                        event.deferReply();

                    } else {

                        for (AtendeeMeeting m : atendeeMeetingArrayList) {

                            Meeting meeting = MeetingDAO.searchMeeting(m.getIdMeeting());

                            info += """
                                    ```INFORMAÇÕES DA REUNIÃO```
                                    - **Tema:** %s
                                    - **Descrição:** %s
                                    - **Data:** %s
                                    - **Início:** %s
                                    - **Término:** %s
                                    - **ID do Funcionário:** %s\n
                                    """.formatted(
                                    meeting.getName(),
                                    meeting.getDescription(),
                                    meeting.getDate(),
                                    meeting.getStartTime(),
                                    meeting.getIdEmployee());
                        }

                        event.reply(info)
                                .setEphemeral(true)
                                .queue();

                        event.deferReply();
                    }

                } else {

                    event.reply("O membro **" + id + "** não existe!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }
            } else if (command.equals(Utils.command[14])) {
                ArrayList<HuffmanEntry> encod = new ArrayList();

                if (!MeetingDAO.getAll().isEmpty()) {
                    encod.add(MeetingDAO.toHuffmanEntry());
                }
                if (!AtendeeMeetingDAO.getAll().isEmpty()) {
                    encod.add(AtendeeMeetingDAO.toHuffmanEntry());
                }
                if (!AtendeeDAO.getAll().isEmpty()) {
                    encod.add(AtendeeDAO.toHuffmanEntry());
                }

                File dirBase = new File("./dadosCodificados");
                if (!dirBase.exists())
                    dirBase.mkdir();

                File dirArquivo = new File("./dadosCodificados/huffmanCompression.teste");
                if (!dirArquivo.exists())
                    dirArquivo.createNewFile();
                ;

                Huffman.saveEntries(encod, dirArquivo);

                event.reply("Dados comprimidos com sucesso!")
                        .setEphemeral(true)
                        .queue();

                event.deferReply();
            } else if (command.equals(Utils.command[15])) {
                Path baseDir = Paths.get("./dados"); 
                if (Files.exists(baseDir)) {
                    Files.walk(baseDir)
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }

                // Recreate the directory after deleting it
                try {
                    Files.createDirectories(baseDir);
                } catch (IOException e) {
                    e.printStackTrace();
                    event.reply("Erro ao recriar diretório de dados")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                File dirArquivo = new File("./dadosCodificados/huffmanCompression.teste");

                if (!dirArquivo.exists()) {
                    event.reply("Nenhum dado previamente comprimido")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                ArrayList<HuffmanEntry> codificados = Huffman.loadEntries(dirArquivo);
                ArrayList<String> saida = new ArrayList<>();

                for (HuffmanEntry codificado : codificados) {
                    String bits = Huffman.bytesToBitString(codificado.encodedBytes);
                    String texto = new String(Huffman.decodifica(bits, codificado.codes));
                    System.out.println(texto);
                    saida.add(texto);
                }
                MeetingDAO meetingDAO = new MeetingDAO();
                AtendeeDAO atendeeDAO = new AtendeeDAO();
                AtendeeMeetingDAO atendeeMeetingDAO = new AtendeeMeetingDAO();
                for (String texto : saida) {
                    String[] parts = texto.split("\\|{5}");

                    switch (parts[0]) {

                        case "Meeting":
                            for (int i = 1; i < parts.length; i += 5) {
                                Meeting meeting = new Meeting(
                                        parts[i],
                                        parts[i + 1],
                                        parts[i + 2],
                                        parts[i + 3],
                                        Integer.parseInt(parts[i + 4]));
                                meetingDAO.insertMeeting(meeting);
                            }
                            break;

                        case "Atendee":
                            for (int i = 1; i < parts.length; i += 2) {
                                Atendee atendee = new Atendee(
                                        parts[i],
                                        parts[i + 1]);
                                atendeeDAO.insertAtendee(atendee);
                            }
                            break;

                        case "AtendeeMeeting":
                            for (int i = 1; i < parts.length; i += 2) {
                                AtendeeMeeting atendeeMeeting = new AtendeeMeeting(
                                        Integer.parseInt(parts[i]),
                                        Integer.parseInt(parts[i + 1]));
                                atendeeMeetingDAO.insert(atendeeMeeting);
                            }
                            break;
                    }
                }

                event.reply("Dados Descomprimidos com sucesso!")
                        .setEphemeral(true)
                        .queue();

                event.deferReply();
            }

            event.deferReply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
