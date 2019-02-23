package com.openkappa.runtime.inlining.hashers;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RecursiveHashable implements Hashable {

  public static Hashable createRandom(int length) {
    return new RecursiveHashable(SimpleHashable.createRandom(length), ThreadLocalRandom.current().nextInt());
  }

  private final Hashable child;
  private final int random;

  public RecursiveHashable(Hashable child, int random) {
    this.child = child;
    this.random = random;
  }


  @Override
  public void accept(Hasher hasher) {
    child.accept(hasher);
    hasher.hash(random);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RecursiveHashable that = (RecursiveHashable) o;
    return random == that.random &&
            Objects.equals(child, that.child);
  }

  @Override
  public int hashCode() {
    return child.hashCode() * 31 + random;
  }
}
