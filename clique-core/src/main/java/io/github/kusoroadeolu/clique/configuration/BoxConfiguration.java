package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable configuration for a {@link io.github.kusoroadeolu.clique.components.Box}.
 *
 * <p>Instances are obtained via {@link #builder()}. The {@link #DEFAULT} constant provides
 * a pre-built configuration with the following values:
 * <ul>
 *   <li>padding: {@code 2}</li>
 *   <li>textAlign: {@link TextAlign#CENTER}</li>
 *   <li>parser: {@link MarkupParser#DEFAULT}</li>
 *   <li>borderColor: empty (no color applied)</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe. {@link BoxConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * BoxConfiguration config = BoxConfiguration.builder()
 *     .padding(3)
 *     .textAlign(TextAlign.CENTER)
 *     .borderColor("blue")
 *     .build();
 * }</pre>
 *
 * @since 1.1.0
 */
@Stable(since = "3.2.0")
public final class BoxConfiguration {

    /**
     * A default {@code BoxConfiguration} with padding {@code 2}, center alignment,
     * the default markup parser, and no border color.
     */
    public static final BoxConfiguration DEFAULT = new BoxConfiguration();

    private final TextAlign textAlign;
    private final MarkupParser parser;
    private final AnsiCode[] borderColor;
    private final int padding;

    private BoxConfiguration() {
        this(new BoxConfigurationBuilder());
    }

    private BoxConfiguration(BoxConfigurationBuilder builder) {
        this.textAlign = builder.textAlign;
        this.parser = builder.parser;
        this.borderColor = builder.borderColor;
        this.padding = builder.padding;
    }

    /**
     * Returns a new builder for constructing a {@code BoxConfiguration}.
     *
     * @return a new {@link BoxConfigurationBuilder}
     */
    public static BoxConfigurationBuilder builder() {
        return new BoxConfigurationBuilder();
    }

    /**
     * Returns the number of blank characters inserted between the box border and
     * its content on each side.
     *
     * <p>Padding is deducted from the box's configured width, not added on top of it.
     *
     * @return the padding value; always {@code >= 0}
     */
    public int getPadding() {
        return this.padding;
    }

    /**
     * Returns the ANSI codes applied to the box border, or an empty array if no
     * border color has been set.
     *
     * @return a defensive copy of the border color codes; never {@code null}, may be empty
     */
    public AnsiCode[] getBorderColor() {
        return this.borderColor.clone();
    }

    /**
     * Returns the horizontal alignment applied to content within the box.
     *
     * @return the text alignment; never {@code null}
     */
    public TextAlign getTextAlign() {
        return this.textAlign;
    }

    /**
     * Returns the markup parser used to interpret inline style tags in box content
     * and border color specifications.
     *
     * @return the parser; never {@code null}
     */
    public MarkupParser getParser() {
        return this.parser;
    }

    @Override
    public String toString() {
        return "BoxConfiguration[" +
                "textAlign=" + textAlign +
                ", parser=" + parser +
                ", borderColor=" + Arrays.toString(borderColor) +
                ", padding=" + padding +
                ']';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        BoxConfiguration that = (BoxConfiguration) object;
        return textAlign == that.textAlign && parser.equals(that.parser) && padding == that.padding && Arrays.equals(borderColor, that.borderColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, textAlign, parser, Arrays.hashCode(borderColor));
    }

    /**
     * Builder for {@link BoxConfiguration}.
     *
     * <p>Default values match those of {@link BoxConfiguration#DEFAULT}. Methods
     * may be called in any order; each returns {@code this} for chaining.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class BoxConfigurationBuilder {
        private TextAlign textAlign = TextAlign.CENTER;
        private MarkupParser parser = MarkupParser.DEFAULT;
        private AnsiCode[] borderColor = {};
        private int padding = 2;

        private BoxConfigurationBuilder(){}

        /**
         * Sets the padding applied between the box border and its content.
         *
         * <p>Padding is deducted from the box's configured width, not added on top of it.
         *
         * @param padding the number of blank characters on each side; must be {@code >= 0}
         * @return this builder
         * @throws IllegalArgumentException if {@code padding} is negative
         */
        public BoxConfigurationBuilder padding(int padding) {
            if (padding < 0) throw new IllegalArgumentException("Padding cannot be negative");
            this.padding = padding;
            return this;
        }

        /**
         * Sets the border color by parsing a markup string using the currently
         * configured parser.
         *
         * <p>The markup string is resolved against the parser set via
         * {@link #parser(MarkupParser)} at the time this method is called.
         *
         * @param borderColor a markup string representing the desired border color;
         *                    must not be {@code null}
         * @return this builder
         */
        public BoxConfigurationBuilder borderColor(String borderColor) {
            return borderColor(ParserUtils.getAnsiCodes(borderColor, parser));
        }

        /**
         * Sets the border color directly from one or more {@link AnsiCode} instances.
         *
         * @param borderColor the ANSI codes to apply to the border; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code borderColor} is {@code null}
         */
        public BoxConfigurationBuilder borderColor(AnsiCode... borderColor) {
            this.borderColor = Objects.requireNonNull(borderColor, "Border color cannot be null");
            return this;
        }

        /**
         * Sets the horizontal alignment for content within the box.
         *
         * @param textAlign the alignment to apply; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code textAlign} is {@code null}
         */
        public BoxConfigurationBuilder textAlign(TextAlign textAlign) {
            this.textAlign = Objects.requireNonNull(textAlign, "Text align cannot be null");
            return this;
        }

        /**
         * Sets the markup parser used to interpret inline style tags in box content
         * and border color markup strings.
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public BoxConfigurationBuilder parser(MarkupParser parser) {
            this.parser = Objects.requireNonNull(parser, "Markup parser cannot be null");
            return this;
        }

        /**
         * Constructs a new {@link BoxConfiguration} from the current builder state.
         *
         * @return a new, immutable {@code BoxConfiguration}
         */
        public BoxConfiguration build() {
            return new BoxConfiguration(this);
        }
    }
}