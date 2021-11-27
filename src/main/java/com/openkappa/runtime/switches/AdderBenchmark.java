package com.openkappa.runtime.switches;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class AdderBenchmark {

  @State(Scope.Benchmark)
  public static abstract class AbstractState {

    @Param("42.42")
    double multiplier;

    Adder[] adders;

    @Setup(Level.Trial)
    public void setup() {
      adders = createMultipliers();
    }

    abstract Adder[] createMultipliers();
  }

  @State(Scope.Benchmark)
  public static class SingleTypeState extends AbstractState{

    @Param
    SingleScenario scenario;

    @Override
    Adder[] createMultipliers() {
      return scenario.build();
    }
  }

  @State(Scope.Benchmark)
  public static class MultiTypeState extends AbstractState{

    @Param
    MultiScenario scenario;

    @Override
    Adder[] createMultipliers() {
      return scenario.build();
    }
  }

  @Benchmark
  @OperationsPerInvocation(4)
  public void multi(MultiTypeState state, Blackhole bh) {
    for (Adder adder : state.adders) {
      bh.consume(adder.add(state.multiplier));
    }
  }

  @Benchmark
  @OperationsPerInvocation(4)
  public void single(SingleTypeState state, Blackhole bh) {
    for (Adder adder : state.adders) {
      bh.consume(adder.add(state.multiplier));
    }
  }
}
