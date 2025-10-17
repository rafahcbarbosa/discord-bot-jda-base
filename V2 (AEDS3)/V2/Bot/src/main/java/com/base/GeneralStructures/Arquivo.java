package com.base.GeneralStructures;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.base.interfaces.Registro;
import com.base.interfaces.RegistroArvoreBMais;
import com.base.interfaces.RegistroHashExtensivel;

public class Arquivo<
        T extends Registro,
        I extends RegistroHashExtensivel<I>,
        F extends RegistroArvoreBMais<F>
    > {

    public int ultimoID;    
    private static final int TAM_CABECALHO = 12; // 4 (ID) + 8 (lista de excluídos)
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private HashExtensivel<I> indice;
    private Constructor<I> construtorIndice;
    private ArvoreBMais<F> indiceFK; // índice B+ genérico para FK
    
    
    // -------------------------------------------
    // CONSTRUTOR GENÉRICO
    // -------------------------------------------
    public Arquivo(String nomeArquivo,
                   Constructor<T> construtor,
                   Constructor<I> construtorIndice,
                   ArvoreBMais<F> indiceFK) throws Exception {

        // Criar diretório base
        File dirBase = new File("./dados");
        if (!dirBase.exists()) dirBase.mkdir();

        // Diretório específico para o arquivo
        File dirArquivo = new File("./dados/" + nomeArquivo);
        if (!dirArquivo.exists()) dirArquivo.mkdir();

        // Inicializações básicas
        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        this.construtorIndice = construtorIndice;
        this.indiceFK = indiceFK;

        // Cria o índice hash extensível genérico
        this.indice = new HashExtensivel<>(
            construtorIndice,
            3,
            "./dados/" + nomeArquivo + "/diretorio.db",
            "./dados/" + nomeArquivo + "/cestos.db"
        );

        // Cabeçalho inicial
            if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);
            arquivo.writeLong(-1);
            ultimoID = 0;
        } else {
            arquivo.seek(0);
            ultimoID = arquivo.readInt();
        }
    }


// -------------------------------------------
// CREATE
// -------------------------------------------
public boolean create(T obj,int fkValue) throws Exception {

    // Se ainda não inicializou ultimoID, lê do arquivo
    if (arquivo.length() >= 4) {
        arquivo.seek(0);
        ultimoID = arquivo.readInt();
    } else {
        // arquivo vazio, inicializa cabeçalho
        ultimoID = 0;
        arquivo.seek(0);
        arquivo.writeInt(ultimoID);
        arquivo.writeLong(-1); // lista de registros excluídos
    }

    // Incrementa o ID e atualiza no objeto
    ultimoID++;
    obj.setId(ultimoID);

    // Atualiza o ID no arquivo
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);

    // Serializa os dados
    byte[] dados = obj.toByteArray();

    // Verifica se há espaço disponível na lista de excluídos
    long endereco = getDeleted(dados.length);
    if (endereco == -1) {
        arquivo.seek(arquivo.length());
        endereco = arquivo.getFilePointer();
        arquivo.writeByte(' ');      // Lápide
        arquivo.writeShort(dados.length);
        arquivo.write(dados);
    } else {
        arquivo.seek(endereco);
        arquivo.writeByte(' ');      // Lápide
        arquivo.skipBytes(2);        // pula o tamanho
        arquivo.write(dados);
    }

    // Atualiza índice hash principal
    I idx = construtorIndice.newInstance();
    idx.setId(obj.getId());
    idx.setPos(endereco);
    indice.create(idx);

    // Atualiza índice B+ de FK, se existir
    if (indiceFK != null) {
        F fkObj = indiceFK.getConstrutor().newInstance();
        fkObj.setId(fkValue); // usa o valor passado
        fkObj.setPos(endereco);
        indiceFK.create(fkObj);
    }

    return true;
}



    // -------------------------------------------
    // READ por ID
    // -------------------------------------------
    public T read(int id) throws Exception {
        I idx = indice.read(id);
        if (idx == null) return null;

        arquivo.seek(idx.getPos());
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();
        byte[] dados = new byte[tamanho];
        arquivo.read(dados);

        if (lapide == ' ') {
            T obj = construtor.newInstance();
            obj.fromByteArray(dados);
            return obj;
        }
        return null;
    }
    public ArvoreBMais<F> getIndiceFK() {
        return indiceFK;
    }

    public void setIndiceFK(ArvoreBMais<F> indiceFK) {
        this.indiceFK = indiceFK;
}


    // -------------------------------------------
    // READ direto por posição
    // -------------------------------------------
    public T readAt(long pos) throws Exception {
        
        if (pos < TAM_CABECALHO || pos >= arquivo.length()) {
            return null; // evita EOF
        }

        arquivo.seek(pos);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();

        if (pos + 3 + tamanho > arquivo.length()) {
            return null; // evita ler além do fim
        }

        byte[] dados = new byte[tamanho];
        arquivo.read(dados);

        if (lapide == ' ') {
            T obj = construtor.newInstance();
            obj.fromByteArray(dados);
            return obj;
        }
        return null;
    }

    // -------------------------------------------
    // DELETE
    // -------------------------------------------
    public boolean delete(int id) throws Exception {
        I idx = indice.read(id);
        if (idx == null) return false;

        arquivo.seek(idx.getPos());
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();
        if (lapide == ' ') {
            arquivo.seek(idx.getPos());
            arquivo.writeByte('*');
            addDeleted(tamanho, idx.getPos());
            indice.delete(id);
            return true;
        }
        return false;
    }

    // -------------------------------------------
    // UPDATE
    // -------------------------------------------
    public boolean update(T novoObj) throws Exception {
        I idx = indice.read(novoObj.getId());
        if (idx == null) return false;

        long endereco = idx.getPos();
        arquivo.seek(endereco);

        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();
        byte[] dadosAntigos = new byte[tamanho];
        arquivo.read(dadosAntigos);

        if (lapide != ' ') return false;

        byte[] novosDados = novoObj.toByteArray();
        short novoTam = (short) novosDados.length;

        if (novoTam <= tamanho) {
            arquivo.seek(endereco + 3);
            arquivo.write(novosDados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte('*');
            addDeleted(tamanho, endereco);

            long novoEndereco = getDeleted(novosDados.length);
            if (novoEndereco == -1) {
                arquivo.seek(arquivo.length());
                novoEndereco = arquivo.getFilePointer();
                arquivo.writeByte(' ');
                arquivo.writeShort(novoTam);
                arquivo.write(novosDados);
            } else {
                arquivo.seek(novoEndereco);
                arquivo.writeByte(' ');
                arquivo.skipBytes(2);
                arquivo.write(novosDados);
            }

            I novoIndice = construtorIndice.newInstance();
            novoIndice.setId(novoObj.getId());
            novoIndice.setPos(novoEndereco);
            indice.update(novoIndice);
        }

        return true;
    }

    // -------------------------------------------
    // SEARCH BY FOREIGN KEY (B+ Tree)
    // -------------------------------------------
    public ArrayList<T> searchByFK(int fkValue) throws Exception {
        if (indiceFK == null)
            throw new IllegalStateException("B+ index (indiceFK) not initialized");

        // cria uma chave temporária
        F chave;
        try {
            chave = indiceFK.getConstrutor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of FK type", e);
        }

        chave.setId(fkValue);
        ArrayList<F> indices = indiceFK.read(chave);

        ArrayList<T> resultados = new ArrayList<>();
        for (F ind : indices) {
            long pos = ind.getPos();
            T obj = this.readAt(pos);
            if (obj != null) resultados.add(obj);
        }

        return resultados;
    }

    // -------------------------------------------
    // Gerenciamento de espaços excluídos
    // -------------------------------------------
    private void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;

        if (endereco == -1) {
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco + 1);
                int tamanho = arquivo.readShort();
                proximo = arquivo.readLong();

                if (tamanho > tamanhoEspaco) {
                    arquivo.seek(posicao == 4 ? posicao : posicao + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    break;
                }

                if (proximo == -1) {
                    arquivo.seek(endereco + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(-1);
                    break;
                }

                posicao = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;
        int tamanho;

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            if (tamanho >= tamanhoNecessario) {
                arquivo.seek(posicao == 4 ? posicao : posicao + 3);
                arquivo.writeLong(proximo);
                return endereco;
            }
            posicao = endereco;
            endereco = proximo;
        }
        return -1;
    }

    // -------------------------------------------
    // CLOSE
    // -------------------------------------------
    public void close() throws Exception {
        arquivo.close();
    }
}

