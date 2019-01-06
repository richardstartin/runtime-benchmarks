package com.openkappa.runtime.boundscheck;

import org.openjdk.jmh.annotations.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CheckIndex {


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public double RangePrefixSum_ManualCheck(BoundsCheckState state) {
        double[] data = state.data;
        int limit = state.index;
        if (limit < 0 || limit >= data.length) {
            throw new RuntimeException(limit + " >= " + data.length);
        }
        double result = 0D;
        for (int i = 0; i <= limit; ++i) {
            result += data[i];
        }
        return result;
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public double RangePrefixSum_NoCheck(BoundsCheckState state) {
        double[] data = state.data;
        int limit = state.index;
        double result = 0D;
        for (int i = 0; i <= limit; ++i) {
            result += data[i];
        }
        return result;
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public double PrefixRangeSum_CheckIndex(BoundsCheckState state) {
        double[] data = state.data;
        int limit = Objects.checkIndex(state.index, data.length);
        double result = 0D;
        for (int i = 0; i <= limit; ++i) {
            result += data[i];
        }
        return result;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public double PointAccess_ManualCheck(BoundsCheckState state) {
        double[] data = state.data;
        int index = state.index;
        if (index < 0 || index >= data.length) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + data.length);
        }
        return data[index];
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public double PointAccess_CheckIndex(BoundsCheckState state) {
        double[] data = state.data;
        int index = Objects.checkIndex(state.index, data.length);
        return data[index];
    }

}
