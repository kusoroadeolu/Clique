package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.util.Collection;
import java.util.SequencedCollection;
import java.util.SequencedMap;


/**
 * @since 4.0.0
 * */
@Stable(since = "4.0.0")
public interface PendingTable {

    /**
     * Sets the table headers.
     *
     * @param headers the header labels, in display order
     * @return the initialized {@link Table}
     * */
    Table headers(String... headers);

    /**
     * Sets the table headers.
     *
     * @param headers the header labels
     * @return the initialized {@link Table}
     * @deprecated in favor of the {@link SequencedCollection} overload
     * */
    @Deprecated(since = "4.0.3")
    Table headers(Collection<String> headers);

    /**
     * Sets the table headers.
     *
     * @param headers the header labels, in display order
     * @return the initialized {@link Table}
     * */
    Table headers(SequencedCollection<String> headers);

    /**
     * Initializes the table from a column-oriented data source, where each entry maps
     * a header to the values in that column, top to bottom.
     * <p>
     * Columns of differing lengths are padded to the longest column.
     *
     * @param columns a map of header to column values, in display order
     * @return the initialized {@link Table}
     * @throws IllegalArgumentException if {@code columns} is null or empty
     * */
    Table fromColumns(SequencedMap<String, ? extends SequencedCollection<String>> columns);

    /**
     * Initializes the table from a row-oriented data source. The first row is used as
     * the table headers; all subsequent rows are added as data rows.
     *
     * @param rows the rows, with the first row treated as headers
     * @return the initialized {@link Table}
     * @throws IllegalArgumentException if {@code rows} is null or empty
     * */
    Table fromRows(SequencedCollection<? extends SequencedCollection<String>> rows);
}