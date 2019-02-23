package com.openkappa.runtime.inlining.hashers;

public interface HashingBenchmarkState {
  Hasher getHasher();

  Hashable getHashable();

}
