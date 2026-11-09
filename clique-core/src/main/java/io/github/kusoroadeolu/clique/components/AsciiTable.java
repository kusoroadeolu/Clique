package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

@Stable(since = "3.2.0")
public class AsciiTable extends AbstractTable {
    public AsciiTable(TableConfiguration configuration) { super(configuration); }

    @Override
    String rowEdge() {
        return border("|");
    }
    @Override
    String cellSeparator() {
        return border("|");
    }

    @Override
    String top(int[] w) {
        return rule(w);
    }

    @Override
    String headerSeparator(int[] w) {
        return rule(w);
    }

    @Override
    String rowSeparator(int[] w) {
        return rule(w);
    }

    private String rule(int[] w) {
        return line(w, "+", "-", "+", "+");
    }

}