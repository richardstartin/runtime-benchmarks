package com.openkappa.runtime.builder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Instant;

public class Builder {

    private final String a;
    private final int b;
    private final String c;
    private final Instant d;

    public Builder(String a, int b, String c, Instant d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Builder builder = (Builder) o;

        return new EqualsBuilder()
                .append(b, builder.b)
                .append(a, builder.a)
                .append(c, builder.c)
                .append(d, builder.d)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(a)
                .append(b)
                .append(c)
                .append(d)
                .toHashCode();
    }
}
