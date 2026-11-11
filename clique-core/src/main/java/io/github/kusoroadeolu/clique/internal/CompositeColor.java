package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Collection;

@InternalApi(since = "3.2.0")
public class CompositeColor implements AnsiCode {
    private final String ansiSequence;

    public CompositeColor(AnsiCode... codes) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(codes).forEach(c -> sb.append(c.ansiSequence()));
        ansiSequence = sb.toString();
    }

    public CompositeColor(Collection<AnsiCode> codes) {
        StringBuilder sb = new StringBuilder();
        codes.forEach(c -> sb.append(c.ansiSequence()));
        ansiSequence = sb.toString();
    }

    @Override
    public String ansiSequence() {
        return ansiSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CompositeColor that = (CompositeColor) o;
        return ansiSequence.equals(that.ansiSequence);
    }

    @Override
    public int hashCode() {
        return ansiSequence.hashCode();
    }
}
