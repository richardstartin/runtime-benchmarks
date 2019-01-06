package com.openkappa.runtime.inlining;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class ThreeHashers implements HashingBenchmarkState {

  @Param({"0", "100", "250", "500"})
  int difficulty;

  @Param({"SIMPLE", "RECURSIVE"})
  HashableScenario scenario;

  private int i;
  private Hasher[] hashers = new Hasher[16];
  protected Hashable hashable;

  @Setup(Level.Trial)
  public void init() {
    int i = 0;
    for (; i + 3 < hashers.length; i += 3) {
      hashers[i] = new Hasher1();
      hashers[i + 1] = new Hasher2();
      hashers[i + 2] = new Hasher3();
    }
    for (; i < hashers.length; ++i) {
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
