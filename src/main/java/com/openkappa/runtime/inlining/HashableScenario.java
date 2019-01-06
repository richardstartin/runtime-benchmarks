package com.openkappa.runtime.inlining;

public enum HashableScenario {
  SIMPLE {
    @Override
    public Hashable createHashable(int difficulty) {
      return SimpleHashable.createRandom(difficulty);
    }
  },
  RECURSIVE {
    @Override
    public Hashable createHashable(int difficulty) {
      return RecursiveHashable.createRandom(difficulty);
    }
  };

  public abstract Hashable createHashable(int difficulty);
}
