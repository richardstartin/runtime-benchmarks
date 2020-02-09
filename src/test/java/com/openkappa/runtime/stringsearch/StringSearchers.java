package com.openkappa.runtime.stringsearch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class StringSearchers {

    @Parameterized.Parameters(name = "{0}/{1}")
    public static Object[][] params() {
        return new Object[][] {
                {"abcdefgh", "ab"},
                {"abcdefgh", "bc"},
                {"abcdefgh", "cd"},
                {"abcdefgh", "de"},
                {"abcdefgh", "ef"},
                {"abcdefgh", "fgh"},
                {"abcdefgh", "ab1"},
                {"abcdefgh", "bc1"},
                {"abcdefgh", "cd1"},
                {"abcdefgh", "de1"},
                {"abcdefgh", "ef1"},
                {"abcdefgh", "fgh1"},
                {"1", "0"},
                {"1", "1"},
                {"011111110", "111111110"},
                {"111111111111111111111111111111111111", "1"},
                {"011111111111111111111111111111111111", "1"},
                {"011111111111111111111111111111111111", "011111111111111111111111"},
                {"101011010100100001", "0010"},
                {"asjf;whfwqppiqweyqguifhwehdpqiwevpiqwhfnqwjnqpiwehdABC1lksjwhfo[whef[o", "ABC1"},
                {"asjf;whfwqppiqweyqguifhwehdpqiwevpiqwhfnqwjnqpiwehdABC1lksjwhfo[whef[o", "ABC2"},
                {"ababcdfeeeeeeeeeepopopopopopopoenoughaskljdl;aksjd", "enough"}
        };
    }

    private final String data;
    private final String term;
    private final int expected;

    public StringSearchers(String data, String term) {
        this.data = data;
        this.term = term;
        this.expected = data.indexOf(term);
    }

    @Test
    public void verifyBitMatrix() {
        assertEquals(expected, new BitMatrixSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifySparseBitMatrix() {
        assertEquals(expected, new SparseBitMatrixSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyUnsafeSparseBitMatrix() {
        assertEquals(expected, new UnsafeSparseBitMatrixSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyUnsafeBitMatrix() {
        assertEquals(expected, new UnsafeBitMatrixSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyUnsafeSWARSparseBitMatrix() {
        assertEquals(expected, new UnsafeSWARSparseBitMatrixSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyBitSliced() {
        assertEquals(expected, new BitSlicedSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyUnsafeBitSliced() {
        assertEquals(expected, new UnsafeBitSlicedSearcher(term.getBytes()).find(data.getBytes()));
    }

    @Test
    public void verifyUnsafeBitSlicedSWAR() {
        assertEquals(expected, new UnsafeBitSlicedSWARSearcher(term.getBytes()).find(data.getBytes()));
    }
}
