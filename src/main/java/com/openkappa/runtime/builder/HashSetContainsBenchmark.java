package com.openkappa.runtime.builder;

import org.openjdk.jmh.annotations.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class HashSetContainsBenchmark {

    @Param({"8", "96", "1024", "8192"})
    int setSize;

    private HashSet<Baseline> baselineSet;
    private Baseline presentBaseline;
    private Baseline missingBaseline;

    private HashSet<Builder> builderSet;
    private Builder presentBuilder;
    private Builder missingBuilder;

    @Setup(Level.Trial)
    public void setup() {
        setupBaselineState();
        setupBuilderState();
    }

    private void setupBaselineState() {
        baselineSet = new HashSet<>();
        for (int i = 0; i < setSize; ++i) {
            while(!baselineSet.add(newBaselineInstance()));
        }
        presentBaseline = baselineSet.iterator().next();
        do {
            missingBaseline = newBaselineInstance();
        } while (baselineSet.contains(missingBaseline));
    }

    private void setupBuilderState() {
        builderSet = new HashSet<>();
        for (int i = 0; i < setSize; ++i) {
            while(!builderSet.add(newBuilderInstance()));
        }
        presentBuilder = builderSet.iterator().next();
        do {
            missingBuilder = newBuilderInstance();
        } while (builderSet.contains(missingBuilder));
    }

    @Benchmark
    public boolean findPresentBaselineInstance() {
        return baselineSet.contains(presentBaseline);
    }

    @Benchmark
    public boolean findMissingBaselineInstance() {
        return baselineSet.contains(missingBaseline);
    }

    @Benchmark
    public boolean findPresentBuilderInstance() {
        return builderSet.contains(presentBuilder);
    }

    @Benchmark
    public boolean findMissingBuilderInstance() {
        return builderSet.contains(missingBuilder);
    }


    private static Baseline newBaselineInstance() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Baseline(UUID.randomUUID().toString(),
                random.nextInt(),
                UUID.randomUUID().toString(),
                Instant.ofEpochMilli(random.nextLong(0, Instant.now().toEpochMilli())));
    }

    private static Builder newBuilderInstance() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Builder(UUID.randomUUID().toString(),
                random.nextInt(),
                UUID.randomUUID().toString(),
                Instant.ofEpochMilli(random.nextLong(0, Instant.now().toEpochMilli())));
    }
}
