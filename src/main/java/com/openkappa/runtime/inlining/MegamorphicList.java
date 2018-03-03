package com.openkappa.simd.inlining;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.openkappa.runtime.DataUtil.createByteArray;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MegamorphicList {

  private List<String>[] strings;

  @Setup(Level.Trial)
  public void init() {
    strings = new List[]{getArrayList(6), getFactoryList6(), getUnModifiableList(6)};
  }

  @Benchmark
  public int sumLength_ArrayList(Blackhole bh) {
    List<String> list = strings[0];
    int blackhole = 0;
    for (int i = 0; i < list.size(); ++i) {
      blackhole += list.get(i).length();
    }
    return blackhole ^ ThreadLocalRandom.current().nextInt(3);
  }


  @Benchmark
  public int sumLength_Factory() {
    List<String> list = strings[1];
    int blackhole = 0;
    for (int i = 0; i < list.size(); ++i) {
      blackhole += list.get(i).length();
    }
    return blackhole ^ ThreadLocalRandom.current().nextInt(3);
  }


  @Benchmark
  public int sumLength_Unmodifiable() {
    List<String> list = strings[2];
    int blackhole = 0;
    for (int i = 0; i < list.size(); ++i) {
      blackhole += list.get(i).length();
    }
    return blackhole ^ ThreadLocalRandom.current().nextInt(3);
  }

  @Benchmark
  public int sumLength_Random2() {
    List<String> list = strings[ThreadLocalRandom.current().nextInt(2)];
    int blackhole = 0;
    for (int i = 0; i < list.size(); ++i) {
      blackhole += list.get(i).length();
    }
    return blackhole;
  }

  @Benchmark
  public int sumLength_Random3() {
    List<String> list = strings[ThreadLocalRandom.current().nextInt(3)];
    int blackhole = 0;
    for (int i = 0; i < list.size(); ++i) {
      blackhole += list.get(i).length();
    }
    return blackhole;
  }

  private List<String> getUnModifiableList(int size) {
    return Collections.unmodifiableList(getArrayList(size));
  }


  private List<String> getFactoryList6() {
    return List.of(randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString()
    );
  }

  private List<String> getFactoryList6_() {
    List<String> list = new ArrayList<>();
    list.add(randomString());
    list.add(randomString());
    return list;
  }


  private List<String> getArrayList(int size) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      list.add(randomString());
    }
    return list;
  }


  private String randomString() {
    return new String(createByteArray(ThreadLocalRandom.current().nextInt(10, 20)));
  }

}
