
package com.base.Compression;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode implements Comparable<HuffmanNode> {
    byte b;
    int frequencia;
    HuffmanNode esquerdo, direito;

    public HuffmanNode(byte b, int f) {
        this.b = b;
        this.frequencia = f;
        esquerdo = direito = null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return this.frequencia - o.frequencia;
    }
}

public class Huffman {

    public static HashMap<Byte, String> codifica(byte[] sequencia) {
        HashMap<Byte, Integer> mapaDeFrequencias = new HashMap<>();
        for (byte c : sequencia) {
            mapaDeFrequencias.put(c, mapaDeFrequencias.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : mapaDeFrequencias.keySet()) {
            pq.add(new HuffmanNode(b, mapaDeFrequencias.get(b)));
        }

        while (pq.size() > 1) {
            HuffmanNode esquerdo = pq.poll();
            HuffmanNode direito = pq.poll();

            HuffmanNode pai = new HuffmanNode((byte) 0, esquerdo.frequencia + direito.frequencia);
            pai.esquerdo = esquerdo;
            pai.direito = direito;

            pq.add(pai);
        }

        HuffmanNode raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        constroiCodigos(raiz, "", codigos);

        return codigos;
    }

    private static void constroiCodigos(HuffmanNode no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) {
            return;
        }

        if (no.b != 0) {
            codigos.put(no.b, codigo);
        }

        constroiCodigos(no.esquerdo, codigo + "0", codigos);
        constroiCodigos(no.direito, codigo + "1", codigos);
    }

    // Versão buscando na tabela de códigos.
    public static byte[] decodifica(String sequenciaCodificada, HashMap<Byte, String> codigos) {
        ByteArrayOutputStream sequenciaDecodificada = new ByteArrayOutputStream();
        StringBuilder codigoAtual = new StringBuilder();

        for (int i = 0; i < sequenciaCodificada.length(); i++) {
            codigoAtual.append(sequenciaCodificada.charAt(i));
            for (byte b : codigos.keySet()) {
                if (codigos.get(b).equals(codigoAtual.toString())) {
                    sequenciaDecodificada.write(b);
                    codigoAtual = new StringBuilder();
                    break;
                }
            }
        }
        return sequenciaDecodificada.toByteArray();
    }

    public static HuffmanEntry cod(String frase) {

        HashMap<Byte, String> codigos = codifica(frase.getBytes());
        VetorDeBits sequenciaCodificada = new VetorDeBits();
        int i = 0;
        for (byte b : frase.getBytes()) {
            String codigo = codigos.get(b);
            for (char c : codigo.toCharArray()) {
                if (c == '0')
                    sequenciaCodificada.clear(i++);
                else
                    sequenciaCodificada.set(i++);
            }
        }
        byte[] vb = sequenciaCodificada.toByteArray();
        return new HuffmanEntry(codigos, vb);
    }

    public static void saveEntries(ArrayList<HuffmanEntry> entries, File file) throws Exception {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {

            out.writeInt(entries.size());

            for (HuffmanEntry entry : entries) {

                out.writeInt(entry.codes.size());
                for (var e : entry.codes.entrySet()) {
                    out.writeByte(e.getKey());
                    byte[] codeBytes = e.getValue().getBytes(StandardCharsets.UTF_8);
                    out.writeInt(codeBytes.length);
                    out.write(codeBytes);
                }

    
                out.writeInt(entry.encodedBytes.length);
                out.write(entry.encodedBytes);
            }
        }
    }

    public static ArrayList<HuffmanEntry> loadEntries(File file) throws Exception {
        ArrayList<HuffmanEntry> list = new ArrayList<>();

        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {

            int total = in.readInt();

            for (int i = 0; i < total; i++) {

                int codeCount = in.readInt();
                HashMap<Byte, String> codes = new HashMap<>();

                for (int j = 0; j < codeCount; j++) {
                    byte b = in.readByte();
                    int len = in.readInt();
                    byte[] codeBytes = new byte[len];
                    in.readFully(codeBytes);
                    codes.put(b, new String(codeBytes, StandardCharsets.UTF_8));
                }

                int ebLen = in.readInt();
                byte[] encodedBytes = new byte[ebLen];
                in.readFully(encodedBytes);

                list.add(new HuffmanEntry(codes, encodedBytes));
            }
        }

        return list;
    }

    public static String bytesToBitString(byte[] vb) {
        VetorDeBits vetor = new VetorDeBits(vb);
        return vetor.toString(); 
    }

}