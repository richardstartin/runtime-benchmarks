package com.openkappa.runtime.switches;

import com.openkappa.runtime.DataUtil;

public enum SingleScenario {
  POLYMORPHIC {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new PolymorphicAdder.DoubleAdder(DataUtil.createDoubleArray(1)),
              new PolymorphicAdder.DoubleAdder(DataUtil.createDoubleArray(1)),
              new PolymorphicAdder.DoubleAdder(DataUtil.createDoubleArray(1)),
              new PolymorphicAdder.DoubleAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  ENUM_IF_ELSE {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new EnumIfElseAdder(DataUtil.createDoubleArray(1)),
              new EnumIfElseAdder(DataUtil.createDoubleArray(1)),
              new EnumIfElseAdder(DataUtil.createDoubleArray(1)),
              new EnumIfElseAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  ENUM_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new EnumSwitchAdder(DataUtil.createDoubleArray(1)),
              new EnumSwitchAdder(DataUtil.createDoubleArray(1)),
              new EnumSwitchAdder(DataUtil.createDoubleArray(1)),
              new EnumSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  INT_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new IntSwitchAdder(DataUtil.createDoubleArray(1)),
              new IntSwitchAdder(DataUtil.createDoubleArray(1)),
              new IntSwitchAdder(DataUtil.createDoubleArray(1)),
              new IntSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  },
  LOCAL_TYPE_SWITCH {
    @Override
    public Adder[] build() {
      return new Adder[]{
              new LocalTypeSwitchAdder(DataUtil.createDoubleArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createDoubleArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createDoubleArray(1)),
              new LocalTypeSwitchAdder(DataUtil.createDoubleArray(1))
      };
    }
  };

  public abstract Adder[] build();
}
