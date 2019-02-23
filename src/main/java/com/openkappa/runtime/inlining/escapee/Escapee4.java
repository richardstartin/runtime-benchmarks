package com.openkappa.runtime.inlining.escapee;

import java.util.Optional;
import java.util.function.Function;

public class Escapee4<T> implements Escapee<T> {
  @Override
  public <S> Optional<T> map(S value, Function<S, T> mapper) {
    return Optional.ofNullable(value).map(mapper);
  }
}
