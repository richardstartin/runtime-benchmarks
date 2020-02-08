package com.openkappa.runtime.stringsearch;

public class BitSlicedSearcher implements Searcher {

    private final long[] low;
    private final long[] high;
    private final long success;

    public BitSlicedSearcher(byte[] term) {
        if (term.length > 64) {
            throw new IllegalArgumentException("Too many bytes");
        }
        this.low = new long[16];
        this.high = new long[16];
        long word = 1L;
        for (byte b : term) {
            low[b & 0xF] |= word;
            high[(b >>> 4) & 0xF] |= word;
            word <<= 1;
        }
        this.success = 1L << (term.length - 1);
    }

    @Override
    public int find(byte[] text) {
        long current = 0L;
        for (int i = 0; i < text.length; ++i) {
            long highMask = high[(text[i] >>> 4) & 0xF];
            long lowMask = low[text[i] & 0xF];
            current = ((current << 1) | 1) & highMask & lowMask;
            if ((current & success) == success) {
                return i - Long.numberOfTrailingZeros(success);
            }
        }
        return -1;
    }
}
