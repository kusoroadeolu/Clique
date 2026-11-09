package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;


@Stable(since = "3.2.0")
public class BoxDrawTable extends AbstractTable {
    private final String topLeft, topRight, bottomLeft, bottomRight;

    public BoxDrawTable(TableConfiguration configuration) {
        this(configuration, "┌", "┐", "└", "┘");
    }

    BoxDrawTable(TableConfiguration configuration, String tl, String tr, String bl, String br) {
        super(configuration);
        this.topLeft = tl; this.topRight = tr; this.bottomLeft = bl; this.bottomRight = br;
    }

    @Override
    String rowEdge() {
        return border("│");
    }

    @Override
    String cellSeparator() {
        return border("│");
    }

    @Override
    String top(int[] w) {
        return line(w, topLeft, "─", "┬", topRight);
    }

    @Override
    String headerSeparator(int[] w) {
        return line(w, "├", "─", "┼", "┤");
    }

    @Override
    String bottom(int[] w) {
        return line(w, bottomLeft, "─", "┴", bottomRight);
    }
}