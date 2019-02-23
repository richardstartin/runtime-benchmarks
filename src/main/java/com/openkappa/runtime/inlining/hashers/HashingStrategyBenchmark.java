package com.openkappa.runtime.inlining.hashers;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

@OutputTimeUnit(MICROSECONDS)
public class HashingStrategyBenchmark {

  @Benchmark
  public int hashCode(OneHasher hasher) {
    return hasher.getHashable().hashCode();
  }

  @Benchmark
  public int hashOneHasher(OneHasher hasher) {
    return hash(hasher);
  }

  @Benchmark
  public int hashTwoHashers(TwoHashers hasher) {
    return hash(hasher);
  }

  @Benchmark
  public int hashThreeHashers(ThreeHashers hasher) {
    return hash(hasher);
  }

  private int hash(HashingBenchmarkState state) {
    Hasher hasher = state.getHasher();
    state.getHashable().accept(hasher);
    return hasher.getHash();
  }
}
