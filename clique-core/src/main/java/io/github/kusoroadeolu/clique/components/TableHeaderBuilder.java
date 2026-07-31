package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.util.*;

import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.parseToCell;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.handleNulls;
import static java.util.Objects.isNull;

@InternalApi(since = "3.2.0")
public class TableHeaderBuilder implements PendingTable {
        private final AbstractTable table;

        public TableHeaderBuilder(Table table) {
            this.table = (AbstractTable) table;
        }

        public Table headers(String... headers) {
            if (isNull(headers) || headers.length == 0)
                throw new IllegalArgumentException("Headers cannot be null or empty");

            final var rows = new WidthAwareList();
            var config = table.configuration;
            table.rows.add(rows);

            for (int i = 0; i < headers.length; i++) {
                String header = handleNulls(headers[i], config.getNullReplacement());
                final var cell = parseToCell(header, config.getParser());
                rows.add(cell);
                final var columns = new WidthAwareList(); //To keep track of all values in this column
                columns.add(cell);

                table.columns.add(i, columns);
            }

            return table;
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

            Table table = headers(headers);
            transposedRows.forEach(table::row);
            return table;
        }

        @Override
        public Table fromRows(SequencedCollection<? extends SequencedCollection<String>> rows) {

            if (isNull(rows) || rows.isEmpty())
                throw new IllegalArgumentException("Rows cannot be null or empty");

            List<? extends SequencedCollection<String>> list = rows.stream().toList();
            Table table = headers(list.getFirst());

            for (int i = 1; i < list.size(); ++i) table.row(list.get(i));

            return table;
        }
}