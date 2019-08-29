package com.openkappa.runtime.enumit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.util.EnumSet;

public class EnumIterationBenchmark {

  @Benchmark
  public void valuesFour(Blackhole bh) {
    for (Four it : Four.values()) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void valuesEight(Blackhole bh) {
    for (Eight it : Eight.values()) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void valuesSixteen(Blackhole bh) {
    for (Sixteen it : Sixteen.values()) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void cachedFour(Blackhole bh) {
    for (Four it : Four.CACHED) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void cachedEight(Blackhole bh) {
    for (Eight it : Eight.CACHED) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void cachedSixteen(Blackhole bh) {
    for (Sixteen it : Sixteen.CACHED) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void enumSetFour(Blackhole bh) {
    for (Four it : EnumSet.allOf(Four.class)) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void enumSetEight(Blackhole bh) {
    for (Eight it : EnumSet.allOf(Eight.class)) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void enumSetSixteen(Blackhole bh) {
    for (Sixteen it : EnumSet.allOf(Sixteen.class)) {
      bh.consume(it.ordinal());
    }
  }

  @Benchmark
  public void bitIterationFour(Blackhole bh) {
    long word = 0xF;
    while (word != 0) {
      bh.consume(Long.numberOfTrailingZeros(word));
      word &= (word - 1);
    }
  }

  @Benchmark
  public void bitIterationEight(Blackhole bh) {
    long word = 0xFF;
    while (word != 0) {
      bh.consume(Long.numberOfTrailingZeros(word));
      word &= (word - 1);
    }
  }

  @Benchmark
  public void bitIterationSixteen(Blackhole bh) {
    long word = 0xFFF;
    while (word != 0) {
      bh.consume(Long.numberOfTrailingZeros(word));
      word &= (word - 1);
    }
  }
}
