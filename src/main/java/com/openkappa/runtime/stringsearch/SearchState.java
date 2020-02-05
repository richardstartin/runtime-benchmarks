package com.openkappa.runtime.stringsearch;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jol.info.GraphLayout;

import java.util.SplittableRandom;

@State(Scope.Thread)
public class SearchState {

    public enum SearcherType {
        BIT_MATRIX {
            @Override
            public Searcher compile(byte[] term) {
                return new BitMatrixSearcher(term);
            }
        },
        SPARSE_BIT_MATRIX {
            @Override
            public Searcher compile(byte[] term) {
                return new SparseBitMatrixSearcher(term);
            }
        };
        public abstract Searcher compile(byte[] term);
    }

    @Param({"100", "1000", "2000"})
    int dataLength;

    @Param({ "3",  "9",
            "13", "19",
            "23", "29",
            "33", "39",
            "53", "59"})
    int termLength;

    @Param({"7", "9", "11", "12"})
    int logVariety;

    @Param("90210")
    long seed;

    @Param
    SearcherType searcherType;

    Searcher searcher;

    private byte[][] data;
    byte[] term;
    private int instance;

    public byte[] next() {
        return data[instance++ & (data.length - 1)];
    }

    @Setup(Level.Trial)
    public void init() {
        data = new byte[1 << logVariety][dataLength];
        SplittableRandom random = new SplittableRandom(seed);
        term = new byte[termLength];
        random.nextBytes(term);
        searcher = searcherType.compile(term);
        for (byte[] datum : data) {
            tryFill(datum, random, term);
        }
        System.out.println(GraphLayout.parseInstance(searcher).toFootprint());
    }

    public static void main(String... args) {
        for (SearcherType type : SearcherType.values()) {
            SearchState searchState = new SearchState();
            searchState.logVariety = 10;
            searchState.searcherType = type;
            searchState.termLength = 8;
            searchState.dataLength = 1000;
            searchState.init();
            System.out.println(new String(searchState.term));
            for (byte[] datum : searchState.data) {
                System.out.println(searchState.searcher.find(datum));
            }
        }
    }


    private void tryFill(byte[] data, SplittableRandom random, byte[] term) {
        random.nextBytes(data);
        int startPosition = dataLength - termLength - random.nextInt(10);
        System.arraycopy(term, 0, data, startPosition, term.length);
        if (searcher.find(data) != startPosition) {
            tryFill(data, random, term);
        }
    }

}
