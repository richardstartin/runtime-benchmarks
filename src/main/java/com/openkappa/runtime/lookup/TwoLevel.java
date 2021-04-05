package com.openkappa.runtime.lookup;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;

@State(Scope.Benchmark)
public class TwoLevel {

    private final Map<String, Object> empty = new HashMap<>();

    private Map<Pair, Object> pairMap;
    private Map<String, Map<String, Object>> twoLevelMap;

    @Param({"5", "10", "100"})
    int level1Cardinality;
    @Param({"5", "10", "100"})
    int level2Cardinality;

    String[] level1Keys;
    String[] level2Keys;

    String[][] inputs;

    @Setup(Level.Trial)
    public void setup() {
        pairMap = new HashMap<>();
        twoLevelMap = new HashMap<>();
        level1Keys = new String[level1Cardinality];
        level2Keys = new String[level2Cardinality];
        for (int i = 0; i < level1Cardinality; ++i) {
            level1Keys[i] = UUID.randomUUID().toString();
            twoLevelMap.put(new String(level1Keys[i]), new HashMap<>());
        }
        for (int i = 0; i < level2Cardinality; ++i) {
            level2Keys[i] = UUID.randomUUID().toString();
        }
        for (int i = 0; i < level1Cardinality; ++i) {
            for (int j = 0; j < level2Cardinality; ++j) {
                pairMap.put(new Pair(new String(level1Keys[i]), new String(level2Keys[j])), new Object());
                twoLevelMap.get(level1Keys[i]).put(new String(level2Keys[j]), new Object());
            }
        }
        SplittableRandom random = new SplittableRandom(0);
        inputs = new String[1024][2];
        for (int i = 0; i < 1024; ++i) {
            inputs[i][0] = level1Keys[random.nextInt(level1Cardinality)];
            inputs[i][1] = level2Keys[random.nextInt(level2Cardinality)];
        }
    }

    @Benchmark
    @OperationsPerInvocation(1024)
    public void getPair(Blackhole bh) {
        for (String[] input : inputs) {
            bh.consume(pairMap.get(new Pair(input[0], input[1])));
        }
    }

    @Benchmark
    @OperationsPerInvocation(1024)
    public void getTwoLevel(Blackhole bh) {
        for (String[] input : inputs) {
            bh.consume(twoLevelMap.getOrDefault(input[0], empty).get(input[1]));
        }
    }
}
