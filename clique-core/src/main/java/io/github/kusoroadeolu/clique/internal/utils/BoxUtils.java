package io.github.kusoroadeolu.clique.internal.utils;


import io.github.kusoroadeolu.clique.configuration.TextAlign;
import io.github.kusoroadeolu.clique.internal.BorderChars;
import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.clique.style.StyleCode;

import java.util.List;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.NEWLINE;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.styleAndReset;

@InternalApi(since = "3.2.0")
public class BoxUtils {

    private BoxUtils(){}

    private static final String RESET = StyleCode.RESET.ansiSequence();

    public static void alignText(StyleBuilder sb, int idx, TextAlign textAlign, String spaces, List<Cell> wordWrap, String vLine, int padding) {
        final Cell cell = wordWrap.get(idx);
        final String ss = cell.styledText();
        final String fixed = BLANK.repeat(padding); //The padding in string


        final int fillSpace = spaces.length() - cell.width() - (padding * 2);
        switch (textAlign) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT ->
                sb.append(vLine)
                        .append(fixed)
                        .append(ss)
                        .append(RESET)
                        .append(BLANK.repeat(Math.max(0, fillSpace)))
                        .append(fixed)
                        .append(vLine)
                        .append(NEWLINE);

            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT ->
                sb.append(vLine)
                        .append(fixed)
                        .append(BLANK.repeat(Math.max(0, fillSpace)))
                        .append(ss)
                        .append(RESET)
                        .append(fixed)
                        .append(vLine)
                        .append(NEWLINE);

            case TOP_CENTER, CENTER, BOTTOM_CENTER -> {
                final int leftFill = fillSpace / 2;
                final int rightFill = fillSpace - leftFill;
                sb.append(vLine)
                        .append(fixed)
                        .append(BLANK.repeat(Math.max(0, leftFill)))
                        .append(ss).append(RESET)
                        .append(BLANK.repeat(Math.max(0, rightFill)))
                        .append(fixed)
                        .append(vLine).append(NEWLINE);
            }
        }
    }

    public static BorderChars colorBorderChars(StyleBuilder sb, BorderChars borderChar, AnsiCode[] borderColor) {
        return borderChar.map(s -> styleAndReset(sb, s, borderColor));
    }
}
