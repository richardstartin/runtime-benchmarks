package com.openkappa.runtime.stream;

import org.openjdk.jmh.annotations.*;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class LongStreams {

  @Param({"100", "10000", "100000"})
  int size;

  @Benchmark
  public long parallelMapReduce() {
    return LongStream.range(0, size).parallel().mapToInt(Long::bitCount).sum();
  }

  @Benchmark
  public long sortedParallelMapReduce() {
    return LongStream.range(0, size).sorted().parallel().mapToInt(Long::bitCount).sum();
  }


  @Benchmark
  public long mapReduce() {
    return LongStream.range(0, size).mapToInt(Long::bitCount).sum();
  }

  @Benchmark
  public long sortedMapReduce() {
    return LongStream.range(0, size).sorted().mapToInt(Long::bitCount).sum();
  }
}
