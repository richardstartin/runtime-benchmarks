package com.openkappa.runtime.ascii;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.Charset;

@State(Scope.Benchmark)
public class AsciiString {

    @Param({"US-ASCII", "ISO-8859-1", "UTF-8"})
    String charsetName;

    @Param({"4", "20", "200", "2000"})
    int size;

    Charset charset;
    private byte[] bytes;

   @Setup(Level.Trial)
   public void setup() {
       this.charset = Charset.forName(charsetName);
       this.bytes = asciiBytes(size);
   }


   @Benchmark
   public String stringFromBytes() {
       return new String(bytes, charset);
   }


    private static byte[] asciiBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; ++i) {
            bytes[i] = (byte) (i & 0x7F);
        }
        return bytes;
    }
}
