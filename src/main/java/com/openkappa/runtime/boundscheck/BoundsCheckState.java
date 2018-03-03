package com.openkappa.runtime.boundscheck;


import org.openjdk.jmh.annotations.*;

import static com.openkappa.runtime.DataUtil.createDoubleArray;

@State(Scope.Thread)
public class BoundsCheckState {

    @Param({"100", "1000", "10000"})
    int size;

    double[] data;

    int index;


    @Setup(Level.Invocation)
    public void init() {
        data = createDoubleArray(size);
        index = size - 1;
    }
}
