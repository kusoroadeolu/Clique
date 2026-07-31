package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.util.*;

import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.parseToCell;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.*;
import static java.util.Objects.isNull;

@InternalApi(since = "3.2.0")
abstract non-sealed class AbstractTable implements Table {
    final List<WidthAwareList> columns; //This is used to track the max height in that column
    final List<WidthAwareList> rows;
    final TableConfiguration configuration;
    String cachedString = null;

     AbstractTable(TableConfiguration configuration) {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.configuration = configuration;
    }

     AbstractTable() {
        this(TableConfiguration.DEFAULT);
    }

    public Table row(Collection<String> rows) {
        return row(rows.toArray(String[]::new));
    }

    public Table row(SequencedCollection<String> rows) {
        return row(rows.toArray(String[]::new));
    }

    public Table row(String... row) {
        Objects.requireNonNull(row, "Given row cannot be null");
        //Get the header's size
        final int headerSize = this.rows.getFirst().size();
        final WidthAwareList rowList = new WidthAwareList();
        this.rows.add(rowList);

        for (int i = 0; i < headerSize; i++) {
            String cell;
            //Pad the cells with null replacements
            if (i >= row.length) cell = configuration.getNullReplacement();
            else {
                cell = row[i];
                cell = handleNulls(cell, configuration.getNullReplacement());
            }

            final Cell c = parseToCell(cell, configuration.getParser());
            rowList.add(c);
            final WidthAwareList colList = columns.get(i);
            colList.add(c);
        }
        nullCachedString();
        return this;
    }

    public Table removeRow(int index) {
        validateHeaders(index);
        validateRowIndex(index, rows);

        rows.remove(index);
        for (WidthAwareList cl : columns) {
            cl.remove(index);
        }
        nullCachedString();
        return this;
    }

    public Table removeCell(int rowIndex, int columnIndex) {
        validateHeaders(rowIndex);
        updateCell(rowIndex, columnIndex, configuration.getNullReplacement());
        return this;
    }

    public Table updateCell(int row, int col, String text) {
        validateRowIndex(row, rows);
        validateColumnIndex(col, columns);

        final WidthAwareList rl = rows.get(row);
        final WidthAwareList cl = columns.get(col);
        final Cell c = parseToCell(text, configuration.getParser());
        rl.update(col, c);
        cl.update(row, c);
        nullCachedString();
        return this;
    }

    void nullCachedString(){
        cachedString = null;
    }

    abstract void colorTableBorders();

    public boolean equals(Object object) {
        if (isNull(object) || getClass() != object.getClass()) return false;

        AbstractTable that = (AbstractTable) object;
        return columns.equals(that.columns) && rows.equals(that.rows) && Objects.equals(configuration, that.configuration);
    }

    public int hashCode() {
        return Objects.hash(columns, rows, configuration);
    }

    public String toString() {
        return "Table[" +
                "columns=" + columns +
                ", rows=" + rows +
                ", tableConfiguration=" + configuration +
                ']';
    }
}
