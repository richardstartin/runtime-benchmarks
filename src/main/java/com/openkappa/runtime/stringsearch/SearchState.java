package com.openkappa.runtime.stringsearch;

import com.openkappa.runtime.stringsearch.generators.DataGenerator;
import com.openkappa.runtime.stringsearch.generators.DataSets;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        },
        UNSAFE_SPARSE_BIT_MATRIX {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeSparseBitMatrixSearcher(term);
            }
        },
        UNSAFE_BIT_MATRIX {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeBitMatrixSearcher(term);
            }
        },
        UNSAFE_SPARSE_BIT_MATRIX_SWAR {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeSWARSparseBitMatrixSearcher(term);
            }
        },
        BIT_SLICED {
            @Override
            public Searcher compile(byte[] term) {
                return new BitSlicedSearcher(term);
            }
        },
        UNSAFE_BIT_SLICED {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeBitSlicedSearcher(term);
            }
        },
        UNSAFE_BIT_SLICED_SWAR {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeBitSlicedSWARSearcher(term);
            }
        },
        UNSAFE_BIT_SLICED_SWAR_PAIR {
            @Override
            public Searcher compile(byte[] term) {
                return new UnsafeBitSlicedSWARPairSearcher(term);
            }
        }
        ;
        public abstract Searcher compile(byte[] term);
    }

    @Param({"100", "1000", "2000"})
    int dataLength;

    @Param({ "3", "19", "40", "59"})
    int termLength;

    @Param({"7", "12"})
    int logVariety;

    @Param("90210")
    long seed;

    @Param
    SearcherType searcherType;

    @Param
    DataSets dataSet;

    Searcher searcher;

    private byte[][] data;
    byte[] term;
    private int instance;

    public byte[] next() {
        return data[instance++ & (data.length - 1)];
    }

    @Setup(Level.Trial)
    public void init() throws IOException {
        Path dataDir = Paths.get(System.getProperty("data.dir", System.getProperty("user.dir")));
        data = new byte[1 << logVariety][dataLength];
        DataGenerator generator = dataSet.create(dataDir, seed);
        SplittableRandom random = new SplittableRandom(seed);
        term = new byte[termLength];
        generator.nextBytes(term);
        searcher = searcherType.compile(term);
        for (byte[] datum : data) {
            tryFill(datum, generator, random, term);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        if (searcher instanceof AutoCloseable) {
            ((AutoCloseable) searcher).close();
        }
    }

    public static void main(String... args) throws IOException {
        for (SearcherType type : SearcherType.values()) {
            SearchState searchState = new SearchState();
            searchState.logVariety = 10;
            searchState.searcherType = type;
            searchState.termLength = 8;
            searchState.dataLength = 1000;
            searchState.dataSet = DataSets.KING_JAMES_BIBLE;
            searchState.init();
            System.out.println(new String(searchState.term));
            for (byte[] datum : searchState.data) {
                System.out.println(searchState.searcher.find(datum));
            }
            if (searchState.searcher instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) searchState.searcher).close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void tryFill(byte[] data, DataGenerator generator, SplittableRandom random, byte[] term) {
        generator.nextBytes(data);
        int startPosition = dataLength - termLength - random.nextInt(10);
        System.arraycopy(term, 0, data, startPosition, term.length);
        int pos;
        if ((pos = searcher.find(data)) != startPosition) {
            System.out.println("Expected " + startPosition + " got " + pos);
            tryFill(data, generator, random, term);
        }
    }

}
