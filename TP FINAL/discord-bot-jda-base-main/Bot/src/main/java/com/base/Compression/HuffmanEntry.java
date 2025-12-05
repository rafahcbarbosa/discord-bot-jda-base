package com.base.Compression;


import java.util.HashMap;

public class HuffmanEntry {

    public HashMap<Byte, String> codes;
    public byte[] encodedBytes;

    public HuffmanEntry(HashMap<Byte, String> codes, byte[] encodedBytes) {
        this.codes = codes;
        this.encodedBytes = encodedBytes;
    }
}
