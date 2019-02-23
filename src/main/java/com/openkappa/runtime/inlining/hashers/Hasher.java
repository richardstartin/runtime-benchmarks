package com.openkappa.runtime.inlining.hashers;

public interface Hasher {
  void hash(byte[] data);
  void hash(int data);
  int getHash();
}
