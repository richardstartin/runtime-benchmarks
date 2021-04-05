package com.openkappa.runtime.stream;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ArrayListStream {

  private ArrayList<String> list;

  @Param({"100", "10000", "100000"})
  int size;

  @Setup(Level.Trial)
  public void setup() {
    list = new ArrayList<>(size);
    for (int i = 0; i < size; ++i) {
      list.add(UUID.randomUUID().toString());
    }
  }


  @Benchmark
  public long parallelMapReduce() {
    Collections.sort(list);
    return list.stream().parallel().mapToInt(String::length).sum();
  }

  @Benchmark
  public long sortedParallelMapReduce() {
    return list.stream().sorted().parallel().mapToInt(String::length).sum();
  }


}

