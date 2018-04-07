package com.openkappa.runtime.gc;


import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(4)
@Fork(jvmArgsPrepend = {"-XX:+UseSerialGC","-XX:+UseCondCardMark", "-mx1G", "-ms1G"}, value = 1)
public class SerialGCStoreBenchmark {

  IntegerAcceptor integerAcceptor;

  IntAcceptor intAcceptor;

  private Integer boxed;
  private Integer neighbour;
  private Integer[] values;
  private Integer distantNeighbour;

  @Setup(Level.Iteration)
  public void init() {
    intAcceptor = new IntAcceptor();
    integerAcceptor = new IntegerAcceptor();
    this.boxed = ThreadLocalRandom.current().nextInt(256, 512);
    this.neighbour = ThreadLocalRandom.current().nextInt(256, 512);
    this.values = new Integer[1 << 21];
    for (int i = 0; i < values.length; ++i) {
      values[i] = ThreadLocalRandom.current().nextInt(256, 65536);
    }
    this.distantNeighbour = ThreadLocalRandom.current().nextInt(256, 512);
  }

  @Benchmark
  public int storeInt() {
    intAcceptor.setValue(boxed);
    return intAcceptor.getValue();
  }


  @Benchmark
  public Integer storeInteger() {
    integerAcceptor.setValue(boxed);
    return integerAcceptor.getValue();
  }

  @Benchmark
  public Integer storeIntegerAndNeighbour() {
    integerAcceptor.setValue(boxed);
    int first = integerAcceptor.getValue();
    integerAcceptor.setValue(neighbour);
    return first + integerAcceptor.getValue();
  }

  @Benchmark
  public Integer storeIntegerAndDistantNeighbour() {
    integerAcceptor.setValue(boxed);
    int first = integerAcceptor.getValue();
    integerAcceptor.setValue(distantNeighbour);
    return first + integerAcceptor.getValue();
  }
}
