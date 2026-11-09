package io.github.kusoroadeolu.clique.internal.utils;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;

import static io.github.kusoroadeolu.clique.internal.markup.PredefinedStyleContext.getOrThrow;

@InternalApi(since = "3.2.0")
public class ParserUtils {
    private static final AnsiCode[] NONE = new AnsiCode[0];

    private ParserUtils(){}

    public static AnsiCode[] getAnsiCodes(String string) {
        return getAnsiCodes(string,  MarkupParser.DEFAULT);
    }

    public static AnsiCode[] getAnsiCodes(String string, MarkupParser parser) {
        if (string.isBlank()) return NONE;
        return Arrays.stream(string.split(parser.configuration().getDelimiter()))
                .map(s -> getOrThrow(s.trim(), parser.configuration().getStyleContext()))
                .toArray(AnsiCode[]::new);
    }
}
