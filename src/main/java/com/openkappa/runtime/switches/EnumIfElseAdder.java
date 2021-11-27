package com.openkappa.runtime.switches;

public class EnumIfElseAdder implements Adder {

  private final Type type;
  private final Object data;

  public EnumIfElseAdder(int[] data) {
    this.type = Type.INT;
    this.data = data;
  }

  public EnumIfElseAdder(long[] data) {
    this.type = Type.LONG;
    this.data = data;
  }

  public EnumIfElseAdder(float[] data) {
    this.type = Type.FLOAT;
    this.data = data;
  }

  public EnumIfElseAdder(double[] data) {
    this.type = Type.DOUBLE;
    this.data = data;
  }

  @Override
  public double add(double value) {
    if (type == Type.INT) {
      return value + ((int[]) data)[0];
    } else if (type == Type.LONG) {
      return value + ((long[]) data)[0];
    } else if (type == Type.FLOAT) {
      return value + ((float[]) data)[0];
    } else if (type == Type.DOUBLE) {
      return value + ((double[]) data)[0];
    } else {
      throw new IllegalArgumentException();
    }
  }
}
