package com.openkappa.runtime.inlining;

public interface Hasher {
  void hash(byte[] data);
  void hash(int data);
  int getHash();
}
