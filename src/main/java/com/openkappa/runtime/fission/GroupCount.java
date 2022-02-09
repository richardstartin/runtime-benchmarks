package com.openkappa.runtime.fission;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(5)
@Measurement(iterations = 30, time = 1, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class GroupCount {
    @Param({"8", "64", "128"})
    int groups;
    @Param({"256", "512", "1024", "2048", "4096"})
    int length;

    private byte[] source;
    private byte[] dest;
    private boolean[] presence;

    @Setup
    public void setup() {
        source = new byte[length];
        dest = new byte[length];
        presence = new boolean[groups];
        SplittableRandom random = new SplittableRandom(42);
        for (int i = 0; i < source.length; i++) {
            source[i] = (byte) random.nextInt(groups);
        }
    }

    @Benchmark
    public void fused(Blackhole bh) {
        int numGroups = 0;
        for (int i = 0; i < source.length & i < dest.length; i++) {
            dest[i] = source[i];
            if (numGroups < groups && !presence[source[i]]) {
                presence[source[i] & 0xFF] = true;
                numGroups++;
            }
        }
        bh.consume(presence);
        Arrays.fill(presence, false);
    }

    @Benchmark
    public void fissured(Blackhole bh) {
        System.arraycopy(source, 0, dest, 0, source.length);
        int numGroups = 0;
        for (int i = 0; i < source.length & i < dest.length & numGroups < groups; i++) {
            if (!presence[source[i]]) {
                presence[source[i] & 0xFF] = true;
                numGroups++;
            }
        }
        bh.consume(presence);
        Arrays.fill(presence, false);
    }
}
