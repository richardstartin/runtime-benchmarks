package com.openkappa.runtime.fission;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(5)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class XorCount {

  @Param({"256", "512", "1024", "2048", "4096"})
  int size;

  private long[] left;
  private long[] right;

  @Setup(Level.Trial)
  public void setup() {
    left = new long[size];
    right = new long[size];
    for (int i = 0; i < size; i++) {
      left[i] = ThreadLocalRandom.current().nextLong();
      right[i] = ThreadLocalRandom.current().nextLong();
    }
  }


  @Benchmark
  public int fused() {
    int count = 0;
    for (int i = 0; i < left.length & i < right.length; i++) {
      left[i] ^= right[i];
      count += Long.bitCount(left[i]);
    }
    return count;
  }

  @Benchmark
  public int fissured() {
    int count = 0;
    for (int i = 0; i < left.length & i < right.length; i++) {
      left[i] ^= right[i];
    }
    for (long l : left) {
      count += Long.bitCount(l);
    }
    return count;
  }
}
