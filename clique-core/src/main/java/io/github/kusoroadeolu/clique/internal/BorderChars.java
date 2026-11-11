package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.configuration.BoxType;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.util.function.UnaryOperator;

@InternalApi(since = "3.2.0")
public record BorderChars(String hLine, String vLine, String topLeft, String topRight, String bottomLeft,
                          String bottomRight) {

    public static BorderChars from(BoxType type) {
        return switch (type) {
            case ASCII -> new BorderChars("-", "|", "+", "+", "+", "+");
            case DOUBLE_LINE -> new BorderChars("═", "║", "╔", "╗", "╚", "╝");
            case CLASSIC -> new BorderChars("─", "│", "┌", "┐", "└", "┘");
            case ROUNDED -> new BorderChars("─", "│", "╭", "╮", "╰", "╯");
        };
    }

    public BorderChars map(UnaryOperator<String> fn) {
        return new BorderChars(
                fn.apply(hLine), fn.apply(vLine),
                fn.apply(topLeft), fn.apply(topRight),
                fn.apply(bottomLeft), fn.apply(bottomRight)
        );
    }
}