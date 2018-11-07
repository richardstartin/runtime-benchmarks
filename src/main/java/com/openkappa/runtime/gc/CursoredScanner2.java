package com.openkappa.runtime.gc;

public class CursoredScanner2 implements CursoredScanner {

  private final String trigger;
  private String current;
  private long cursor;

  public CursoredScanner2(String trigger) {
    this.trigger = trigger;
  }

  @Override
  public void writeName(String name) {
    this.current = name;
  }

  @Override
  public void writeLong(long value) {
    if (trigger.equals(current)) {
      this.cursor = value;
    }
  }

  @Override
  public long getCursor() {
    return cursor;
  }

}
