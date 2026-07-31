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
            table.rows.add(rows);

            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                header = handleNulls(header, table.configuration.getNullReplacement());
                final var cell = parseToCell(header, table.configuration.getParser());
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
            if (isNull(columns) || columns.isEmpty()) {
                throw new IllegalArgumentException("Columns cannot be null or empty");
            }

            SequencedSet<String> headers = columns.sequencedKeySet();
            Table table = headers(headers);

            for (SequencedCollection<String> row : columns.sequencedValues()) {
                table.row(row);
            }

            return table;
        }
}