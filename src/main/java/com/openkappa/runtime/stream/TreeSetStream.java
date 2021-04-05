package com.openkappa.runtime.stream;

import org.openjdk.jmh.annotations.*;

import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class TreeSetStream {

  private TreeSet<String> set;

  @Param({"100", "10000", "100000"})
  int size;

  @Setup(Level.Trial)
  public void setup() {
    set = new TreeSet<>();
    for (int i = 0; i < size; ++i) {
      set.add(UUID.randomUUID().toString());
    }
  }

  @Benchmark
  public int size() {
    return set.size();
  }

  @Benchmark
  public long count() {
    return set.stream().count();
  }

  @Benchmark
  public long distinctCount() {
    return set.stream().distinct().count();
  }


}
