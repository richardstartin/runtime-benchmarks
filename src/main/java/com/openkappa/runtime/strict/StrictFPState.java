package com.openkappa.runtime.strict;

import org.openjdk.jmh.annotations.*;

import java.util.SplittableRandom;

@State(Scope.Benchmark)
public class StrictFPState {

    double[] a;
    byte[] padding;
    double[] b;

    @Param({"1024"})
    int size;

    @Param("92010")
    long seed;

    double s;


    @Setup(Level.Trial)
    public void init() {
        SplittableRandom random = new SplittableRandom(seed);
        s = random.nextDouble();
        a = new double[size];
        padding = new byte[35];
        b = new double[size];
    }
}
