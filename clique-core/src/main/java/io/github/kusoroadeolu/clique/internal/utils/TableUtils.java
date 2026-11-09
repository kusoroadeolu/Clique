package io.github.kusoroadeolu.clique.internal.utils;

import io.github.kusoroadeolu.clique.configuration.CellAlign;
import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;

@InternalApi(since = "3.2.0")
public final class TableUtils {
    private TableUtils() {}

    public static String pad(CellAlign align, Cell cell, int width) {
        int gap = Math.max(0, width - cell.width());
        String styled = cell.styledText();
        return switch (align) {
            case LEFT -> styled + BLANK.repeat(gap);
            case RIGHT -> BLANK.repeat(gap) + styled;
            case CENTER -> {
                int left = gap / 2;
                yield BLANK.repeat(left) + styled + BLANK.repeat(gap - left);
            }
        };
    }

    public static String handleNulls(String val, String nullReplacement) {
        return val != null ? val : nullReplacement;
    }

    public static void validateHeaders(int idx) {
        if (idx == 0) throw new IllegalArgumentException("Cannot remove a header from a table");
    }
}