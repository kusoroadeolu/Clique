package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.CellAlign;
import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.*;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.NEWLINE;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.parseToCell;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.*;
import static java.util.Objects.isNull;

@InternalApi(since = "3.2.0")
abstract non-sealed class AbstractTable implements Table {
    final List<List<Cell>> rows = new ArrayList<>();
    final TableConfiguration configuration;

    AbstractTable(TableConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "Table configuration cannot be null");
    }

    AbstractTable() {
        this(TableConfiguration.DEFAULT);
    }


    abstract String rowEdge();
    abstract String cellSeparator();
    abstract String headerSeparator(int[] widths);

    String top(int[] widths) {
        return null;
    }
    String rowSeparator(int[] widths) {
        return null;
    }
    String bottom(int[] widths) {
        return null;
    }
    int cellPadding() {
        return configuration.getPadding();
    }


    Table setHeaders(String[] headers) {
        if (headers == null || headers.length == 0)
            throw new IllegalArgumentException("Headers cannot be null or empty");
        if (!rows.isEmpty())
            throw new IllegalStateException("Headers have already been set");
        rows.add(toCells(headers, headers.length));
        return this;
    }

    public Table row(String... row) {
        Objects.requireNonNull(row, "Given row cannot be null");
        if (rows.isEmpty()) throw new IllegalStateException("Headers must be set before adding rows");
        rows.add(toCells(row, rows.getFirst().size()));
        return this;
    }

    public Table row(Collection<String> row) {
        return row(row.toArray(String[]::new));
    }

    public Table row(SequencedCollection<String> row) {
        return row(row.toArray(String[]::new));
    }

    public Table removeRow(int rowIndex) {
        validateHeaders(rowIndex);
        Objects.checkIndex(rowIndex, rows.size());
        rows.remove(rowIndex);
        return this;
    }

    public Table removeCell(int rowIndex, int columnIndex) {
        validateHeaders(rowIndex);
        return updateCell(rowIndex, columnIndex, configuration.getNullReplacement());
    }

    public Table updateCell(int rowIndex, int columnIndex, String text) {
        Objects.requireNonNull(text, "Cell text cannot be null");
        Objects.checkIndex(rowIndex, rows.size());
        Objects.checkIndex(columnIndex, rows.getFirst().size());
        rows.get(rowIndex).set(columnIndex, parseToCell(text, configuration.getParser()));
        return this;
    }

    private List<Cell> toCells(String[] values, int size) {
        var cells = new ArrayList<Cell>(size);
        for (int i = 0; i < size; i++) {
            String raw = i < values.length ? values[i] : null;
            cells.add(parseToCell(handleNulls(raw, configuration.getNullReplacement()), configuration.getParser()));
        }
        return cells;
    }


    @Override
    public String get() {
        int[] widths = columnWidths();
        var colAligns = configuration.getColumnAlignment();
        var lines = new ArrayList<String>();

        addLine(lines, top(widths));
        for (int i = 0; i < rows.size(); i++) {
            lines.add(renderRow(rows.get(i), widths, colAligns));
            addLine(lines, i == 0 ? headerSeparator(widths) : rowSeparator(widths));
        }
        addLine(lines, bottom(widths));
        return String.join(NEWLINE, lines);
    }

    private String renderRow(List<Cell> row, int[] widths, Map<Integer, CellAlign> colAligns) {
        var sb = new StringBuilder(rowEdge());
        for (int i = 0; i < widths.length; i++) {
            if (i > 0) sb.append(cellSeparator());
            CellAlign align = colAligns.getOrDefault(i, configuration.getAlignment());
            sb.append(pad(align, row.get(i), widths[i] + cellPadding()));
        }
        return sb.append(rowEdge()).toString();
    }

    private int[] columnWidths() {
        int[] widths = new int[rows.getFirst().size()];
        for (var row : rows)
            for (int i = 0; i < widths.length; i++)
                widths[i] = Math.max(widths[i], row.get(i).width());
        return widths;
    }

    private static void addLine(List<String> lines, String line) {
        if (line != null) lines.add(line);
    }


    final String line(int[] widths, String left, String fill, String join, String right) {
        var sb = new StringBuilder(border(left));
        for (int i = 0; i < widths.length; i++) {
            if (i > 0) sb.append(border(join));
            sb.append(border(fill.repeat(widths[i] + cellPadding())));
        }
        return sb.append(border(right)).toString();
    }

    final String border(String s) {
        AnsiCode[] color = configuration.getBorderColor();
        return color.length == 0 ? s : new StyleBuilder().appendAndReset(s, color).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AbstractTable) o;
        return rows.equals(that.rows) && configuration.equals(that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, configuration);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[rows=" + rows + ", configuration=" + configuration + "]";
    }
}
