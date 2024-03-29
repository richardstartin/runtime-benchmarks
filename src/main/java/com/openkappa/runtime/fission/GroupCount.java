package com.openkappa.runtime.fission;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class GroupCount {

  @Param({"8", "64", "128"})
  int groups;
  @Param({"256", "512", "1024", "2048", "4096"})
  int length;

  private byte[] source;
  private byte[] dest;
  private boolean[] presence;

  @Setup(Level.Trial)
  public void setup() {
    source = new byte[length];
    dest = new byte[length];
    presence = new boolean[groups];
    SplittableRandom random = new SplittableRandom(42);
    for (int i = 0; i < source.length; i++) {
      source[i] = (byte) random.nextInt(groups);
    }
  }

  @Benchmark
  public void fused(Blackhole bh) {
    int numGroups = 0;
    for (int i = 0; i < source.length & i < dest.length; i++) {
      dest[i] = source[i];
      if (numGroups < groups && !presence[source[i]]) {
        presence[source[i] & 0xFF] = true;
        numGroups++;
      }
    }
    bh.consume(presence);
    Arrays.fill(presence, false);
  }

  @Benchmark
  public void fissured(Blackhole bh) {
    System.arraycopy(source, 0, dest, 0, source.length);
    int numGroups = 0;
    for (int i = 0; i < source.length & i < dest.length & numGroups < groups; i++) {
      if (!presence[source[i]]) {
        presence[source[i] & 0xFF] = true;
        numGroups++;
      }
    }
    bh.consume(presence);
    Arrays.fill(presence, false);
  }

}
