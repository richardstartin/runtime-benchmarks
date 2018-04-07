package com.openkappa.runtime.gc;

import org.openjdk.jmh.annotations.CompilerControl;

import java.util.concurrent.ThreadLocalRandom;

public class IntegerAcceptor {

  private Integer value;


  public void setValue(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public int getValueDynamic() {
    return value << ThreadLocalRandom.current().nextInt();
  }
}
