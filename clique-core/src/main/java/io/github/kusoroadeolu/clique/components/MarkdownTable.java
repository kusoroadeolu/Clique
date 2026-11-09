package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

@Stable(since = "3.2.0")
public class MarkdownTable extends AbstractTable {
    public MarkdownTable(TableConfiguration configuration) { super(configuration); }

    @Override
    String rowEdge() {
        return border("|");
    }

    @Override
    String cellSeparator() {
        return border("|");
    }

    @Override
    String headerSeparator(int[] w) {
        return line(w, "|", "-", "|", "|");
    }
}