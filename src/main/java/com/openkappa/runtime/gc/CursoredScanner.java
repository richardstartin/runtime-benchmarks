package com.openkappa.runtime.gc;

public interface CursoredScanner {

  void writeName(String name);

  void writeLong(long value);

  long getCursor();
}
