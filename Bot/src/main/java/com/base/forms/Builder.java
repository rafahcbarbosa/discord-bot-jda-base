package com.base.forms;

import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.modals.Modal;

public class Builder {
    
    public static Modal registerFirst(){


                                        // id //      // visible title //
        Modal modal = Modal.create("registerFirst", "Formulário de registro")
                .addComponents(
                    
                    Label.of("Nome completo", Field.fullName),
                    Label.of("Email", Field.email),
                    Label.of("Telefone (apenas números, com país e DDD)", Field.phoneNumber),
                    Label.of("Data de nascimento (padrão dia/mês/ano)", Field.birthdate),
                    Label.of("Cargo", Field.roleMenu)

                ).build();

        return modal;
    }

    public static Modal registerSecond(){

        Modal modal = Modal.create("registerSecond", "Formulário de registro")
                
                .addComponents(
                    
                    Label.of("Nome completo", Field.fullName),
                    Label.of("Email do Employee (não se pode mudar)", Field.email),
                    Label.of("Telefone (apenas números, com país e DDD)", Field.phoneNumber),
                    Label.of("Data de nascimento (padrão dia/mês/ano)", Field.birthdate),
                    Label.of("Cargo", Field.roleMenu)

                ).build();

        return modal;
    }

    public static Modal meetingFirst(){

        Modal modal = Modal.create("meetingFirst", "Criar uma reunião")
            .addComponents(

                Label.of("Tema", Field.meetingName),
                Label.of("Descrição", Field.description),
                Label.of("Data (padrão dia/mês/ano)", Field.date),
                Label.of("Início (padrão hora:minuto)", Field.startTime)

            ).build();

        return modal;
    }

     public static Modal meetingSecond(){

        Modal modal = Modal.create("meetingSecond", "Update reunião")
            .addComponents(

                Label.of("Tema", Field.meetingName),
                Label.of("Descrição", Field.description),
                Label.of("Data (padrão dia/mês/ano)", Field.date),
                Label.of("Início (padrão hora:minuto)", Field.startTime),
                Label.of("meetingId", Field.meetingId)

            ).build();

        return modal;
    }
}   
