package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Objects;

/**
 * An immutable configuration for a {@link io.github.kusoroadeolu.clique.parser.MarkupParser}.
 *
 * <p>Instances are obtained via {@link #builder()}. The {@link #DEFAULT} constant provides
 * a pre-built configuration with the following values:
 * <ul>
 *   <li>delimiter: {@code ','}</li>
 *   <li>strictParsing: {@code false}</li>
 *   <li>autoReset: {@code false}</li>
 *   <li>styleContext: {@link StyleContext#NONE}</li>
 * </ul>
 *
 * <p><b>Style resolution order.</b> When the parser encounters a markup tag, styles are
 * resolved in the following order, with earlier entries taking precedence:
 * <ol>
 *   <li>Local styles — registered via {@link ParserConfigurationBuilder#styleContext(StyleContext)}
 *       or {@link ParserConfigurationBuilder#addStyle(String, AnsiCode)} on this configuration.</li>
 *   <li>Global custom styles — registered via {@code Clique.registerStyle()}.</li>
 *   <li>Predefined styles — built-in colors, backgrounds, and text styles.</li>
 * </ol>
 *
 * <p>This class is immutable and thread-safe. {@link ParserConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * ParserConfiguration config = ParserConfiguration.builder()
 *     .enableAutoReset()
 *     .delimiter(' ')
 *     .addStyle("highlight", ColorCode.YELLOW)
 *     .build();
 * }</pre>
 *
 * @since 1.0.0
 */
@Stable(since = "3.2.0")
public final class ParserConfiguration {

    /**
     * A default {@code ParserConfiguration} with delimiter {@code ','}, strict parsing
     * and auto-reset disabled, and no local style context.
     */
    public static final ParserConfiguration DEFAULT = new ParserConfiguration();

    private final String delimiter;
    private final boolean enableStrictParsing;
    private final boolean enableAutoReset;
    private final StyleContext styleContext;

    private ParserConfiguration() {
        this(new ParserConfigurationBuilder());
    }

    private ParserConfiguration(ParserConfigurationBuilder builder) {
        this.delimiter = builder.delimiter;
        this.enableStrictParsing = builder.enableStrictParsing;
        this.enableAutoReset = builder.enableAutoReset;
        this.styleContext = builder.context;
    }

    /**
     * Returns a new builder for constructing a {@code ParserConfiguration}.
     *
     * @return a new {@link ParserConfigurationBuilder}
     */
    public static ParserConfigurationBuilder builder() {
        return new ParserConfigurationBuilder();
    }

    /**
     * Returns whether strict parsing is enabled.
     *
     * <p>When {@code true}, the parser throws
     * {@code io.github.kusoroadeolu.clique.internal.exception.UnidentifiedStyleException}
     * for unrecognized style names on otherwise valid tags. Structurally malformed tags
     * are always passed through as-is regardless of this setting.
     *
     * @return {@code true} if strict parsing is enabled; {@code false} otherwise
     */
    public boolean getEnableStrictParsing() {
        return enableStrictParsing;
    }

    /**
     * Returns whether auto-reset is enabled.
     *
     * <p>When {@code true}, an ANSI reset sequence is automatically injected when a new
     * tag is opened, preventing styles from leaking into subsequent tags.
     *
     * @return {@code true} if auto-reset is enabled; {@code false} otherwise
     */
    public boolean getEnableAutoReset() {
        return enableAutoReset;
    }

    /**
     * Returns the delimiter used to separate style names within a markup tag.
     *
     * <p>Defaults to {@code ","} (e.g. {@code [red, bold]}). A space delimiter would
     * allow {@code [red bold]} syntax instead.
     *
     * @return the delimiter string; never {@code null}
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Returns the local {@link StyleContext} for this configuration.
     *
     * <p>Styles in this context are resolved before global custom styles and predefined
     * styles. Returns {@link StyleContext#NONE} if no local styles were registered.
     *
     * @return the style context; never {@code null}
     */
    public StyleContext getStyleContext() {
        return styleContext;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ParserConfiguration that = (ParserConfiguration) o;
        return enableStrictParsing == that.enableStrictParsing && enableAutoReset == that.enableAutoReset && Objects.equals(delimiter, that.delimiter) && Objects.equals(styleContext, that.styleContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enableAutoReset, enableStrictParsing, delimiter, styleContext);
    }

    @Override
    public String toString() {
        return "ParserConfiguration[" +
                "delimiter='" + delimiter + '\'' +
                ", enableStrictParsing=" + enableStrictParsing +
                ", enableAutoReset=" + enableAutoReset +
                ']';
    }

    /**
     * Builder for {@link ParserConfiguration}.
     *
     * <p>Default values match those of {@link ParserConfiguration#DEFAULT}. Methods may
     * be called in any order; each returns {@code this} for chaining. Style registrations
     * via {@link #addStyle(String, AnsiCode)} and {@link #styleContext(StyleContext)} are
     * cumulative — both can be used together and their entries are merged into a single
     * {@link StyleContext} at {@link #build()} time.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class ParserConfigurationBuilder {
        private String delimiter = Character.toString(',');
        private boolean enableStrictParsing = false;
        private boolean enableAutoReset = false;
        private final StyleContext.StyleContextBuilder styleContextBuilder = StyleContext.builder();
        private StyleContext context = StyleContext.NONE;

        /**
         * Enables auto-reset, causing an ANSI reset sequence to be injected automatically
         * when a new tag is encountered, preventing style bleed between tags.
         *
         * @return this builder
         */
        public ParserConfigurationBuilder enableAutoReset() {
            this.enableAutoReset = true;
            return this;
        }

        /**
         * Enables strict parsing, causing the parser to throw
         * {@code UnidentifiedStyleException} when a valid tag contains an unrecognized
         * style name.
         *
         * <p>Without this option, unrecognized styles are silently ignored and the tag
         * is passed through as-is. Structurally malformed tags (e.g. unclosed brackets,
         * nested tags) are always passed through regardless of this setting.
         *
         * @return this builder
         */
        public ParserConfigurationBuilder enableStrictParsing() {
            this.enableStrictParsing = true;
            return this;
        }

        /**
         * Sets the delimiter used to separate style names within a markup tag.
         *
         * <p>Defaults to {@code ','}. For example, setting {@code ' '} allows
         * {@code [red bold]} syntax in place of {@code [red, bold]}.
         *
         * @param delimiter the character to use as a style delimiter
         * @return this builder
         */
        public ParserConfigurationBuilder delimiter(char delimiter) {
            this.delimiter = Character.toString(delimiter);
            return this;
        }

        /**
         * Registers a single custom style in the local {@link StyleContext}.
         *
         * <p>Local styles take precedence over global custom styles and predefined styles.
         * Multiple calls accumulate; styles registered here are merged with any
         * {@link StyleContext} added via {@link #styleContext(StyleContext)}.
         *
         * @param markup the markup name to register (e.g. {@code "highlight"})
         * @param code   the {@link AnsiCode} to associate with the markup name
         * @return this builder
         */
        public ParserConfigurationBuilder addStyle(String markup, AnsiCode code) {
            styleContextBuilder.add(markup, code);
            return this;
        }

        /**
         * Merges all styles from the given {@link StyleContext} into the local style
         * context for this configuration.
         *
         * <p>Local styles take precedence over global custom styles and predefined styles.
         * Multiple calls accumulate; styles from each context are merged with any styles
         * added via {@link #addStyle(String, AnsiCode)}.
         *
         * @param styleContext the context whose styles to add; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code styleContext} is {@code null}
         */
        public ParserConfigurationBuilder styleContext(StyleContext styleContext) {
            this.styleContextBuilder.add(styleContext);
            return this;
        }
        /**
         * Constructs a new {@link ParserConfiguration} from the current builder state.
         *
         * <p>All styles registered via {@link #addStyle(String, AnsiCode)} and
         * {@link #styleContext(StyleContext)} are merged into a single {@link StyleContext}
         * at this point.
         *
         * @return a new, immutable {@code ParserConfiguration}
         */
        public ParserConfiguration build() {
            this.context = styleContextBuilder.build();
            return new ParserConfiguration(this);
        }
    }
}