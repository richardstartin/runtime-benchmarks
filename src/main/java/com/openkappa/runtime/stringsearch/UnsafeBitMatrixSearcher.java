package com.openkappa.runtime.stringsearch;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Adapted from https://github.com/linasm/string-search-algos
 */
public class UnsafeBitMatrixSearcher implements Searcher, AutoCloseable {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final long masksOffset;
    private final long success;

    public UnsafeBitMatrixSearcher(byte[] searchString) {
        if (searchString.length > 64) {
            throw new IllegalArgumentException("Too many bytes");
        }
        masksOffset = UNSAFE.allocateMemory(256 * Long.BYTES);
        UNSAFE.setMemory(masksOffset, 256 * Long.BYTES, (byte)0);
        long word = 1L;
        for (byte key : searchString) {
            UNSAFE.putLong(maskAddress(key & 0xFF),
                    UNSAFE.getLong(maskAddress(key & 0xFF)) | word);
            word <<= 1;
        }
        this.success = 1L << (searchString.length - 1);
    }

    public int find(byte[] data) {
        long current = 0L;
        for (int i = 0; i < data.length; ++i) {
            long mask = UNSAFE.getLong(maskAddress(data[i] & 0xFF));
            current = ((current << 1) | 1) & mask;
            if ((current & success) == success) {
                return i - Long.numberOfTrailingZeros(success);
            }
        }
        return -1;
    }

    @Override
    public void close() {
        UNSAFE.freeMemory(masksOffset);
    }

    private long maskAddress(int position) {
        return masksOffset + (position * Long.BYTES);
    }
}
