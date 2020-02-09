package com.openkappa.runtime.stringsearch;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static com.openkappa.runtime.stringsearch.SparseUtil.compilePattern;

public class UnsafeBitSlicedSWARSearcher implements Searcher {

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

    private final long address;
    private final long first;
    private final long success;

    public UnsafeBitSlicedSWARSearcher(byte[] term) {
        if (term.length > 64) {
            throw new IllegalArgumentException("Too many bytes");
        }
        this.address = UNSAFE.allocateMemory(16 * Long.BYTES * 2);
        UNSAFE.setMemory(address, 16 * Long.BYTES * 2, (byte)0);
        long word = 1L;
        for (byte b : term) {
            UNSAFE.putLong(lowAddress(b & 0xF), word | UNSAFE.getLong(lowAddress(b & 0xF)));
            UNSAFE.putLong(highAddress((b >>> 4) & 0xF), word | UNSAFE.getLong(highAddress((b >>> 4) & 0xF)));
            word <<= 1;
        }
        this.success = 1L << (term.length - 1);
        this.first = compilePattern(term[0]);
    }

    @Override
    public int find(byte[] text) {
        long current = 0L;
        int i = 0;
        for (; i + 7 < text.length; i += Long.BYTES) {
            long word = first ^ UNSAFE.getLong(text, i + BYTE_ARRAY_OFFSET);
            long tmp = (word & 0x7F7F7F7F7F7F7F7FL) + 0x7F7F7F7F7F7F7F7FL;
            tmp = ~(tmp | word | 0x7F7F7F7F7F7F7F7FL);
            int j = Long.numberOfTrailingZeros(tmp) >>> 3;
            if (j != Long.BYTES) { // found the first byte
                for (int k = i + j; k + 1 < text.length; k += 2) {
                    long h2 = UNSAFE.getLong(highAddress((text[k] >>> 4) & 0xF));
                    long l2 = UNSAFE.getLong(lowAddress(text[k] & 0xF));
                    long h1 = UNSAFE.getLong(highAddress((text[k+1] >>> 4) & 0xF));
                    long l1 = UNSAFE.getLong(lowAddress(text[k+1] & 0xF));
                    current = (((current << 1) | 1) & h1 & l1) | (((current << 2) | 2) & h2 & l2);
                    if (current == 0 && (k & (Long.BYTES - 1)) == 0) {
                        break;
                    }
                    if ((current & success) == success) {
                        return k - Long.numberOfTrailingZeros(success);
                    }
                }
            }
        }
        for (; i < text.length; ++i) {
            long highMask = UNSAFE.getLong(highAddress((text[i] >>> 4) & 0xF));
            long lowMask = UNSAFE.getLong(lowAddress(text[i] & 0xF));
            current = ((current << 1) | 1) & highMask & lowMask;
            if ((current & success) == success) {
                return i - Long.numberOfTrailingZeros(success);
            }
        }
        return -1;
    }

    private long lowAddress(int position) {
        return address + Long.BYTES * position;
    }

    private long highAddress(int position) {
        return address + Long.BYTES * (position + 16);
    }
}
