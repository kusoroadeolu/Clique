module clique.core {
    requires transitive clique.spi;
    requires clique.core;
    uses io.github.kusoroadeolu.clique.spi.CliqueTheme;
    exports io.github.kusoroadeolu.clique.components;
    exports io.github.kusoroadeolu.clique.configuration;
    exports io.github.kusoroadeolu.clique.style;
    exports io.github.kusoroadeolu.clique.parser;
    exports io.github.kusoroadeolu.clique;
    exports io.github.kusoroadeolu.clique.exceptions;
}