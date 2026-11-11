package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.utils.CharWidth;

import java.util.Objects;

@InternalApi(since = "3.2.0")
public final class Cell {
    private final String text;
    private final String styledText;
    private final int width;

    public Cell(String text, String styledText) {
        this.text = text;
        this.styledText = styledText;
        this.width = CharWidth.of(text);
    }

    public String text() {
        return text;
    }

    public String styledText() {
        return styledText;
    }

    public int width() {
        return width;
    }

    public boolean isEmpty() {
        return width() == 0;
    }

    @Override
    public String toString() {
        return "Cell[" +
                "styledText='" + styledText + '\'' +
                ", text='" + text + '\'' +
                ", width=" + width +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;
        return width == cell.width && Objects.equals(text, cell.text) && Objects.equals(styledText, cell.styledText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, text, styledText);
    }
}