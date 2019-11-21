package com.openkappa.runtime.svhm;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SwitchVsHashMap {

    static class Switches {

        public static String[] DIM1 = new String[] {"foo1", "bar1", "xyz1", "coke1"};
        public static String[] DIM2 = new String[] {"foo2", "bar2", "xyz2", "coke2"};

        public static long switchDim1(String x) {
            switch(x) {
                case "foo1" : return 0b11001L;
                case "bar1" : return 0b11010L;
                case "xyz1" : return 0b11100L;
                case "coke1" : return 0b11000L;
                default: return 0b100000L;
            }
        }

        public static long switchDim2(String x) {
            switch(x) {
                case "foo2" : return 0b11001L;
                case "bar2" : return 0b11010L;
                case "xyz2" : return 0b11100L;
                case "coke2" : return 0b11000L;
                default: return 0b100000L;
            }
        }
    }

    @State(Scope.Benchmark)
    public static class SwitchVsHashMapState {


        Map<String, Long> dim1;
        Map<String, Long> dim2;
        List<String> dim1Values = new ArrayList<>(128);
        List<String> dim2Values = new ArrayList<>(128);
        long default1 = Switches.switchDim1("missing");
        long default2 = Switches.switchDim2("missing");

        @Setup(Level.Trial)
        public void init() {
            dim1 = new HashMap<>();
            for (String x : Switches.DIM1) {
                dim1.put(x, Switches.switchDim1(x));
            }

            dim2 = new HashMap<>();
            for (String x : Switches.DIM2) {
                dim2.put(x, Switches.switchDim2(x));
            }

            for (int i = 0; i < 128; ++i) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    dim1Values.add(Switches.DIM1[i & 3]);
                } else {
                    byte[] data = new byte[4];
                    ThreadLocalRandom.current().nextBytes(data);
                    dim1Values.add(new String(data));
                }
            }

            for (int i = 0; i < 128; ++i) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    dim2Values.add(Switches.DIM2[i & 3]);
                } else {
                    byte[] data = new byte[4];
                    ThreadLocalRandom.current().nextBytes(data);
                    dim2Values.add(new String(data));
                }
            }

        }

        int i;
        int j;

        String key1() {
            ++i;
            int pos = i & 127;
            return dim1Values.get(pos);
        }

        String key2() {
            ++j;
            int pos = j & 127;
            return dim2Values.get(pos);
        }
    }

    @Benchmark
    public int matchHashMap(SwitchVsHashMapState state) {
        return Long.numberOfTrailingZeros(state.dim1.getOrDefault(state.key1(), state.default1) & state.dim2.getOrDefault(state.key2(), state.default2));
    }

    @Benchmark
    public int matchSwitch(SwitchVsHashMapState state) {
        return Long.numberOfTrailingZeros(Switches.switchDim1(state.key1()) & Switches.switchDim2(state.key2()));
    }
}
