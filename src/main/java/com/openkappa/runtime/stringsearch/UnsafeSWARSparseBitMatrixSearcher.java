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

    public static void main(String... args) {
        try (UnsafeSWARSparseBitMatrixSearcher search = new UnsafeSWARSparseBitMatrixSearcher(new byte[]{-81, -51, 29, 123, 57, -88, 32, -30})) {
            System.out.println("found it at " + search.find(new byte[]{94, 65, 67, -50, -13, -19, 112, 40, 123, -12, -49, -31, -100, -89, -22, -12, 64, -31, 35, 7, 31, -90, 52, -66, 52, -37, 36, -119, 39, 68, 35, -57, -107, -79, -79, -45, -5, 37, -31, 49, -43, -97, -16, -111, -31, 23, -28, -117, -25, 6, 24, 68, -83, -66, 109, 12, -64, 12, -24, 63, -85, 10, 107, 75, -43, 13, -76, -120, -97, 59, -34, 46, -17, 116, -30, 103, 127, 27, 111, -117, 111, 0, -100, 24, 121, 72, 80, 92, -52, 2, -78, 31, -40, 3, 82, -52, 21, 28, 106, -77, 121, -28, 65, -28, 37, -13, 109, 59, -48, 12, 66, -78, -25, -103, -13, 59, 113, 57, 95, 68, 6, -44, -99, 10, -92, 34, -43, -86, -78, -92, -42, 69, -78, -91, -73, 88, 110, 105, -93, -13, -56, -56, -104, 126, -51, 80, 52, 12, 40, -55, 113, -70, 115, 95, -23, -122, -16, -58, 30, 48, 97, -5, -57, -22, -62, 75, 25, -44, 35, -62, 49, -19, -67, 104, 0, 62, -2, -74, 62, 15, 91, -96, 81, 34, 63, -46, 125, -15, -78, 3, 46, -116, 11, -91, -11, -31, 46, 67, -34, -72, -48, -40, -51, -88, -104, 5, -29, 21, -20, 6, -111, -83, -16, -43, -50, 95, 122, 127, 32, -101, -80, 55, -111, 42, 1, 87, 112, 42, 69, -45, -32, -96, -38, 54, 40, 37, 77, -13, -101, 105, 77, 71, -39, -124, 64, 54, 28, -108, -47, 121, -70, 50, -35, -52, -124, -111, -116, 74, 120, -7, -72, 87, -18, 34, 86, -69, -88, 48, 32, 33, -67, -114, -110, -27, 101, -61, -102, 71, -19, 113, 103, -14, 6, -105, -103, -64, -57, -48, 76, -72, -58, -92, -97, 54, -99, -24, 41, 10, 52, 4, -66, -98, -103, -114, -74, 71, -59, -38, 64, 12, 67, 117, 74, 20, -112, -66, 12, 83, 105, -96, 24, 107, -21, 91, -47, -94, 28, 65, 41, 121, 126, -85, -91, -38, -90, 115, 55, -92, -46, -95, -95, 10, 60, 37, 66, -32, 95, 112, -100, 37, 47, -94, -57, 112, -25, 30, -1, 43, 87, 58, -36, -24, 62, -96, -17, 23, 90, 98, -76, -124, 95, -69, 127, -5, 43, 55, -88, 21, -108, 14, 47, -47, -30, 99, -21, -112, 91, 108, -126, -1, 36, 63, 118, 19, 90, 96, 10, 3, 34, -44, 116, -8, -29, -114, -86, -46, 46, 58, 71, -66, 18, 51, -87, 4, 43, 104, -99, -56, 45, 115, 54, -4, -113, -78, 86, -77, 71, 56, 24, 21, -126, 116, 116, -102, 39, -64, 39, -88, 28, 22, -52, -93, 74, 40, -88, -88, 72, 43, -101, -30, -68, 70, -120, -128, 48, 31, -94, 102, -53, -10, -72, -19, 19, 24, -9, 60, -112, -82, 91, -96, -94, -62, -70, 113, 35, 46, -124, -9, 75, -120, 64, 34, -128, -82, 49, -61, 31, 86, 13, 8, -32, 1, 63, 73, 8, -65, -39, -102, 40, 119, -10, -35, 35, -72, -41, -4, -31, 9, 76, 91, 82, 62, -95, 84, -17, 31, 81, -33, 34, -28, 101, 34, -99, 77, -125, 118, 113, -51, -10, -121, -105, -117, 3, -108, -83, -32, -97, -112, -98, -68, 94, -113, 99, -81, -20, -70, 86, 73, 28, -95, 11, 3, 94, -22, -90, 51, 61, 82, -88, -9, -118, 24, 41, 42, -109, 21, -31, 66, -117, 125, 82, 93, -110, -124, 38, 75, 41, 122, -121, 56, 47, -76, 14, 69, 13, 13, 59, -80, 81, 68, 49, 23, -86, -72, -118, 8, -113, 103, 83, -58, 92, -127, 116, -77, -52, -123, 103, 96, -93, -112, -105, -88, 48, 18, 93, -87, -31, -44, -56, 74, 108, 105, -126, -126, 77, 5, -81, -21, -42, -2, 22, -61, 110, 26, -67, 32, -18, 71, -105, -17, -91, -102, 70, 127, -36, -97, 100, -56, -27, -2, 120, 71, 11, 15, -116, -111, 107, -77, -24, 71, 4, 29, 98, -92, -54, -6, -3, 118, -84, 19, 33, 93, -119, 102, 98, 105, 82, -66, 42, 64, -14, -84, -59, 57, -17, 11, 35, -105, -15, 53, 123, 65, -86, -41, 14, -20, -87, 76, -92, -6, -97, -7, -102, -106, 124, -86, -85, -102, -111, -30, -98, 125, -15, 96, -71, -73, 75, 9, 39, 113, 116, -68, 50, 95, -56, -108, 80, -22, -34, 104, -106, -82, 111, 91, 81, -16, -117, -126, -100, -61, -72, -67, -19, -56, -118, 80, 106, 1, -20, -17, -43, 53, 41, 64, -117, -17, -22, 52, -106, -74, -13, -11, -68, -74, 121, 112, -65, -81, -114, 70, -14, -69, -64, 103, 82, 88, 94, -12, -48, -114, -111, -93, 79, 42, 84, 95, -70, -32, 34, -26, -73, 88, 55, -110, -18, -26, 119, -122, 24, 95, 3, 106, 64, -128, -2, 98, 126, -53, 15, -24, 70, -62, -4, -56, -80, 114, 119, -45, -36, -39, -119, 104, 24, -15, 76, -109, 30, -97, 71, -93, 13, 84, -3, 53, 44, -22, 63, 62, 122, -3, -36, -35, 33, -54, -89, 46, 70, -10, -102, 53, -25, 94, 56, 73, 103, 66, 53, 19, -78, -108, 49, -30, 96, 59, -29, 117, 56, -60, -109, -4, 122, -45, -62, 38, -62, -54, -30, -92, 77, -117, -112, -116, 90, 2, -44, -61, 124, 2, -70, 20, 43, -48, 87, 80, 98, -9, -79, -96, -19, 13, -62, 15, -109, -11, -38, 79, 32, 121, 108, -59, 67, 90, -59, -75, -60, -71, 54, 91, 31, 9, 46, 120, -75, -94, 32, 123, -54, -34, -31, 43, 109, 27, -59, -32, 55, -88, 18, -28, -76, 101, 101, 88, -30, 73, 85, -56, 100, -107, 33, -56, 26, 40, 108, 120, 58, -46, 116, -55, 19, 108, -18, 37, -85, 53, -128, -114, -109, 63, -66, 76, 32, 5, 38, -121, -108, 94, -109, -46, 71, 20, -31, -64, 11, -74, 95, -35, -109, -96, -81, -78, -81, -51, 29, 123, 57, -88, 32, -30, -18, 9}));
        }
        try (UnsafeSWARSparseBitMatrixSearcher search = new UnsafeSWARSparseBitMatrixSearcher("A12f".getBytes())) {
            System.out.println("found it at " + search.find("ashhdipiqwhciqwipbqciwbecpiqwbecA12fFKJAsflgqweiffbibdlasbflagiofbwcdp".getBytes()));
            System.out.println("found it at " + search.find("ashhdipiqwhciqwipbqciwbecpiqwbecA12fFKJasflgqweiffbibdlasbflagiofbwcdp".getBytes()));

            System.out.println("found it at " + search.find("ashhdipiqwhciqwipbqciwbecpiqwbecFKJAsflgqweiffbibdlasbflagiofbwcdp".getBytes()));
            System.out.println("found it at " + search.find("A12ashhdipiqwhciqwipbqciwbecpiqwbecFKJAsflgqweiffbibdlasbflagiofbwcdp".getBytes()));
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
