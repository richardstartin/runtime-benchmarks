package com.openkappa.runtime.switches;

public class EnumSwitchAdder implements Adder {

  private final Type type;
  private final Object data;

  public EnumSwitchAdder(int[] data) {
    this.type = Type.INT;
    this.data = data;
  }

  public EnumSwitchAdder(long[] data) {
    this.type = Type.LONG;
    this.data = data;
  }

  public EnumSwitchAdder(float[] data) {
    this.type = Type.FLOAT;
    this.data = data;
  }

  public EnumSwitchAdder(double[] data) {
    this.type = Type.DOUBLE;
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
