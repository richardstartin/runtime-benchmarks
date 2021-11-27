package com.openkappa.runtime.switches;

import static com.openkappa.runtime.switches.Types.*;

public class IntSwitchAdder implements Adder {

  private final int type;
  private final Object data;

  public IntSwitchAdder(int[] data) {
    this.type = INT;
    this.data = data;
  }

  public IntSwitchAdder(long[] data) {
    this.type = Types.LONG;
    this.data = data;
  }

  public IntSwitchAdder(float[] data) {
    this.type = Types.FLOAT;
    this.data = data;
  }

  public IntSwitchAdder(double[] data) {
    this.type = Types.DOUBLE;
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
