package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.parser.MarkupParser;

import java.util.Objects;

/**
 * An immutable configuration object that defines the structure and style of an {@code ItemList}.
 *
 * <p>This class provides settings for indentation, spacing between symbols and content,
 * and the parser used for markup resolution. Default values are:
 * <ul>
 * <li>Indent Size: 2</li>
 * <li>Symbol Spacing: 1</li>
 * <li>Parser: {@link MarkupParser#DEFAULT}</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b> This class is immutable and thread-safe.
 *
 * <p>Example:
 * <pre>{@code
 * ItemListConfiguration config = ItemListConfiguration.builder()
 *     .indentSize(1)
 *     .symbolSpacing(2)
 *     .parser(Clique.parser())
 *     .build();
 * }</pre>
 *
 * @since 4.0.0
 */
@Stable(since = "4.0.0")
public class ItemListConfiguration {
    /**
     * The default item list configuration.
     */
    public static final ItemListConfiguration DEFAULT = new ItemListConfiguration();

    private final MarkupParser parser;
    private final int indentSize;
    private final int symbolSpacing;

    private ItemListConfiguration() {
        this(new ItemListConfigurationBuilder());
    }

    private ItemListConfiguration(ItemListConfigurationBuilder builder) {
        this.indentSize = builder.indentSize;
        this.parser = builder.parser;
        this.symbolSpacing = builder.symbolSpacing;
    }

    /**
     * Creates a new builder for constructing an {@code ItemListConfiguration}.
     *
     * @return a new builder instance
     */
    public static ItemListConfigurationBuilder builder() {
        return new ItemListConfigurationBuilder();
    }

    /**
     * Returns the markup parser used to resolve tags in list content.
     *
     * @return the markup parser
     */
    public MarkupParser getParser() {
        return parser;
    }

    /**
     * Returns the number of spaces used for each level of nesting.
     *
     * @return the indentation size
     */
    public int getIndentSize() {
        return indentSize;
    }

    /**
     * Returns the number of spaces between the item symbol and the item content.
     *
     * @return the symbol spacing
     */
    public int getSymbolSpacing() {
        return symbolSpacing;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ItemListConfiguration that = (ItemListConfiguration) o;
        return indentSize == that.indentSize && symbolSpacing == that.symbolSpacing && Objects.equals(parser, that.parser);
    }

    @Override
    public int hashCode() {
       return Objects.hash(indentSize, symbolSpacing, parser);
    }

    @Override
    public String toString() {
        return "ItemListConfiguration[" +
                "parser=" + parser +
                ", indentSize=" + indentSize +
                ", symbolSpacing=" + symbolSpacing +
                ']';
    }

    /**
     * Builder for {@link ItemListConfiguration} instances.
     *
     * <p>This builder is <b>not thread-safe</b>.
     */
    public static class ItemListConfigurationBuilder {
        private int indentSize = 2;
        private MarkupParser parser = MarkupParser.DEFAULT;
        private int symbolSpacing = 1;

        private ItemListConfigurationBuilder(){}

        /**
         * Sets the number of spaces for each nesting level.
         *
         * @param indentSize the indentation size; must be at least 1
         * @return this builder
         * @throws IllegalArgumentException if {@code indentSize < 1}
         */
        public ItemListConfigurationBuilder indentSize(int indentSize) {
            if (indentSize < 1) throw new IllegalArgumentException("Indent size cannot be less than 1");
            this.indentSize = indentSize;
            return this;
        }

        /**
         * Sets the parser for markup resolution.
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public ItemListConfigurationBuilder parser(MarkupParser parser) {
            Objects.requireNonNull(parser, "Parser cannot be null");
            this.parser = parser;
            return this;
        }

        /**
         * Sets the spacing between the item symbol and content.
         *
         * @param spacing the number of spaces; must not be negative
         * @return this builder
         * @throws IllegalArgumentException if {@code spacing < 0}
         */
        public ItemListConfigurationBuilder symbolSpacing(int spacing) {
            if (spacing < 0) throw new IllegalArgumentException("Symbol spacing cannot be negative");
            this.symbolSpacing = spacing;
            return this;
        }

        /**
         * Builds a new {@link ItemListConfiguration} based on current settings.
         *
         * @return a new configuration instance
         */
        public ItemListConfiguration build() {
            return new ItemListConfiguration(this);
        }
    }
}