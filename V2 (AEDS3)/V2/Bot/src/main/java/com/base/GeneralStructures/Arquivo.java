package com.base.GeneralStructures;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.base.interfaces.Registro;
import com.base.interfaces.RegistroArvoreBMais;
import com.base.interfaces.RegistroHashExtensivel;
import com.base.GeneralStructures.HashExtensivel;
import com.base.CRUD_Meeting.IndiceMeeting;
import com.base.CRUD_Meeting.Meeting;

public class Arquivo<
        T extends Registro,
        I extends RegistroHashExtensivel<I>,
        F extends RegistroArvoreBMais<F>
    >  {
    private static final int TAM_CABECALHO = 4;
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    private HashExtensivel<I> indice;
    private Constructor<I> construtorIndice;
     protected ArvoreBMais<F> indiceFK;


    public Arquivo(String nomeArquivo, Constructor<T> construtor, Constructor<I> construtorIndice) throws Exception {
        File diretorio = new File("./dados");
        if (!diretorio.exists()) diretorio.mkdir();

        diretorio = new File("./dados/" + nomeArquivo);
        if (!diretorio.exists()) diretorio.mkdir();

        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

         this.construtorIndice = construtorIndice;
            this.indice = new HashExtensivel<>(
            construtorIndice,
            3,
            "./dados/" + nomeArquivo + "/diretorio.db",
            "./dados/" + nomeArquivo + "/cestos.db"
        );

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);    // Último ID usado
            arquivo.writeLong(-1);  // Lista de registros excluídos
        }
    }
            public boolean create(T obj) throws Exception {
                arquivo.seek(0);
                int novoID = arquivo.readInt() + 1;
                arquivo.seek(0);
                arquivo.writeInt(novoID);
                obj.setId(novoID);

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

         public T readAt(long pos) throws Exception {
            arquivo.seek(pos);
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

        public <F extends RegistroArvoreBMais<F>> ArrayList<T> searchByFK(ArvoreBMais<F> indiceFK, int fkValue) throws Exception {

            if (indiceFK == null)
                throw new IllegalStateException("B+ index (indiceFK) not initialized");

            // Create a temporary FK key
            F chave;
            try {
                chave = indiceFK.getConstrutor().newInstance();
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException e) {
                throw new RuntimeException("Failed to create instance of FK", e);
            }

            chave.setId(fkValue); // assuming 'id' in F represents the foreign key

            // Find all entries with this FK
            ArrayList<F> indices = indiceFK.read(chave);

            ArrayList<T> resultados = new ArrayList<>();

            for (F ind : indices) {
                long pos = ind.getPos();          
                T obj = this.readAt(pos);         // readAt reads by file pointer
                if (obj != null)
                    resultados.add(obj);
            }


            return resultados;
        }





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



        public void close() throws Exception {
            arquivo.close();
        }

}
