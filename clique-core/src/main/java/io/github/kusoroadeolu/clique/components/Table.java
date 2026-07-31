package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.util.Collection;
import java.util.SequencedCollection;

/**
 * A component for displaying structured, multi-column data in the terminal.
 *
 * <p>Tables support multiple pre-defined styles (e.g., ASCII, Box-Draw) and
 * dynamic content manipulation. This component does not support multi-line
 * cell content; strings containing newlines may result in malformed output.
 *
 * <p><b>Markup Handling:</b> Content added to cells is automatically
 * processed via the configured {@code MarkupParser}.
 *
 * <p><b>Thread Safety:</b> Implementations of this interface are <b>not thread-safe</b>.
 * External synchronization is required if multiple threads access or modify
 * the table structure concurrently.
 *
 * @since 1.0.0
 */
@Stable(since = "3.0.0")
public sealed interface Table extends Component permits AbstractTable{

    /**
     * Appends a new row to the end of the table.
     *
     * @param row the cell contents for the new row
     * @return this table instance for method chaining
     * @throws NullPointerException if the varargs array or any element is {@code null}
     */
    Table row(String... row);



    /**
     * @deprecated in favor of the {@link SequencedCollection} overload
     * */
    @Deprecated(since = "4.0.3")
    Table row(Collection<String> row);

    /**
     * Appends a new row to the end of the table using the provided sequenced collection.
     *
     * @param row a collection of strings representing the cells of the row;
     * must not be {@code null}
     * @return this table instance for method chaining
     * @throws NullPointerException if {@code rows} or any element is {@code null}
     */
    Table row(SequencedCollection<String> row);

    /**
     * Removes the row at the specified index.
     *
     * <p><b>Scoping:</b> Indices are 0-based. The header row (index 0)
     * cannot be removed via this method.
     *
     * @param index the 0-based index of the row to remove
     * @return this table instance for method chaining
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    Table removeRow(int index);

    /**
     * Removes the content of a specific cell and replaces it with the
     * configured {@code nullReplacement} value.
     *
     * @param row the 0-based row index
     * @param col the 0-based column index
     * @return this table instance for method chaining
     * @throws IndexOutOfBoundsException if row or column indices are out of range
     */
    Table removeCell(int row, int col);

    /**
     * Updates the content of a specific cell.
     *
     * <p>The provided {@code text} is immediately eligible for markup parsing
     * during the next render cycle.
     *
     * @param row the 0-based row index
     * @param col the 0-based column index
     * @param text the new content for the cell; must not be {@code null}
     * @return this table instance for method chaining
     * @throws NullPointerException if {@code text} is {@code null}
     * @throws IndexOutOfBoundsException if row or column indices are out of range
     */
    Table updateCell(int row, int col, String text);
}