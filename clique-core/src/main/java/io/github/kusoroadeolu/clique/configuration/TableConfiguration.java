package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable configuration object that defines the visual and functional behavior of a {@code Table}.
 *
 * <p>This class uses a builder pattern for construction. Default values are:
 * <ul>
 * <li>Padding: 1</li>
 * <li>Alignment: {@link CellAlign#LEFT}</li>
 * <li>Parser: {@link MarkupParser#DEFAULT}</li>
 * <li>Null Replacement: "" (empty string)</li>
 * <li>Border Color: none (empty array)</li>
 * </ul>
 *
 * <p><b>Resolution Order:</b> When determining cell alignment, configurations defined via
 * {@link TableConfigurationBuilder#columnAlignment(int, CellAlign)} take precedence over
 * the global {@link TableConfigurationBuilder#alignment(CellAlign)} setting.
 *
 * <p><b>Thread Safety:</b> This class is immutable and thread-safe.
 *
 * @since 1.0.0
 */
@Stable(since = "3.2.0")
public final class TableConfiguration {
    /**
     * The default table configuration.
     */
    public static final TableConfiguration DEFAULT = new TableConfiguration();

    private final int padding;
    private final CellAlign alignment;
    private final MarkupParser parser;
    private final String nullReplacement;
    private final Map<Integer, CellAlign> columnAlignment;
    private final AnsiCode[] borderColor;

    private TableConfiguration() {
        this(new TableConfigurationBuilder());
    }

    private TableConfiguration(TableConfigurationBuilder builder) {
        this.padding = builder.padding;
        this.alignment = builder.alignment;
        this.parser = builder.parser;
        this.nullReplacement = builder.nullReplacement;
        this.columnAlignment = Map.copyOf(builder.columnAlignment);
        this.borderColor = builder.borderColor;
    }

    /**
     * Creates a new builder for constructing a {@code TableConfiguration}.
     *
     * @return a new builder instance
     */
    public static TableConfigurationBuilder builder() {
        return new TableConfigurationBuilder();
    }

    /**
     * Returns the horizontal padding applied to cell content.
     *
     * @return the number of spaces used for padding
     */
    public int getPadding() {
        return this.padding;
    }

    /**
     * Returns the global alignment setting for table cells.
     *
     * @return the default cell alignment
     */
    public CellAlign getAlignment() {
        return this.alignment;
    }

    /**
     * Returns the parser used for resolving markup within cell content.
     *
     * @return the markup parser
     */
    public MarkupParser getParser() {
        return this.parser;
    }

    /**
     * Returns the string used to replace null cell values.
     *
     * @return the null replacement string
     */
    public String getNullReplacement() {
        return this.nullReplacement;
    }

    /**
     * Returns the ANSI codes used to style the table borders.
     *
     * @return a copy of the border color ANSI codes
     */
    public AnsiCode[] getBorderColor() {
        return this.borderColor.clone();
    }

    /**
     * Returns the map of column-specific alignments.
     *
     * @return a copy of the column alignment map
     */
    public Map<Integer, CellAlign> getColumnAlignment() {
        return new HashMap<>(this.columnAlignment);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TableConfiguration that = (TableConfiguration) object;
        return padding == that.padding && alignment == that.alignment && parser.equals(that.parser)
                && nullReplacement.equals(that.nullReplacement) && columnAlignment.equals(that.columnAlignment) && Arrays.equals(borderColor, that.borderColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, alignment, parser, nullReplacement, columnAlignment, Arrays.hashCode(borderColor));
    }

    @Override
    public String toString() {
        return "TableConfiguration[" +
                "padding=" + padding +
                ", alignment=" + alignment +
                ", parser=" + parser +
                ", nullReplacement='" + nullReplacement + '\'' +
                ", columnAlignment=" + columnAlignment +
                ", borderColor=" + Arrays.toString(borderColor) +
                ']';
    }

    /**
     * Builder for {@link TableConfiguration} instances.
     *
     * <p>This builder is <b>not thread-safe</b>.
     */
    public static class TableConfigurationBuilder {
        private int padding = 1;
        private CellAlign alignment = CellAlign.LEFT;
        private MarkupParser parser = MarkupParser.DEFAULT;
        private String nullReplacement = "";
        private final Map<Integer, CellAlign> columnAlignment = new HashMap<>();
        private AnsiCode[] borderColor = {};

        private TableConfigurationBuilder(){}


        /**
         * Sets the padding for table cells.
         *
         * @param padding the number of spaces to apply; must not be negative
         * @return this builder
         * @throws IllegalArgumentException if {@code padding < 0}
         */
        public TableConfigurationBuilder padding(int padding) {
            if (padding < 0) throw new IllegalArgumentException("Padding cannot be negative.");
            this.padding = padding;
            return this;
        }

        /**
         * Sets the default alignment for all table cells.
         *
         * @param alignment the cell alignment; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code alignment} is {@code null}
         */
        public TableConfigurationBuilder alignment(CellAlign alignment) {
            this.alignment = Objects.requireNonNull(alignment, "Cell alignment cannot be null");
            return this;
        }

        /**
         * Sets the parser for markup resolution.
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public TableConfigurationBuilder parser(MarkupParser parser) {
            this.parser = Objects.requireNonNull(parser, "Parser cannot be null");
            return this;
        }

        /**
         * Sets the replacement string for cells containing {@code null}.
         *
         * @param nullReplacement the replacement string; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code nullReplacement} is {@code null}
         */
        public TableConfigurationBuilder nullReplacement(String nullReplacement) {
            this.nullReplacement = Objects.requireNonNull(nullReplacement, "Null replacement cannot be null");
            return this;
        }

        /**
         * Sets the border color using a markup-styled string.
         *
         * @param borderColor a string containing color tags  e.g "red, bold". Note that the markup brackets should not be included
         * @return this builder
         * @throws NullPointerException if {@code borderColor} is {@code null}
         */
        public TableConfigurationBuilder borderColor(String borderColor) {
            Objects.requireNonNull(borderColor, "Border color string cannot be null");
            return borderColor(ParserUtils.getAnsiCodes(borderColor, parser));
        }

        /**
         * Sets the border color using ANSI codes.
         *
         * @param borderColor the ANSI codes to apply
         * @return this builder
         * @throws NullPointerException if {@code borderColor} is {@code null}
         */
        public TableConfigurationBuilder borderColor(AnsiCode... borderColor) {
            this.borderColor = Objects.requireNonNull(borderColor, "Border color cannot be null");
            return this;
        }

        /**
         * Sets column-specific alignments using a map of column indices to alignments.
         *
         * @param columnAlignment the mapping of column indices; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code columnAlignment} is {@code null}
         */
        public TableConfigurationBuilder columnAlignment(Map<Integer, CellAlign> columnAlignment) {
            Objects.requireNonNull(columnAlignment, "Column alignment cannot be null");
            this.columnAlignment.putAll(columnAlignment);
            return this;
        }

        /**
         * Sets the alignment for a specific column.
         *
         * <p><b>Scoping:</b> This setting overrides the global table alignment for the specified column index.
         *
         * @param column the 0-based index of the column
         * @param alignment the alignment to apply; must not be {@code null}
         * @return this builder
         * @throws IllegalArgumentException if {@code column < 0}
         * @throws NullPointerException if {@code alignment} is {@code null}
         */
        public TableConfigurationBuilder columnAlignment(int column, CellAlign alignment) {
            if (column < 0) throw new IllegalArgumentException("Column index cannot be less than 0");
            Objects.requireNonNull(alignment, "Column alignment cannot be null");
            this.columnAlignment.put(column, alignment);
            return this;
        }

        /**
         * Builds a new {@link TableConfiguration} based on current settings.
         *
         * @return a new configuration instance
         */
        public TableConfiguration build() {
            return new TableConfiguration(this);
        }
    }
}