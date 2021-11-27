package com.openkappa.runtime.enummap;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.UUID;

public class EnumMapBenchmark {

  @State(Scope.Benchmark)
  public static abstract class BaseState {
    @Param("10000")
    int size;

    @Param("42")
    int seed;

    int[] randomValues;

    @Setup(Level.Trial)
    public void setup() {
      SplittableRandom random = new SplittableRandom(seed);
      randomValues = new int[size];
      for (int i = 0; i < size; i++) {
        randomValues[i] = random.nextInt(0, Integer.MAX_VALUE);
      }
      fill(randomValues);
    }

    abstract void fill(int[] randomValues);
  }

  @State(Scope.Benchmark)
  public static class EnumMapState extends BaseState {

    EnumMap<AnEnum, String> map;
    AnEnum[] values;

    @Override
    void fill(int[] randomValues) {
      map = new EnumMap<>(AnEnum.class);
      values = new AnEnum[randomValues.length];
      AnEnum[] enumValues = AnEnum.values();
      int pos = 0;
      for (int i : randomValues) {
        values[pos++] = enumValues[i % enumValues.length];
      }
      for (AnEnum value : enumValues) {
        map.put(value, UUID.randomUUID().toString());
      }
    }
  }

  @State(Scope.Benchmark)
  public static class MixedState extends BaseState {

    EnumMap<AnEnum, String> map;
    String[] values;

    @Override
    void fill(int[] randomValues) {
      map = new EnumMap<>(AnEnum.class);
      values = new String[randomValues.length];
      AnEnum[] enumValues = AnEnum.values();
      int pos = 0;
      for (int i : randomValues) {
        values[pos++] = enumValues[i % enumValues.length].toString();
      }
      for (AnEnum value : enumValues) {
        map.put(value, UUID.randomUUID().toString());
      }
    }
  }

  @State(Scope.Benchmark)
  public static class HashMapState extends BaseState {

    HashMap<String, String> map;
    String[] values;

    @Override
    void fill(int[] randomValues) {
      map = new HashMap<>();
      values = new String[randomValues.length];
      AnEnum[] enumValues = AnEnum.values();
      int pos = 0;
      for (int i : randomValues) {
        values[pos++] = enumValues[i % enumValues.length].toString();
      }
      for (AnEnum value : enumValues) {
        map.put(value.toString(), UUID.randomUUID().toString());
      }
    }
  }

  @Benchmark
  public void enumMap(EnumMapState state, Blackhole bh) {
    for (AnEnum value : state.values) {
      bh.consume(state.map.get(value));
    }
  }

  @Benchmark
  public void hashMap(HashMapState state, Blackhole bh) {
    for (String value : state.values) {
      bh.consume(state.map.get(value));
    }
  }
}
