package com.base.util;

public class Utils {

    public static final String apiToken =
    public static final String adminID = "593532741602246656";
    public static final String serverID = "1391108240665874553";
    public static final String debugChannelID = "1437239436160008212";

    public static final String[] command = {

            "criar-employee", // [0]
            "criar-reunião", // [1]
            "atualizar-employee", // [2]
            "atualizar-reunião", // [3]
            "deletar-employee", // [4]
            "deletar-reunião", // [5]
            "mostrar-employee", // [6]
            "mostrar-reunião", // [7]
            "mostrar-todas-reuniões", // [8]
            "criar-user", // [9]
            "mostrar-user", // [10]
            "adicionar-employee-reuniao", // [11]
            "mostrar-employees-reuniao", // [12]
            "mostrar-reunioes-employee", // [13]
            "huffman",
            "decode-huffman",
            "lzw",
            "decode-lzw",
            "buscar-employee-nome",
            "buscar-user-nome",
            "deletar-proprio-user"
    };

    public static final String[] description = {
            "Registra o employee no sistema", // [0]
            "Agenda uma reunião", // [1]
            "Atualiza seu cadastro", // [2]
            "Atualiza os dados de uma reunião", // [3]
            "Deleta sua conta", // [4]
            "Deleta uma reunião", // [5]
            "Mostra as suas informações", // [6]
            "Mostra as informações de uma reunião", // [7]
            "Mostra todas as reuniões marcadas pelo usuário", // [8]
            "Cria um novo usuário no sistema", // [9]
            "Mostra as informações de um membro", // [10]
            "Adiciona um membro a uma reunião", // [11]
            "Lista todos os membros que participam de uma reunião", // [12]
            "Lista todas as reuniões das quais um membro participa", // [13]
            "Huffman encoding",
            "Huffman decoding",
            "LZW encoding",
            "LZW decoding",
            "busca Employee por nome",
            "busca User por nome",
            "deletar-proprio-user"   
    };
}
