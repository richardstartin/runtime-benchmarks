package com.openkappa.runtime.lookup;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.HashMap;

@State(Scope.Benchmark)
public class HashMapResize {


    @Param({"10", "14"})
    int keys;

    @Param({"16", "24"})
    int capacity;


    @Benchmark
    public HashMap<Integer, Integer> loadHashMap() {
        HashMap<Integer, Integer> map = new HashMap<>(capacity);
        for (int i = 0; i < keys; ++i) {
            map.put(i, i);
        }
        return map;
    }
}
