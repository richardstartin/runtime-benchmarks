package com.openkappa.runtime.strict;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class StrictFP {

    @Benchmark
    public void daxpyStrict(StrictFPState state, Blackhole bh) {
        daxpyStrict(state.s, state.a, state.b);
        bh.consume(state.a);
    }


    @Benchmark
    public void daxpy(StrictFPState state, Blackhole bh) {
        daxpy(state.s, state.a, state.b);
        bh.consume(state.a);
    }

    @Benchmark
    public double sumStrict(StrictFPState state) {
        return sumStrict(state.a);
    }

    @Benchmark
    public double sum(StrictFPState state) {
        return sum(state.a);
    }

    @Benchmark
    public double unrolledSumStrict(StrictFPState state) {
        return unrolledSumStrict(state.a);
    }

    @Benchmark
    public double unrolledSum(StrictFPState state) {
        return unrolledSum(state.a);
    }

    public static strictfp double sumStrict(double[] data) {
        double sum = 0D;
        for (int i = 0; i < data.length; ++i) {
            sum += data[i];
        }
        return sum;
    }

    public static double sum(double[] data) {
        double sum = 0D;
        for (int i = 0; i < data.length; ++i) {
            sum += data[i];
        }
        return sum;
    }


    public static strictfp double unrolledSumStrict(double[] data) {
        double sum1 = 0D;
        double sum2 = 0D;
        double sum3 = 0D;
        double sum4 = 0D;
        for (int i = 0; i + 3 < data.length; i += 4) {
            sum1 += data[i];
            sum2 += data[i+1];
            sum3 += data[i+2];
            sum4 += data[i+3];
        }
        return sum1 + sum2 + sum3 + sum4;
    }

    public static double unrolledSum(double[] data) {
        double sum1 = 0D;
        double sum2 = 0D;
        double sum3 = 0D;
        double sum4 = 0D;
        for (int i = 0; i + 3 < data.length; i += 4) {
            sum1 += data[i];
            sum2 += data[i+1];
            sum3 += data[i+2];
            sum4 += data[i+3];
        }
        return sum1 + sum2 + sum3 + sum4;
    }


    public static strictfp void daxpyStrict(double s, double[] a, double[] b) {
        for (int i = 0; i < a.length && i < b.length; ++i) {
            a[i] += s * b[i];
        }
    }

    public static strictfp void daxpy(double s, double[] a, double[] b) {
        for (int i = 0; i < a.length && i < b.length; ++i) {
            a[i] += s * b[i];
        }
    }
}
