package com.base.GeneralStructures;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.base.interfaces.*;


public class Arquivo<T extends Registro, I extends RegistroHashExtensivel<I>> {

    public int ultimoID;
    private static final int TAM_CABECALHO = 12; // 4 (ID) + 8 (lista de excluídos)
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private HashExtensivel<I> indice;
    private Constructor<I> construtorIndice;

    // -------------------------------------------
    // CONSTRUTOR GENÉRICO
    // -------------------------------------------
    public Arquivo(String nomeArquivo,
            Constructor<T> construtor,
            Constructor<I> construtorIndice) throws Exception {

        File dirBase = new File("./dados");
        if (!dirBase.exists())
            dirBase.mkdir();

        File dirArquivo = new File("./dados/" + nomeArquivo);
        if (!dirArquivo.exists())
            dirArquivo.mkdir();

        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;

        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        this.construtorIndice = construtorIndice;

        this.indice = new HashExtensivel<>(
                construtorIndice,
                3,
                "./dados/" + nomeArquivo + "/diretorio.db",
                "./dados/" + nomeArquivo + "/cestos.db");

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);
            arquivo.writeLong(-1);
            ultimoID = 0;
        } else {
            arquivo.seek(0);
            ultimoID = arquivo.readInt();
        }
    }

    public boolean create(T obj, int fkValue) throws Exception {

        arquivo.seek(0);
        ultimoID = arquivo.readInt();

        ultimoID++;
        obj.setId(ultimoID);

        arquivo.seek(0);
        arquivo.writeInt(ultimoID);

        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');
            arquivo.writeShort(dados.length);
            arquivo.write(dados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');
            arquivo.skipBytes(2);
            arquivo.write(dados);
        }

        I idx = construtorIndice.newInstance();
        idx.setId(obj.getId());
        idx.setPos(endereco);
        indice.create(idx);

        return true;
    }

    public T read(int id) throws Exception {
        I idx = indice.read(id);
        if (idx == null)
            return null;

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

    public boolean delete(int id) throws Exception {

        I idx = indice.read(id);
        if (idx == null)
            return false;

        long pos = idx.getPos();
        if (pos < TAM_CABECALHO || pos >= arquivo.length())
            return false;

        arquivo.seek(pos);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();

        if (lapide != ' ')
            return false;

        byte[] dados = new byte[tamanho];
        arquivo.readFully(dados);

        T obj = construtor.newInstance();
        obj.fromByteArray(dados);
        int fkValue = obj.getForeignKey();

        arquivo.seek(pos);
        arquivo.writeByte('*');

        addDeleted(tamanho, pos);

        indice.delete(id);

        return true;
    }

    public boolean update(T novoObj) throws Exception {
        I idx = indice.read(novoObj.getId());
        if (idx == null)
            return false;

        long endereco = idx.getPos();
        arquivo.seek(endereco);

        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();
        byte[] dadosAntigos = new byte[tamanho];
        arquivo.read(dadosAntigos);

        if (lapide != ' ')
            return false;

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

    public ArrayList<T> searchByFK(int fkValue) throws Exception {
        ArrayList<T> resultados = new ArrayList<>();

        if (arquivo.length() < 12)
            return resultados;

        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        long posListaExcluidos = arquivo.readLong();

        long pos = arquivo.getFilePointer();

        while (pos < arquivo.length()) {
            arquivo.seek(pos);

            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();

            if (lapide == ' ') {

                byte[] dados = new byte[tamanho];
                arquivo.readFully(dados);

                T obj = construtor.newInstance();
                obj.fromByteArray(dados);

                if (obj.getForeignKey() == fkValue) {
                    resultados.add(obj);
                }
            } else {

                arquivo.skipBytes(tamanho);
            }

            pos = arquivo.getFilePointer();
        }

        return resultados;
    }

    public ArrayList<T> searchByInt(int fkValue) throws Exception {
        ArrayList<T> resultados = new ArrayList<>();

        if (arquivo.length() < 12)
            return resultados;

        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        long posListaExcluidos = arquivo.readLong();

        long pos = arquivo.getFilePointer();

        while (pos < arquivo.length()) {
            arquivo.seek(pos);

            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();

            if (lapide == ' ') {

                byte[] dados = new byte[tamanho];
                arquivo.readFully(dados);

                T obj = construtor.newInstance();
                obj.fromByteArray(dados);

                if (obj.getIntKey() == fkValue) {
                    resultados.add(obj);
                }
            } else {

                arquivo.skipBytes(tamanho);
            }

            pos = arquivo.getFilePointer();
        }

        return resultados;
    }

    public  String dumpCesto() throws Exception {
        return indice.dumpCestos();
    }

     public  String dumpDiretorio() throws Exception {
        return indice.dumpDiretorios();
    }

    public ArrayList<T> getAll() throws Exception {
        ArrayList<T> resultados = new ArrayList<>();

        if (arquivo.length() < 12)
            return resultados;

        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        long posListaExcluidos = arquivo.readLong();

        long pos = arquivo.getFilePointer();

        while (pos < arquivo.length()) {
            arquivo.seek(pos);

            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();

            if (lapide == ' ') {

                byte[] dados = new byte[tamanho];
                arquivo.readFully(dados);

                T obj = construtor.newInstance();
                obj.fromByteArray(dados);

                
                resultados.add(obj);
            } else {

                arquivo.skipBytes(tamanho);
            }

            pos = arquivo.getFilePointer();
        }

        return resultados;
    }

    public ArrayList<T> searchByString(String fkValue) throws Exception {
        ArrayList<T> resultados = new ArrayList<>();

        if (arquivo.length() < 12)
            return resultados;

        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        long posListaExcluidos = arquivo.readLong();

        long pos = arquivo.getFilePointer();

        while (pos < arquivo.length()) {
            arquivo.seek(pos);

            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();

            if (lapide == ' ') {

                byte[] dados = new byte[tamanho];
                arquivo.readFully(dados);

                T obj = construtor.newInstance();
                obj.fromByteArray(dados);

                if (obj.getString().compareTo(fkValue) == 0) {
                    resultados.add(obj);
                }
            } else {

                arquivo.skipBytes(tamanho);
            }

            pos = arquivo.getFilePointer();
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

        // Caso lista vazia
        if (endereco == -1) {
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
            return;
        }

        long proximo;
        while (true) {
            arquivo.seek(endereco + 1);
            int tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            // Inserir antes (ordenado por tamanho)
            if (tamanho > tamanhoEspaco) {
                arquivo.seek(posicao == 4 ? posicao : posicao + 3);
                arquivo.writeLong(enderecoEspaco);

                arquivo.seek(enderecoEspaco + 3);
                arquivo.writeLong(endereco);
                return;
            }

            // Inserir no final
            if (proximo == -1) {
                arquivo.seek(endereco + 3);
                arquivo.writeLong(enderecoEspaco);

                arquivo.seek(enderecoEspaco + 3);
                arquivo.writeLong(-1);
                return;
            }

            posicao = endereco;
            endereco = proximo;
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            int tamanho = arquivo.readShort();
            long proximo = arquivo.readLong();

            if (tamanho >= tamanhoNecessario) {
                // Remove da lista
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
