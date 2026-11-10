package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.util.*;

import static java.util.Objects.isNull;

@InternalApi(since = "3.2.0")
public class TableHeaderBuilder implements PendingTable {
        private final AbstractTable table;

        public TableHeaderBuilder(Table table) {
            this.table = (AbstractTable) table;
        }

        public Table headers(String... headers) {
            return table.setHeaders(headers);
        }

        public Table headers(Collection<String> headers) {
            return headers(headers.toArray(String[]::new));
        }

        @Override
        public Table headers(SequencedCollection<String> headers) {
            return headers(headers.toArray(String[]::new));
        }

        @Override
        public Table fromColumns(SequencedMap<String, ? extends SequencedCollection<String>> columns) {
            if (isNull(columns) || columns.isEmpty())
                throw new IllegalArgumentException("Columns cannot be null or empty");

            SequencedCollection<String> headers = columns.sequencedKeySet();
            List<ArrayList<String>> colValues = columns.sequencedValues().stream().map(ArrayList::new).toList();

            int rowCount = colValues.stream().mapToInt(List::size).max().orElse(0);
            List<SequencedCollection<String>> transposedRows = new ArrayList<>();
            for (int r = 0; r < rowCount; r++) {
                List<String> row = new ArrayList<>();
                for (List<String> col : colValues) row.add(r < col.size() ? col.get(r)
                        : table.configuration.getNullReplacement());

                transposedRows.add(row);
            }

            headers(headers);
            transposedRows.forEach(table::row);
            return table;
        }

        @Override
        public Table fromRows(SequencedCollection<? extends SequencedCollection<String>> rows) {

            if (isNull(rows) || rows.isEmpty())
                throw new IllegalArgumentException("Rows cannot be null or empty");

            List<? extends SequencedCollection<String>> list = rows.stream().toList();
            headers(list.getFirst());

            for (int i = 1; i < list.size(); ++i) table.row(list.get(i));

            return table;
        }
}