# discord-bot-jda-base  
  
# PASSO A PASSO  
1- Crie uma conta no discord (https://discord.com/)  
2- Entre no servidor de testes (https://discord.gg/2TymcAfaVK)  
3- Baixe o código na sua máquina  
4- Adicione a chave de API enviada (Va no Utils.java dentro em Bot/src/util/  e cole a chave na apiToken)
5- Instale o Apache Maven para rodar o programa  
  
# COMPILANDO  
IMPORTANTE: Abra o CMD no diretório "discord-bot-jda-base\Bot"  
* mvn clean install (quando der algum problema)  
* mvn compile exec:java (para executar o programa)  
* ctrl + c (encerrar a execução do bot)  

# COMANDOS
### 👤 Gerenciamento de Usuário
| **Comando** | **Descrição** |
|--------------|---------------|
| `/registro` | Registra o usuário no sistema |
| `/atualizar-registro` | Atualiza seu cadastro |
| `/mostrar-registro` | Mostra as suas informações |
| `/deletar-registro` | Deleta sua conta |

---

### 📅 Gerenciamento de Reuniões
| **Comando** | **Descrição** |
|--------------|---------------|
| `/criar-reunião` | Agenda uma reunião |
| `/atualizar-reunião` | Atualiza os dados de uma reunião |
| `/mostrar-reunião` | Mostra as informações de uma reunião |
| `/mostrar-todas-reuniões` | Mostra todas as reuniões marcadas pelo usuário |
| `/deletar-reunião` | Deleta uma reunião |

---

### 👥 Gerenciamento de Membros
| **Comando** | **Descrição** |
|--------------|---------------|
| `/criar-membro` | Cria um novo membro (atendee) no sistema |
| `/mostrar-membro` | Mostra as informações de um membro |
| `/adicionar-membro-reuniao` | Adiciona um membro a uma reunião |
| `/mostrar-membros-reuniao` | Lista todos os membros que participam de uma reunião |
| `/mostrar-reunioes-membro` | Lista todas as reuniões das quais um membro participa |
