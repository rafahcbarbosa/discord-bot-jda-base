package com.base.Encryption;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Encriptor {

    private static int p = 37;
    private static int q = 41;
    private static int n = 1517;
    private static int z = 1440;
    private static int e = 7;
    private static int d = 823;

    public static String Criptografar(String texto) {

        String criptografado = "";

        BigInteger bigN = BigInteger.valueOf(n);
        BigInteger bigE = BigInteger.valueOf(e);

        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            int valor = b & 0xFF;  
            BigInteger bi = BigInteger.valueOf(valor);

            BigInteger cript = bi.modPow(bigE, bigN);

            criptografado = criptografado + cript + " ";
        }

        return criptografado.trim();
    }

    public static String Descriptografar(String cript) {

        String[] partes = cript.split(" ");

        BigInteger bigN = BigInteger.valueOf(n);
        BigInteger bigD = BigInteger.valueOf(d);

        byte[] bytes = new byte[partes.length];
        int i = 0;

        for (String parte : partes) {
            BigInteger c = new BigInteger(parte);

            BigInteger dec = c.modPow(bigD, bigN);

            int valor = dec.intValue();

            bytes[i] = (byte) valor;
            i++;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
