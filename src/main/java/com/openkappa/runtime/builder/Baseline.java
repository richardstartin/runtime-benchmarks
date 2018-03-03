package com.openkappa.runtime.builder;

import java.time.Instant;

public class Baseline {

    private final String a;
    private final int b;
    private final String c;
    private final Instant d;

    public Baseline(String a, int b, String c, Instant d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Baseline baseline = (Baseline) o;

        if (b != baseline.b) return false;
        if (!a.equals(baseline.a)) return false;
        if (!c.equals(baseline.c)) return false;
        return d.equals(baseline.d);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b;
        result = 31 * result + c.hashCode();
        result = 31 * result + d.hashCode();
        return result;
    }
}
