package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;

@Stable(since = "3.2.0")
public class CompactTable extends AbstractTable {
    public CompactTable(TableConfiguration configuration) {
        super(configuration);
    }

    @Override
    int cellPadding() {
        return 0;
    }

    @Override
    String rowEdge() {
        return "";
    }

    @Override
    String cellSeparator() {
        return BLANK.repeat(configuration.getPadding());
    }

    @Override
    String headerSeparator(int[] w) {
        var sb = new StringBuilder();
        for (int i = 0; i < w.length; i++) {
            if (i > 0) sb.append(cellSeparator());
            sb.append(border("-".repeat(w[i])));
        }
        return sb.toString();
    }
}