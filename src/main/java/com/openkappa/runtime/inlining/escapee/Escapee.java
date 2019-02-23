package com.openkappa.runtime.inlining.escapee;

import java.util.Optional;
import java.util.function.Function;

public interface Escapee<T> {

  <S> Optional<T> map(S value, Function<S, T> mapper);
}
