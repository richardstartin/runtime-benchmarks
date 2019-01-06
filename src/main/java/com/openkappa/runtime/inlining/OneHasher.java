package com.openkappa.runtime.inlining;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class OneHasher implements HashingBenchmarkState {

  @Param({"0", "100", "250", "500"})
  int difficulty;

  @Param({"SIMPLE", "RECURSIVE"})
  HashableScenario scenario;

  private int i;
  private Hasher[] hashers = new Hasher[16];
  private Hashable hashable;

  @Setup(Level.Trial)
  public void init() {
    for (int i = 0; i < hashers.length; ++i) {
      hashers[i] = new Hasher1();
    }
    hashable = scenario.createHashable(difficulty);
  }

  @Override
  public Hasher getHasher() {
    return hashers[i++ & (hashers.length - 1)];
  }

  @Override
  public Hashable getHashable() {
    return hashable;
  }
}
