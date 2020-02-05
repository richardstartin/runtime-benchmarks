package com.openkappa.runtime.stringsearch;

import org.openjdk.jol.info.GraphLayout;

public class Layout {

    public static void main(String... args) {
        BitMatrixSearcher bitMatrixSearcher = new BitMatrixSearcher(new byte[]{1, 2, 3});
        System.out.println(GraphLayout.parseInstance(bitMatrixSearcher).toFootprint());

        SparseBitMatrixSearcher smallSparseBitMatrixSearcher = new SparseBitMatrixSearcher(new byte[]{1, 2, 3});
        System.out.println(GraphLayout.parseInstance(smallSparseBitMatrixSearcher).toFootprint());

        byte[] term = new byte[64];
        for (byte i = 0; i < 64; ++i) {
            term[i] = i;
        }
        SparseBitMatrixSearcher worstCaseSparseBitMatrixSearcher = new SparseBitMatrixSearcher(term);
        System.out.println(GraphLayout.parseInstance(worstCaseSparseBitMatrixSearcher).toFootprint());

    }
}
