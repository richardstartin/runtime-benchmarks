package com.openkappa.runtime.stringsearch;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeSWARSparseBitMatrixSearcher implements Searcher, AutoCloseable {

    private static final Unsafe UNSAFE;
    private static final int BYTE_ARRAY_OFFSET;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final long masksOffset;
    private final long positionsOffset;
    private final long success;
    private final long first;


    public UnsafeSWARSparseBitMatrixSearcher(byte[] searchString) {
        if (searchString.length > 64) {
            throw new IllegalArgumentException("Too many bytes");
        }
        int cardinality = 0;
        long[] existence = new long[4];
        for (byte key : searchString) {
            int value = key & 0xFF;
            long word = existence[value >>> 6];
            if ((word & (1L << value)) == 0) {
                ++cardinality;
                existence[value >>> 6] |= (1L << value);
            }
        }
        int masksStorage = (cardinality + 1) * Long.BYTES;
        int positionsStorage = 256;
        this.masksOffset = UNSAFE.allocateMemory(masksStorage + positionsStorage);
        UNSAFE.setMemory(masksOffset, masksStorage + positionsStorage, (byte)0);
        this.positionsOffset = masksOffset + masksStorage;
        int index = 0;
        for (byte key : searchString) {
            int position = rank(key, existence);
            UNSAFE.putByte(positionsOffset + (key & 0xFF), (byte)position);
            UNSAFE.putLong(masksOffset + position, UNSAFE.getLong(masksOffset + position) | (1L << index));
            ++index;
        }
        this.success = 1L << (searchString.length - 1);
        this.first = compilePattern(searchString[0]);
    }

    public int find(byte[] data) {
        long current = 0L;
        int i = 0;
        for (; i + 7 < data.length; i += Long.BYTES) {
            long word = first ^ UNSAFE.getLong(data, i + BYTE_ARRAY_OFFSET);
            long tmp = (word & 0x7F7F7F7F7F7F7F7FL) + 0x7F7F7F7F7F7F7F7FL;
            tmp = ~(tmp | word | 0x7F7F7F7F7F7F7F7FL);
            int j = Long.numberOfTrailingZeros(tmp) >>> 3;
            if (j != Long.BYTES) { // found the first byte
                for (int k = i + j; k < data.length; ++k) {
                    int value = data[k] & 0xFF;
                    int position = UNSAFE.getByte(positionsOffset + value) & 0xFF;
                    long mask = UNSAFE.getLong(masksOffset + position);
                    current = ((current << 1) | 1) & mask;
                    if (current == 0 && (k & (Long.BYTES - 1)) == 0) {
                        break;
                    }
                    if ((current & success) == success) {
                        return k - Long.numberOfTrailingZeros(success);
                    }
                }
            }
        }
        for (; i < data.length; ++i) {
            int value = data[i] & 0xFF;
            int position = UNSAFE.getByte(positionsOffset + value) & 0xFF;
            long mask = UNSAFE.getLong(masksOffset + position);
            current = ((current << 1) | 1) & mask;
            if ((current & success) == success) {
                return i - Long.numberOfTrailingZeros(success);
            }
        }
        return -1;
    }

    private static int rank(byte key, long[] existence) {
        int value = (key & 0xFF);
        int wi = value >>> 6;
        int i = 0;
        int position = 0;
        while (i < wi) {
            position += Long.bitCount(existence[i]);
            ++i;
        }
        return position + Long.bitCount(existence[wi] & ((1L << value) - 1));
    }

    @Override
    public void close() {
        UNSAFE.freeMemory(masksOffset);
    }

    private static long compilePattern(byte value) {
        long pattern = value & 0xFFL;
        return pattern
                | (pattern << 8)
                | (pattern << 16)
                | (pattern << 24)
                | (pattern << 32)
                | (pattern << 40)
                | (pattern << 48)
                | (pattern << 56);
    }
}
