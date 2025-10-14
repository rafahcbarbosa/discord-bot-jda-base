package com.base.forms;

// import org.w3c.dom.Text;
// import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
// import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;

public class Field {
    
        // Registration form

        // Form 1
        public static final TextInput fullName = TextInput.create("nome", TextInputStyle.SHORT)
                .setPlaceholder("Ex: Seu Nome Completo da Silva")
                .setRequired(true)
                .setRequiredRange(1, 50)
                .build();

        public static final TextInput email = TextInput.create("email", TextInputStyle.SHORT)
                .setPlaceholder("Ex: pedrinhogameplays@gmail.com")
                .setRequired(true)
                .setRequiredRange(1, 50)
                .build();

        public static final TextInput phoneNumber = TextInput.create("telefone", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 5531285243875")
                .setRequired(true)
                .setRequiredRange(1, 13)
                .build();

        public static final TextInput birthdate = TextInput.create("nascimento", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 30/07/2004")
                .setRequired(true)
                .setRequiredRange(1, 10)
                .build();

        // public static final EntitySelectMenu roleMenu = EntitySelectMenu.create("seleção-cargo", SelectTarget.ROLE)
        //         .setPlaceholder("Escolha seu cargo")
        //         .setRequired(true)
        //         .build();

        public static final TextInput roleMenu = TextInput.create("seleção-cargo", TextInputStyle.SHORT)
                .setPlaceholder("Escolha seu cargo")
                .setRequired(true)
                .build();

        // Form 2

        public static final TextDisplay passwordRules = TextDisplay.of(
                                """
                                
                                - 8-30 caracteres
                                - Conter um **número**
                                - Conter um **símbolo**
                                - Conter uma letra **maiúscula**

                                """);

        public static final TextInput password = TextInput.create("senha", TextInputStyle.SHORT)
                .setPlaceholder("Ex: Minhasenhalegal123*")
                .setRequired(true)
                .setRequiredRange(8, 30)
                .build();

        public static final TextInput passwordRepeat = TextInput.create("senha-repetida", TextInputStyle.SHORT)
                .setPlaceholder("Repita sua senha aqui")
                .setRequired(true)
                .setRequiredRange(1, 30)
                .build();

        // Meeting form

        public static final TextInput meetingName = TextInput.create("tema", TextInputStyle.SHORT)
                .setPlaceholder("Assunto da reunião")
                .setRequired(true)
                .setRequiredRange(1, 100)
                .build();
        
        public static final TextInput description = TextInput.create("descrição", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Descreva o assunto a ser tratado em detalhes (não obrigatório)")
                .setRequired(false)
                .build();

        public static final TextInput date = TextInput.create("data", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 23/08/2025")
                .setRequired(true)
                .setRequiredRange(1, 10)
                .build();

        public static final TextInput startTime = TextInput.create("início", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 10:30")
                .setRequired(true)
                .setRequiredRange(1, 5)
                .build();
                
        public static final TextInput finishTime = TextInput.create("fim", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 12:00")
                .setRequired(true)
                .setRequiredRange(1, 5)
                .build();
                
}
               
