package com.openkappa.runtime.stringsearch.generators;

public interface DataGenerator {

    byte nextByte();

    default void nextBytes(byte[] data) {
        for (int i = 0; i < data.length; ++i) {
            data[i] = nextByte();
        }
    }
}
