package com.openkappa.runtime.gc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

@OutputTimeUnit(MICROSECONDS)
public class CursoredScannerBenchmark {

  @Benchmark
  public long scan(CursoredScannerState state) {
    CursoredScanner scanner = state.cursoredScanner;
    CursoredScannerState.Data data = state.data;
    for (int i = 0; i < data.size(); ++i) {
      scanner.writeName(data.getName(i));
      scanner.writeLong(data.getValue(i));
    }
    return scanner.getCursor();
  }
}
