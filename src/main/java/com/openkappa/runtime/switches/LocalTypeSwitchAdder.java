package com.openkappa.runtime.switches;

public class LocalTypeSwitchAdder implements Adder {

  private static final int INT = 0;
  private static final int LONG = 1;
  private static final int FLOAT = 2;
  private static final int DOUBLE = 3;

  private final int type;
  private final Object data;

  public LocalTypeSwitchAdder(int[] data) {
    this.type = INT;
    this.data = data;
  }

  public LocalTypeSwitchAdder(long[] data) {
    this.type = LONG;
    this.data = data;
  }

  public LocalTypeSwitchAdder(float[] data) {
    this.type = FLOAT;
    this.data = data;
  }

  public LocalTypeSwitchAdder(double[] data) {
    this.type = DOUBLE;
    this.data = data;
  }

  @Override
  public double add(double value) {
    switch (type) {
      case INT:
        return value + ((int[]) data)[0];
      case LONG:
        return value + ((long[]) data)[0];
      case FLOAT:
        return value + ((float[]) data)[0];
      case DOUBLE:
        return value + ((double[]) data)[0];
      default:
        throw new IllegalArgumentException();
    }
  }
}
