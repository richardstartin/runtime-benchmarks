package com.openkappa.runtime.inlining.hashers;

import com.openkappa.runtime.DataUtil;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleHashable implements Hashable {

  public SimpleHashable(byte[] data, int length, int random) {
    this.data = data;
    this.length = length;
    this.random = random;
  }

  public static Hashable createRandom(int length) {
    return new SimpleHashable(DataUtil.createByteArray(length), length, ThreadLocalRandom.current().nextInt());
  }

  private final byte[] data;
  private final int length;
  private final int random;
  @Override
  public void accept(Hasher hasher) {
    hasher.hash(length);
    hasher.hash(data);
    hasher.hash(random);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SimpleHashable that = (SimpleHashable) o;
    return length == that.length &&
            random == that.random &&
            Arrays.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    int result = length + 31 * random;
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }
}
