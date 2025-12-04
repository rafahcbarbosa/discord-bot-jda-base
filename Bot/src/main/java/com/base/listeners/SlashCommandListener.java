package com.base.listeners;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Comparator;
import com.base.Encryption.*;

import com.base.CRUD_User.User;
import com.base.CRUD_User.UserDAO;
import com.base.CRUD_EmployeeMeeting.EmployeeMeeting;
import com.base.CRUD_EmployeeMeeting.EmployeeMeetingDAO;
import com.base.CRUD_Employee.Employee;
import com.base.CRUD_Employee.EmployeeDAO;
import com.base.CRUD_Meeting.Meeting;
import com.base.CRUD_Meeting.MeetingDAO;
import com.base.Compression.Huffman;
import com.base.Compression.HuffmanEntry;
import com.base.Compression.LZW;
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

            String UserName = event.getUser().getName();
            List<Role> roleList = event.getMember().getRoles();
            String role;

            if (roleList != null) {

                role = roleList.get(0).getName();

            } else {

                role = "(SEM CARGO)";
            }

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

            } else if (command.equals(Utils.command[3])) {
                Modal modal = Builder.meetingSecond();
                event.replyModal(modal).queue();
            } else if (command.equals(Utils.command[4])) {

                Employee employee = new Employee();
                int id = event.getOption("id").getAsInt();
                employee = EmployeeDAO.searchEmployee(id);

                if (employee != null) {

                    EmployeeDAO.deleteEmployee(id);

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
                ArrayList<EmployeeMeeting> employeeMeetings = EmployeeMeetingDAO.searchByEmployee(id);

                if (meeting != null) {

                    meetingDAO.deleteMeeting(id);

                    for (EmployeeMeeting employeeMeeting : employeeMeetings) {
                        EmployeeMeetingDAO.delete(employeeMeeting.getId());
                    }

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
                Employee employee = EmployeeDAO.searchEmployee(id);

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
                            "- Início: " + meeting.getStartTime() + "\n";

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
                                " | ID Employee: " + m.getIdUser());
                    }
                    System.out.println("==============================");

                    String info = "";

                    for (Meeting m : meetingArrayList) {
                        info += "INFORMAÇÕES DA REUNIÃO " + m.getId() + "\n"
                                + "- Tema: " + m.getName() + "\n"
                                + "- Descrição: " + m.getDescription() + "\n"
                                + "- Data: " + m.getDate() + "\n"
                                + "- Início: " + m.getStartTime() + "\n";
                    }

                    event.reply(info)
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }

            } else if (command.equals(Utils.command[9])) {

                if ((UserDAO.exists(UserName))) {
                    event.reply("Membro já registrado")
                            .setEphemeral(true)
                            .queue();
                } else {
                    User user = new User(UserName, role);

                    UserDAO.insertUser(user);

                    event.reply("Membro registrado com sucesso!\n- **ID:** " + user.getId())
                            .setEphemeral(true)
                            .queue();
                }

            } else if (command.equals(Utils.command[10])) {

                int id = event.getOption("id").getAsInt();

                User user = UserDAO.searchUser(id);
                String info;

                if (user == null) {
                    info = "user does not exist";
                } else {
                    info = """
                            ```INFORMAÇÕES DO MEMBRO```
                            - **Nome:** %s
                            - **Cargo:** %s
                            """.formatted(
                            user.getName(),
                            user.getCargo());

                }

                event.reply(info)
                        .setEphemeral(true)
                        .queue();

            } else if (command.equals(Utils.command[11])) {

                int idEmployee = event.getOption("id_employee").getAsInt();
                Employee employee = EmployeeDAO.searchEmployee(idEmployee);

                int idMeeting = event.getOption("id_meeting").getAsInt();
                Meeting meeting = MeetingDAO.searchMeeting(idMeeting);

                User user = UserDAO.searchByName(UserName);

                if (meeting == null) {
                    event.reply("Meeting não existe!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();

                } else if (employee == null) {

                    event.reply("Employee não existe!")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                } else if ((user == null || user.getId() != meeting.getIdUser())) {
                    event.reply("Você não é dono da reunião")
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                } else {
                    EmployeeMeeting employeeMeeting = new EmployeeMeeting(idEmployee, idMeeting);
                    EmployeeMeetingDAO.insert(employeeMeeting);

                    String info = "```INFORMAÇÕES DA REUNIÃO```" + "\n" +
                            "- **Tema:** " + meeting.getName() + "\n" +
                            "- **Descrição:** " + meeting.getDescription() + "\n" +
                            "- **Data:** " + meeting.getDate() + "\n" +
                            "- **Início:** " + meeting.getStartTime() + "\n" +
                            "```INFORMAÇÕES DO FUNCIONÁRIO```" + "\n" +
                            "- **Nome:** " + employee.getName() + "\n" +
                            "- **Telefone:** " + employee.getPhoneNumber() + "\n" +
                            "- **E-mail:** " + employee.getEmail() + "\n" +
                            "- **Data de Nascimento:** " + employee.getBirthdate() + "\n" +
                            "- **Cargo:** " + employee.getRole() + "\n";

                    event.reply(info)
                            .setEphemeral(true)
                            .queue();

                    event.deferReply();
                }

            } else if (command.equals(Utils.command[12])) {

                int id = event.getOption("id").getAsInt();
                Meeting meeting = MeetingDAO.searchMeeting(id);
                User user = UserDAO.searchByName(UserName);
                boolean eDono = false;

                if (meeting != null) {
                    if (user.getId() == meeting.getIdUser()) {
                        eDono = true;
                    }

                    System.out.println("ENTROU");

                    ArrayList<EmployeeMeeting> employeeMeetingArrayList = EmployeeMeetingDAO.searchByMeeting(id);
                    String info = "";

                    if (employeeMeetingArrayList.size() == 0) {

                        event.reply("Essa reunião possui membros!")
                                .setEphemeral(true)
                                .queue();

                        event.deferReply();

                    } else {

                        if (!eDono) {
                            for (EmployeeMeeting m : employeeMeetingArrayList) {

                                Employee employee = EmployeeDAO.searchEmployee(m.getIdEmployee());

                                info += "```INFORMAÇÕES DO MEMBRO```" + "\n" +
                                        "- **ID:** " + employee.getId() + "\n" +
                                        "- **Nome:** " + employee.getName() + "\n" +
                                        "- **Telefone:** " + employee.getPhoneNumber() + "\n" +
                                        "- **Email:** " + employee.getEmail() + "\n" +
                                        "- **Data de Nascimento:** " + employee.getBirthdate() + "\n" +
                                        "- **Cargo:** " + employee.getRole() + "\n\n";

                            }

                        } else {

                            for (EmployeeMeeting m : employeeMeetingArrayList) {

                                Employee employee = EmployeeDAO.searchEmployee(m.getIdEmployee());

                                info += "```INFORMAÇÕES DO MEMBRO```" + "\n" +
                                        "- **ID:** " + employee.getId() + "\n" +
                                        "- **Nome:** " + employee.getName() + "\n" +
                                        "- **Telefone:** " + Encriptor.Descriptografar(employee.getPhoneNumber()) + "\n"
                                        +
                                        "- **Email:** " + Encriptor.Descriptografar(employee.getEmail()) + "\n" +
                                        "- **Data de Nascimento:** " + employee.getBirthdate() + "\n" +
                                        "- **Cargo:** " + employee.getRole() + "\n\n";

                            }
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
                Employee user = EmployeeDAO.searchEmployee(id);
                User users = UserDAO.searchByName(UserName);

                if (user != null) {

                    ArrayList<EmployeeMeeting> employeeMeetingArrayList = EmployeeMeetingDAO.searchByMeeting(id);
                    String info = "";

                    if (employeeMeetingArrayList.size() == 0) {

                        event.reply("Essa reunião possui membros!").queue();
                        event.deferReply();

                    } else {

                        for (EmployeeMeeting m : employeeMeetingArrayList) {

                            Meeting meeting = MeetingDAO.searchMeeting(m.getIdMeeting());
                            
                            info += "```INFORMAÇÕES DA REUNIÃO```" + "\n" +
                                    "- **Tema:** " + meeting.getName() + "\n" +
                                    "- **Descrição:** " + meeting.getDescription() + "\n" +
                                    "- **Data:** " + meeting.getDate() + "\n" +
                                    "- **Início:** " + meeting.getStartTime() + "\n";

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

                if (!EmployeeDAO.getAll().isEmpty()) {
                    encod.add(EmployeeDAO.toHuffmanEntry());
                }

                if (!MeetingDAO.getAll().isEmpty()) {
                    encod.add(MeetingDAO.toHuffmanEntry());
                }
                if (!EmployeeMeetingDAO.getAll().isEmpty()) {
                    encod.add(EmployeeMeetingDAO.toHuffmanEntry());
                }
                if (!UserDAO.getAll().isEmpty()) {
                    encod.add(UserDAO.toHuffmanEntry());
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

                File dirArquivo = new File("./dadosCodificados/huffmanCompression.teste");

                if (!dirArquivo.exists()) {
                    event.reply("Nenhum dado previamente comprimido")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

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

                ArrayList<HuffmanEntry> codificados = Huffman.loadEntries(dirArquivo);
                ArrayList<String> saida = new ArrayList<>();

                for (HuffmanEntry codificado : codificados) {
                    String bits = Huffman.bytesToBitString(codificado.encodedBytes);
                    String texto = new String(Huffman.decodifica(bits, codificado.codes));
                    System.out.println(texto);
                    saida.add(texto);
                }
                EmployeeDAO employeeDAO = new EmployeeDAO();
                MeetingDAO meetingDAO = new MeetingDAO();
                UserDAO userDAO = new UserDAO();
                EmployeeMeetingDAO employeeMeetingDAO = new EmployeeMeetingDAO();
                employeeDAO.recreateDir();
                meetingDAO.recreateDir();
                userDAO.recreateDir();
                employeeMeetingDAO.recreateDir();

                for (String texto : saida) {
                    String[] parts = texto.split("\\|{5}");

                    switch (parts[0]) {
                        case "Employee":
                            for (int i = 1; i < parts.length; i += 5) {
                                ;
                                Employee employee = new Employee(
                                        parts[i],
                                        parts[i + 1],
                                        parts[i + 2],
                                        parts[i + 3],
                                        parts[i + 4]);
                                employeeDAO.insertEmployee(employee);
                            }
                            break;

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

                        case "User":
                            for (int i = 1; i < parts.length; i += 2) {

                                User user = new User(
                                        parts[i],
                                        parts[i + 1]);
                                userDAO.insertUser(user);

                            }
                            break;

                        case "EmployeeMeeting":
                            for (int i = 1; i < parts.length; i += 2) {

                                EmployeeMeeting employeeMeeting = new EmployeeMeeting(
                                        Integer.parseInt(parts[i]),
                                        Integer.parseInt(parts[i + 1]));
                                employeeMeetingDAO.insert(employeeMeeting);
                            }
                            break;
                    }
                }

                event.reply("Dados Descomprimidos com sucesso!")
                        .setEphemeral(true)
                        .queue();

                event.deferReply();
            } else if (command.equals(Utils.command[16])) {
                ArrayList<byte[]> encod = new ArrayList();

                if (!EmployeeDAO.getAll().isEmpty()) {
                    encod.add(EmployeeDAO.toLZWEntry());
                }

                if (!MeetingDAO.getAll().isEmpty()) {
                    encod.add(MeetingDAO.toLZWEntry());
                }
                if (!EmployeeMeetingDAO.getAll().isEmpty()) {
                    encod.add(EmployeeMeetingDAO.toLZWEntry());
                }
                if (!UserDAO.getAll().isEmpty()) {
                    encod.add(UserDAO.toLZWEntry());
                }

                File dirBase = new File("./dadosCodificados");
                if (!dirBase.exists())
                    dirBase.mkdir();

                File dirArquivo = new File("./dadosCodificados/lzwCompression.lzw");
                if (!dirArquivo.exists())
                    dirArquivo.createNewFile();
                ;

                LZW.saveLZWEntries(encod, dirArquivo);
                event.reply("Dados comprimidos com sucesso!")
                        .setEphemeral(true)
                        .queue();

                event.deferReply();

                ArrayList<byte[]> codificados = LZW.loadLZWEntries(dirArquivo);
                ArrayList<String> saida = new ArrayList<>();

                for (byte[] codificado : codificados) {
                    String texto = new String(LZW.decodifica(codificado));
                    System.out.println(texto);
                    saida.add(texto);
                }

            } else if (command.equals(Utils.command[17])) {
                Path baseDir = Paths.get("./dados");

                File dirArquivo = new File("./dadosCodificados/lzwCompression.lzw");

                if (!dirArquivo.exists()) {
                    event.reply("Nenhum dado previamente comprimido")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

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

                ArrayList<byte[]> codificados = LZW.loadLZWEntries(dirArquivo);
                ArrayList<String> saida = new ArrayList<>();

                for (byte[] codificado : codificados) {
                    String texto = new String(LZW.decodifica(codificado));
                    System.out.println(texto);
                    saida.add(texto);
                }
                EmployeeDAO employeeDAO = new EmployeeDAO();
                MeetingDAO meetingDAO = new MeetingDAO();
                UserDAO userDAO = new UserDAO();
                EmployeeMeetingDAO employeeMeetingDAO = new EmployeeMeetingDAO();
                employeeDAO.recreateDir();
                meetingDAO.recreateDir();
                userDAO.recreateDir();
                employeeMeetingDAO.recreateDir();

                for (String texto : saida) {
                    String[] parts = texto.split("\\|{5}");

                    switch (parts[0]) {
                        case "Employee":
                            for (int i = 1; i < parts.length; i += 5) {
                                ;
                                Employee employee = new Employee(
                                        parts[i],
                                        parts[i + 1],
                                        parts[i + 2],
                                        parts[i + 3],
                                        parts[i + 4]);
                                employeeDAO.insertEmployee(employee);
                            }
                            break;

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

                        case "User":
                            for (int i = 1; i < parts.length; i += 2) {

                                User user = new User(
                                        parts[i],
                                        parts[i + 1]);
                                userDAO.insertUser(user);

                            }
                            break;

                        case "EmployeeMeeting":
                            for (int i = 1; i < parts.length; i += 2) {

                                EmployeeMeeting employeeMeeting = new EmployeeMeeting(
                                        Integer.parseInt(parts[i]),
                                        Integer.parseInt(parts[i + 1]));
                                employeeMeetingDAO.insert(employeeMeeting);
                            }
                            break;
                    }
                }

                event.reply("Dados Descomprimidos com sucesso!")
                        .setEphemeral(true)
                        .queue();

                event.deferReply();
            }else if (command.equals(Utils.command[18])) {
                String email = event.getOption("email").getAsString().trim();
                Employee employee = EmployeeDAO.searchByEmail(Encriptor.Criptografar(email));
                String info;
                if(employee != null){
                    info = "```INFORMAÇÕES DE Usuário```" + "\n" +
                            "- **Nome:** " + employee.getName() + "\n" +
                            "- **Telefone:** " + employee.getPhoneNumber() + "\n" +
                            "- **E-mail:** " + employee.getEmail() + "\n" +
                            "- **Data de Nascimento:** " + employee.getBirthdate() + "\n" +
                            "- **Cargo:** " + employee.getRole() + "\n";
                }else {
                    info = "Employee não encontrado";
                }
                event.reply(info)
                        .setEphemeral(true)
                        .queue();
            }else if (command.equals(Utils.command[19])) {
                String nome = event.getOption("nome").getAsString().trim();
                User user = UserDAO.searchByName(nome);
                String info;
                if (user != null) {
                    info = "```INFORMAÇÕES DE Usuário```" + "\n" +
                            "- **Nome:** " + user.getName() + "\n" +
                            "- **Cargo:** " + user.getCargo() + "\n" ;
                            
                } else {
                    info = "User não encontrado";
                }
                event.reply(info)
                        .setEphemeral(true)
                        .queue();
            }
            event.deferReply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
