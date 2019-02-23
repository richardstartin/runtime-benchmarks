package com.openkappa.runtime.inlining.escapee;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Instant;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

@OutputTimeUnit(MICROSECONDS)
public class EscapeeBenchmark {

  public static Escapee<Instant> getNewEscapee(int i, int count) {
    switch (i % count) {
      case 0:
        return new Escapee1();
      case 1:
        return new Escapee2();
      case 2:
        return new Escapee3();
      case 3:
        return new Escapee4();
      default:
        return null;
    }
  }

  public enum Scenario {
    ONE {
      @Override
      void fill(Escapee[] escapees) {
        for (int i = 0; i < escapees.length; ++i) {
          escapees[i] = getNewEscapee(i, 1);
        }
      }
    },
    TWO {
      @Override
      void fill(Escapee[] escapees) {
        for (int i = 0; i < escapees.length; ++i) {
          escapees[i] = getNewEscapee(i, 2);
        }
      }
    },
    THREE {
      @Override
      void fill(Escapee[] escapees) {
        for (int i = 0; i < escapees.length; ++i) {
          escapees[i] = getNewEscapee(i, 3);
        }
      }
    },
    FOUR {
      @Override
      void fill(Escapee[] escapees) {
        for (int i = 0; i < escapees.length; ++i) {
          escapees[i] = getNewEscapee(i, 4);
        }
      }
    };
    abstract void fill(Escapee[] escapees);
  }

  @State(Scope.Benchmark)
  public static class InstantEscapeeState {
    Escapee<Instant>[] escapees;

    @Param({"ONE", "TWO", "THREE", "FOUR"})
    Scenario scenario;

    @Param({"true", "false"})
    boolean isPresent;

    int size = 4;

    String input;


    @Setup(Level.Trial)
    public void init() {
      escapees = new Escapee[size];
      scenario.fill(escapees);
      input = isPresent ? "" : null;
    }
  }

  @Benchmark
  @OperationsPerInvocation(4)
  public void mapValue(InstantEscapeeState state, Blackhole bh) {
    for (Escapee<Instant> escapee : state.escapees) {
      bh.consume(escapee.map(state.input, x -> Instant.now()).orElseGet(Instant::now));
    }
  }


  @State(Scope.Benchmark)
  public static class StringEscapeeState {
    Escapee<String>[] escapees;

    @Param({"ONE", "TWO", "THREE", "FOUR"})
    Scenario scenario;

    @Param({"true", "false"})
    boolean isPresent;

    int size = 4;

    String input;
    String ifPresent;
    String ifAbsent;

    @Setup(Level.Trial)
    public void init() {
      escapees = new Escapee[size];
      scenario.fill(escapees);
      ifPresent = UUID.randomUUID().toString();
      ifAbsent = UUID.randomUUID().toString();
      input = isPresent ? "" : null;
    }
  }

  @Benchmark
  @OperationsPerInvocation(4)
  public void mapValueNoAllocation(StringEscapeeState state, Blackhole bh) {
    for (Escapee<String> escapee : state.escapees) {
      bh.consume(escapee.map(state.input, x -> state.ifPresent).orElseGet(() -> state.ifAbsent));
    }
  }

}
