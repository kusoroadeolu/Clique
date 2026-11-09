package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;

public class RoundedBoxDrawTable extends BoxDrawTable {
    public RoundedBoxDrawTable(TableConfiguration configuration) {
        super(configuration, "╭", "╮", "╰", "╯");
    }
}
