package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable configuration for a {@link io.github.kusoroadeolu.clique.components.Frame}.
 *
 * <p>Instances are obtained via {@link #builder()}. The {@link #DEFAULT} constant provides
 * a pre-built configuration with the following values:
 * <ul>
 *   <li>padding: {@code 2}</li>
 *   <li>align: {@link FrameAlign#CENTER}</li>
 *   <li>parser: {@link MarkupParser#DEFAULT}</li>
 *   <li>borderColor: empty (no color applied)</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe. {@link FrameConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * FrameConfiguration config = FrameConfiguration.builder()
 *     .padding(3)
 *     .frameAlign(FrameAlign.LEFT)
 *     .borderColor("red")
 *     .build();
 * }</pre>
 *
 * @since 3.1.0
 */
@Stable(since = "3.2.0")
public final class FrameConfiguration {

    /**
     * A default {@code FrameConfiguration} with padding {@code 2}, center alignment,
     * the default markup parser, and no border color.
     */
    public static final FrameConfiguration DEFAULT = new FrameConfiguration();

    private final int padding;
    private final FrameAlign align;
    private final MarkupParser parser;
    private final AnsiCode[] borderColor;

    private FrameConfiguration() {
        this(new FrameConfigurationBuilder());
    }

    private FrameConfiguration(FrameConfigurationBuilder builder) {
        this.padding = builder.padding;
        this.align = builder.frameAlign;
        this.parser = builder.parser;
        this.borderColor = builder.borderColor;
    }

    /**
     * Returns a new builder for constructing a {@code FrameConfiguration}.
     *
     * @return a new {@link FrameConfigurationBuilder}
     */
    public static FrameConfigurationBuilder builder() {
        return new FrameConfigurationBuilder();
    }

    /**
     * Returns the number of blank characters inserted between the frame border and
     * its content on each side.
     *
     * @return the padding value; always {@code >= 0}
     */
    public int getPadding() {
        return this.padding;
    }

    /**
     * Returns the ANSI codes applied to the frame border, or an empty array if no
     * border color has been set.
     *
     * @return the border color codes; never {@code null}, may be empty
     */
    public AnsiCode[] getBorderColor() {
        return this.borderColor.clone();
    }

    /**
     * Returns the default horizontal alignment applied to nested content.
     *
     * <p>Individual nodes may override this value at nest time via
     * {@link io.github.kusoroadeolu.clique.components.Frame#nest(String,
     * io.github.kusoroadeolu.clique.configuration.FrameAlign)}.
     *
     * @return the frame alignment; never {@code null}
     */
    public FrameAlign getFrameAlign() {
        return this.align;
    }

    /**
     * Returns the markup parser used to interpret inline style tags in nested strings
     * and border color specifications.
     *
     * @return the parser; never {@code null}
     */
    public MarkupParser getParser() {
        return this.parser;
    }

    @Override
    public String toString() {
        return "FrameConfiguration[" +
                "padding=" + padding +
                ", align=" + align +
                ", parser=" + parser +
                ", borderColor=" + Arrays.toString(borderColor) +
                ']';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        FrameConfiguration that = (FrameConfiguration) object;
        return padding == that.padding && align == that.align && parser.equals(that.parser) && Arrays.equals(borderColor, that.borderColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, align, parser, Arrays.hashCode(borderColor));
    }

    /**
     * Builder for {@link FrameConfiguration}.
     *
     * <p>Default values match those of {@link FrameConfiguration#DEFAULT}. Methods
     * may be called in any order; each returns {@code this} for chaining.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class FrameConfigurationBuilder {
        private int padding = 2;
        private FrameAlign frameAlign = FrameAlign.CENTER;
        private MarkupParser parser = MarkupParser.DEFAULT;
        private AnsiCode[] borderColor = {};

        private FrameConfigurationBuilder(){}


        /**
         * Sets the padding applied between the frame border and its content.
         *
         * @param padding the number of blank characters on each side; must be {@code >= 0}
         * @return this builder
         * @throws IllegalArgumentException if {@code padding} is negative
         */
        public FrameConfigurationBuilder padding(int padding) {
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
        public FrameConfigurationBuilder borderColor(String borderColor) {
            return borderColor(ParserUtils.getAnsiCodes(borderColor, parser));
        }

        /**
         * Sets the border color directly from one or more {@link AnsiCode} instances.
         *
         * @param borderColor the ANSI codes to apply to the border; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code borderColor} is {@code null}
         */
        public FrameConfigurationBuilder borderColor(AnsiCode... borderColor) {
            Objects.requireNonNull(borderColor, "Border color cannot be null");
            this.borderColor = borderColor;
            return this;
        }

        /**
         * Sets the default horizontal alignment for content nested in the frame.
         *
         * <p>This value is used when content is added without an explicit alignment.
         * Individual nodes may override it at nest time.
         *
         * @param align the alignment to apply; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code align} is {@code null}
         */
        public FrameConfigurationBuilder frameAlign(FrameAlign align) {
            Objects.requireNonNull(align, "Frame align cannot be null");
            this.frameAlign = align;
            return this;
        }

        /**
         * Sets the markup parser used to interpret inline style tags in nested content
         * and border color markup strings.
         *
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public FrameConfigurationBuilder parser(MarkupParser parser) {
            Objects.requireNonNull(parser, "Parser cannot be null");
            this.parser = parser;
            return this;
        }

        /**
         * Constructs a new {@link FrameConfiguration} from the current builder state.
         *
         * @return a new, immutable {@code FrameConfiguration}
         */
        public FrameConfiguration build() {
            return new FrameConfiguration(this);
        }
    }
}