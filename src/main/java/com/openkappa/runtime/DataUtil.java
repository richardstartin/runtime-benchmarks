package com.openkappa.runtime;

import java.util.concurrent.ThreadLocalRandom;

public class DataUtil {

  public static double[] createDoubleArray(int size) {
    double[] array = new double[size];
    for (int i = 0; i < size; ++i) {
      array[i] = rand().nextDouble();
    }
    return array;
  }


  public static double[] createDoubleArrayGaussian(int size) {
    double[] array = new double[size];
    for (int i = 0; i < size; ++i) {
      array[i] = rand().nextGaussian();
    }
    return array;
  }



  public static float[] createFloatArray(int size) {
    float[] array = new float[size];
    for (int i = 0; i < size; ++i) {
      array[i] = (float)rand().nextDouble();
    }
    return array;
  }


  public static long[] createLongArray(int size) {
    long[] array = new long[size];
    for (int i = 0; i < size; ++i) {
      array[i] = rand().nextLong();
    }
    return array;
  }


  public static int[] createIntArray(int size) {
    int[] array = new int[size];
    for (int i = 0; i < size; ++i) {
      array[i] = rand().nextInt();
    }
    return array;
  }

  public static short[] createShortArray(int size) {
    short[] array = new short[size];
    for (int i = 0; i < size; ++i) {
      array[i] = (short)rand().nextInt();
    }
    return array;
  }

  public static char[] createCharArray(int size) {
    char[] array = new char[size];
    for (int i = 0; i < size; ++i) {
      array[i] = (char)rand().nextInt();
    }
    return array;
  }

  public static byte[] createByteArray(int size) {
    byte[] array = new byte[size];
    rand().nextBytes(array);
    return array;
  }
  
  
  private static final ThreadLocalRandom rand() {
    return ThreadLocalRandom.current();
  }
}

