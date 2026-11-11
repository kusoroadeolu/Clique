package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable configuration for a {@link io.github.kusoroadeolu.clique.components.Tree}.
 *
 * <p>Controls two aspects of tree rendering:
 * <ul>
 *   <li><b>Connector color</b> — the ANSI color applied to the connector characters
 *       ({@code ├─}, {@code └─}, {@code │}) that link nodes in the rendered output.
 *       Node labels are not affected.</li>
 *   <li><b>Parser</b> — the {@link MarkupParser} used to resolve inline markup tags
 * </ul>
 *
 * <p>The {@link #DEFAULT} constant provides a pre-built configuration with no connector
 * color and {@link MarkupParser#DEFAULT} as the parser.
 *
 * <p>This class is immutable and thread-safe. {@link TreeConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * TreeConfiguration config = TreeConfiguration.builder()
 *     .connectorColor("[cyan]")
 *     .parser(MarkupParser.NONE)
 *     .build();
 *
 * Tree tree = new Tree("root", config);
 * }</pre>
 *
 * @since 3.1.0
 */
@Stable(since = "3.2.0")
public final class TreeConfiguration {

    /**
     * A default {@code TreeConfiguration} with no connector color and
     * {@link MarkupParser#DEFAULT} as the parser.
     */
    public static final TreeConfiguration DEFAULT = new TreeConfiguration();

    private final MarkupParser parser;
    private final AnsiCode[] connectorColor;

    private TreeConfiguration() {
        this(new TreeConfigurationBuilder());
    }

    private TreeConfiguration(TreeConfigurationBuilder builder) {
        this.parser = builder.parser;
        this.connectorColor = builder.connectorColor;
    }

    /**
     * Returns a new builder for constructing a {@code TreeConfiguration}.
     *
     * @return a new {@link TreeConfigurationBuilder}
     */
    public static TreeConfigurationBuilder builder() {
        return new TreeConfigurationBuilder();
    }

    /**
     * Returns the {@link MarkupParser} used to resolve markup tags in node labels
     * at render time.
     *
     * @return the parser; never {@code null}
     */
    public MarkupParser getParser() {
        return parser;
    }

    /**
     * Returns a copy of the ANSI codes applied to tree connector characters
     *
     * <p>Returns an empty array if no connector color has been set. A defensive copy
     * is returned on each call.
     *
     * @return a copy of the connector color codes; never {@code null}, may be empty
     */
    public AnsiCode[] getConnectorColor() {
        return connectorColor.clone();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        TreeConfiguration that = (TreeConfiguration) object;
        return Objects.equals(parser, that.parser) && Arrays.equals(connectorColor, that.connectorColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parser, Arrays.hashCode(connectorColor));
    }

    @Override
    public String toString() {
        return "TreeConfiguration[" +
                "parser=" + parser +
                ", connectorColor='" + Arrays.toString(connectorColor) + '\'' +
                ']';
    }

    /**
     * Builder for {@link TreeConfiguration}.
     *
     * <p>Default values match those of {@link TreeConfiguration#DEFAULT}. Methods may
     * be called in any order; each returns {@code this} for chaining.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class TreeConfigurationBuilder {
        private AnsiCode[] connectorColor = {};
        private MarkupParser parser = MarkupParser.DEFAULT;

        /**
         * Sets the {@link MarkupParser} used to resolve markup tags in node labels.
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public TreeConfigurationBuilder parser(MarkupParser parser) {
            Objects.requireNonNull(parser, "Parser cannot be null");
            this.parser = parser;
            return this;
        }

        /**
         * Sets the ANSI color applied to tree connector characters ({@code ├─},
         * {@code └─}, {@code │}) during rendering. Node labels are not affected.
         *
         * @param connectorColor one or more ANSI codes to apply to connectors;
         *                       must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code connectorColor} is {@code null}
         */
        public TreeConfigurationBuilder connectorColor(AnsiCode... connectorColor) {
            Objects.requireNonNull(connectorColor, "Connector color cannot be null");
            this.connectorColor = connectorColor;
            return this;
        }

        /**
         * Sets the connector color by parsing a markup string using the currently
         * configured parser.
         *
         * <p>The markup string is resolved against the parser set via
         * {@link #parser(MarkupParser)} at the time this method is called.
         *
         * <p>Equivalent to {@code connectorColor(ParserUtils.getAnsiCodes(connectorColor, parser))}.
         *
         * @param connectorColor a markup string representing the desired connector color;
         *                       must not be {@code null}
         * @return this builder
         */
        public TreeConfigurationBuilder connectorColor(String connectorColor) {
            return connectorColor(ParserUtils.getAnsiCodes(connectorColor, parser));
        }

        /**
         * Constructs a new {@link TreeConfiguration} from the current builder state.
         *
         * @return a new, immutable {@code TreeConfiguration}
         */
        public TreeConfiguration build() {
            return new TreeConfiguration(this);
        }
    }
}