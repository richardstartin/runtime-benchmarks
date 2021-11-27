package com.openkappa.runtime.switches;

import com.openkappa.runtime.DataUtil;

public enum MultiScenario {
  POLYMORPHIC {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new PolymorphicAdder.IntAdder(DataUtil.createIntArray(1)),
              new PolymorphicAdder.LongAdder(DataUtil.createLongArray(1)),
              new PolymorphicAdder.FloatAdder(DataUtil.createFloatArray(1)),
              new PolymorphicAdder.DoubleAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  ENUM_IF_ELSE {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new EnumIfElseAdder(DataUtil.createIntArray(1)),
              new EnumIfElseAdder(DataUtil.createLongArray(1)),
              new EnumIfElseAdder(DataUtil.createFloatArray(1)),
              new EnumIfElseAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  ENUM_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new EnumSwitchAdder(DataUtil.createIntArray(1)),
              new EnumSwitchAdder(DataUtil.createLongArray(1)),
              new EnumSwitchAdder(DataUtil.createFloatArray(1)),
              new EnumSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  INT_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new IntSwitchAdder(DataUtil.createIntArray(1)),
              new IntSwitchAdder(DataUtil.createLongArray(1)),
              new IntSwitchAdder(DataUtil.createFloatArray(1)),
              new IntSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  LOCAL_TYPE_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new LocalTypeSwitchAdder(DataUtil.createIntArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createLongArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createFloatArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  };

  public abstract Adder[] build();


}
