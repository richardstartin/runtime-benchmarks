package com.openkappa.runtime.gc;

public class CursoredScanner1 implements CursoredScanner {

  private final String trigger;

  private boolean atCursor;
  private long cursor;

  public CursoredScanner1(String trigger) {
    this.trigger = trigger;
  }

  @Override
  public void writeName(String name) {
    this.atCursor = trigger.equals(name);
  }

  @Override
  public void writeLong(long value) {
    if (atCursor) {
      this.cursor = value;
    }
  }

  @Override
  public long getCursor() {
    return cursor;
  }


}
