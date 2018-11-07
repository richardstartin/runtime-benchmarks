package com.openkappa.runtime.gc;

import org.openjdk.jmh.annotations.*;

import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class CursoredScannerState {

  public enum Scannner {
    SCANNER1 {
      @Override
      CursoredScanner scanner(String triggerName) {
        return new CursoredScanner1(triggerName);
      }
    },
    SCANNER2 {
      @Override
      CursoredScanner scanner(String triggerName) {
        return new CursoredScanner2(triggerName);
      }
    };
    abstract CursoredScanner scanner(String triggerName);
  }

  public static class Data {

    private final String[] names;
    private final long[] values;

    public Data(String[] names, long[] values) {
      this.names = names;
      this.values = values;
    }

    public int size() {
      return names.length;
    }

    public String getName(int i) {
      return names[i];
    }

    public long getValue(int i) {
      return values[i];
    }
  }

  @Param({"SCANNER1", "SCANNER2"})
  Scannner scannerType;

  @Param("trigger1")
  String triggerName;

  @Param({"10", "100"})
  int size;

  CursoredScanner cursoredScanner;
  Data data;


  @Setup(Level.Iteration)
  public void setup() {
    this.cursoredScanner = scannerType.scanner(triggerName);
    this.data = new Data(getNames(), getValues());
    System.gc();
  }

  private String[] getNames() {
    String[] names = new String[size];
    for (int i = 0; i < names.length; ++i) {
      names[i] = randomString(triggerName.length());
    }
    names[ThreadLocalRandom.current().nextInt(names.length)] = triggerName;
    return names;
  }

  private long[] getValues() {
    long[] values = new long[size];
    for (int i = 0; i < values.length; ++i) {
      values[i] = ThreadLocalRandom.current().nextLong();
    }
    return values;
  }

  private static String randomString(int size) {
    byte[] data = new byte[size];
    ThreadLocalRandom.current().nextBytes(data);
    return Base64.getEncoder().encodeToString(data);
  }
}
