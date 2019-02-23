package com.openkappa.runtime.inlining.hashers;

public interface Hashable {
  void accept(Hasher hasher);
}
