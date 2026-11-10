package io.github.kusoroadeolu.clique.internal.utils;


import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.RGBColor;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.Locale;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.*;

@InternalApi(since = "3.2.0")
public final class StringUtils {
    private StringUtils(){}

    public static void clearStringBuilder(StringBuilder sb) {
        sb.setLength(ZERO);
    }

    public static Cell parseToCell(String text, MarkupParser parser) {
        return new Cell(parser.getOriginalString(text), parser.parse(text));
    }

    public static String parse(String text, MarkupParser parser) {
        return parser.parse(text);
    }

    public static String stripAnsi(String styled) {
        final int n = styled.length();
        final var clean = new StringBuilder(n);
        int i = 0;

        while (i < n) {
            char c = styled.charAt(i);

            if (c == ESC && i + 1 < n) {
                char next = styled.charAt(i + 1);

                if (next == LBRACKET) {
                    i += 2;
                    while (i < n) {
                        char f = styled.charAt(i++);
                        if (f >= 0x40 && f <= 0x7E) break;
                    }
                    continue;
                }

                if (next == RBRACKET) {
                    i += 2;
                    while (i < n) {
                        char o = styled.charAt(i);
                        if (o == BEL) { i++; break; }
                        if (o == ESC && nextCharEquals(styled, i + 1, '\\')) { i += 2; break; }
                        i++;
                    }
                    continue;
                }
            }

            clean.append(c);
            i++;
        }

        return clean.toString();
    }

    public static String normalizeStyle(String s) {
        return s.toLowerCase(Locale.ROOT).trim();
    }

    public static boolean nextCharEquals(String s, int pos, char ch){
        return pos < s.length() && s.charAt(pos) == ch;
    }


    public static RGBAnsiCode hex(String hex){
        return hexBase(hex, false);
    }

    public static RGBAnsiCode bgHex(String hex){
        return hexBase(hex, true);
    }

    public static RGBAnsiCode hexBase(String hex, boolean background){
        if (!hex.startsWith(HASH) || hex.length() != 7)
            throw new IllegalArgumentException("Invalid hex color: expected '#RRGGBB' but got '" + hex + "'");
        try {
            int red   = Integer.parseInt(hex.substring(1, 3), 16);
            int green = Integer.parseInt(hex.substring(3, 5), 16);
            int blue  = Integer.parseInt(hex.substring(5, 7), 16);
            return new RGBColor(red, green, blue, background);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color: '" + hex + "' contains non-hex characters (expected '#RRGGBB')", e);
        }
    }

    public static String styleAndReset(StyleBuilder sb, String border, AnsiCode[] borderColor) {
        var str = sb.appendAndReset(border, borderColor).toString();
        sb.clear();
        return str;
    }
}
