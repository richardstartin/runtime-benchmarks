package com.openkappa.runtime.switches;

public abstract class PolymorphicAdder implements Adder {

  @Override
  public abstract double add(double value);

  public static class IntAdder extends PolymorphicAdder {

    private final int[] ints;

    public IntAdder(int[] data) {
      this.ints = data;
    }

    @Override
    public double add(double value) {
      return value + ints[0];
    }
  }

  public static class LongAdder extends PolymorphicAdder {

    private final long[] longs;

    public LongAdder(long[] data) {
      this.longs = data;
    }

    @Override
    public double add(double value) {
      return value + longs[0];
    }
  }

  public static class FloatAdder extends PolymorphicAdder {

    private final float[] floats;

    public FloatAdder(float[] data) {
      this.floats = data;
    }

    @Override
    public double add(double value) {
      return value + floats[0];
    }
  }


  public static class DoubleAdder extends PolymorphicAdder {

    private final double[] doubles;

    public DoubleAdder(double[] data) {
      this.doubles = data;
    }

    @Override
    public double add(double value) {
      return value + doubles[0];
    }
  }
}
