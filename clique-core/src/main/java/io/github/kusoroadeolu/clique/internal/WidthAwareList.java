package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.util.ArrayList;
import java.util.List;

@InternalApi(since = "3.2.0")
public class WidthAwareList {
    private final List<Cell> cells;
    private int longest;

    public WidthAwareList() {
        this(new ArrayList<>());
    }

    public WidthAwareList(List<Cell> cells) {
        this.cells = cells;
        longest = calculateLongest();
    }


    public void add(Cell c) {
        updateLongest(c);
        cells.add(c);
    }
    
    public void updateLongest(Cell c) {
        final int len = c.width();
        if (len > longest) longest = len;
    }


    //Gets the styled text from the table
    public String getStyledText(int pos) {
        return cells.get(pos).styledText();
    }

    public Cell get(int pos) {
        return cells.get(pos);
    }


    public int longest() {
        return longest;
    }

    //Get the styled text from the list
    public List<String> list() {
        return cells.stream()
                .map(Cell::styledText)
                .toList();
    }

    public List<Cell> cells() {
        return new ArrayList<>(cells);
    }

    public int size() {
        return cells.size();
    }

    private int calculateLongest() {
        return cells.stream()
                .mapToInt(Cell::width)
                .max()
                .orElse(0);
    }

    @Override
    public String toString() {
        return "values: " + cells + ", longest: " + longest;
    }
}
