package io.github.kusoroadeolu.clique.internal.markup;


import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.List;

@InternalApi(since = "3.2.0")
public class ParseToken {
    final int start;
    int end;
    final List<AnsiCode> styles;


    public ParseToken(int start, int end,List<AnsiCode> styles) {
        this.start = start;
        this.styles = styles;
        this.end = end;
    }


    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public List<AnsiCode> styles() {
        return styles;
    }
}