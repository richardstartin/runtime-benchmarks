package com.openkappa.runtime.stream;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StreamCount {

    @Param("262144")
    int size;
    int[] naturals;

    @Setup(Level.Trial)
    public void init() {
        this.naturals = new int[size];
        for (int i = 1; i < size; ++i) {
            naturals[i] = naturals[i-1] + 1;
        }
    }

    @Benchmark
    public long countRangeDistinct() {
        return IntStream.range(0, size).distinct().count();
    }


    @Benchmark
    public long headSum() {
        return IntStream.range(0, size).limit(1000).sum();
    }

    @Benchmark
    public long sortedHeadSum() {
        return IntStream.range(0, size).sorted().limit(1000).sum();
    }

    @Benchmark
    public long countIterator() {
        return IntStream.iterate(0, i -> i < size, i -> i + 1).count();
    }

    @Benchmark
    public long countRange() {
        return IntStream.range(0, size).count();
    }

    @Benchmark
    public long countHalfRange() {
        return IntStream.range(0, size).limit(size / 2).count();
    }

    @Benchmark
    public long countArray() {
        return IntStream.of(naturals).count();
    }

    @Benchmark
    public long sumIterator() {
        return IntStream.iterate(0, i -> i + 1).limit(1 << 18).sum();
    }

    @Benchmark
    public long sumRange() {
        return IntStream.range(0, 1 << 18).sum();
    }

    @Benchmark
    public long schoolBoySum() {
        return (1 << 18) * ((1 << 18) +  1) / 2;
    }

    @Benchmark
    public long sumArray() {
        return IntStream.of(naturals).sum();
    }
}
