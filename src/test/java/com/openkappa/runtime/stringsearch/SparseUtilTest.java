package com.openkappa.runtime.stringsearch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.openkappa.runtime.stringsearch.SparseUtil.rank;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SparseUtilTest {

    private final byte test;
    private final int expectedRank;
    private final long[] bitmap;

    public SparseUtilTest(byte[] data, byte test, int expectedRank) {
        this.test = test;
        this.expectedRank = expectedRank;
        this.bitmap = new long[4];
        for (byte b : data) {
            bitmap[(b & 0xFF) >>> 6] |= (1L << (b & 0xFF));
        }
    }

    @Parameterized.Parameters
    public static Object[][] params() {
        return new Object[][]{
                {new byte[] {1, 2, 3}, (byte)1, 0},
                {new byte[] {1, 2, 3}, (byte)2, 1},
                {new byte[] {1, 2, 3}, (byte)3, 2},
                {new byte[] {1, 65, (byte)129}, (byte)1, 0},
                {new byte[] {1, 65, (byte)129}, (byte)65, 1},
                {new byte[] {1, 65, (byte)129}, (byte)129, 2},
        };
    }

    @Test
    public void testRank() {
        assertEquals(expectedRank, rank(test, bitmap));
    }

}