package com.openkappa.runtime.shipilev;



import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3, jvmArgsAppend = {"-XX:LoopUnrollLimit=1"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ImplicitNP {

    @Param({"false", "true"})
    boolean blowup;

    volatile Holder h;

    @Setup
    public void setup() {
        if (blowup) {
            try {
                test();
            } catch (NullPointerException npe) {
                // swallow
            }
        }
        h = new Holder();
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    @Benchmark
    public int test() {
        int sum = 0;
        for (int c = 0; c < 100; c++) {
            sum += h.x;
        }
        return sum;
    }

    static class Holder {
        int x;
    }
}


