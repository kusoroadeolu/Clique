package io.github.kusoroadeolu.clique.internal.utils;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

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
        var pattern = Pattern.quote(parser.configuration().getDelimiter());
        return Arrays.stream(string.split(pattern))
                .map(s -> getOrThrow(s, parser.configuration().getStyleContext()))
                .toArray(AnsiCode[]::new);
    }
}
