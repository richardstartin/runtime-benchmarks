package com.openkappa.runtime.stringsearch;

public class SparseBitMatrixSearcher implements Searcher {

    private final long[] masks;
    private byte[] positions;
    private final long success;

    public SparseBitMatrixSearcher(byte[] searchString) {
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
        this.masks = new long[cardinality + 1];
        this.positions = new byte[256];
        int index = 0;
        for (byte key : searchString) {
            int position = rank(key, existence);
            positions[key & 0xFF] = (byte)position;
            masks[position] |= (1L << index);
            ++index;
        }
        this.success = 1L << (searchString.length - 1);
    }

    public int find(byte[] data) {
        long current = 0L;
        for (int i = 0; i < data.length; ++i) {
            int value = data[i] & 0xFF;
            long mask = masks[positions[value] & 0xFF];
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
        int position = 1;
        while (i < wi) {
            position += Long.bitCount(existence[i]);
            ++i;
        }
        return position + Long.bitCount(existence[wi] & ((1L << value) - 1));
    }
}

