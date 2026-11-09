package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Arrays;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.ZERO;

/**
 * Immutable configuration for a {@link io.github.kusoroadeolu.clique.components.Divider}.
 *
 * <p>Controls three aspects of divider rendering:
 * <ul>
 *   <li><b>Divider character</b> — the character used to draw the horizontal line.
 *       Defaults to {@code '─'}.</li>
 *   <li><b>Divider color</b> — the ANSI color applied to the divider characters
 *       during rendering. Defaults to reset (no color).</li>
 *   <li><b>Parser</b> — the {@link MarkupParser} used to resolve inline markup tags
 *       in the divider title.</li>
 * </ul>
 *
 * <p>The {@link #DEFAULT} constant provides a pre-built configuration with the
 * default divider character, no divider color, and {@link MarkupParser#DEFAULT}
 * as the parser.
 *
 * <p>This class is immutable and thread-safe. {@link DividerConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * DividerConfiguration config = DividerConfiguration.builder()
 *     .dividerChar('=')
 *     .dividerColor("red")
 *     .parser(MarkupParser.DEFAULT)
 *     .build();
 *
 * }</pre>
 */
public class DividerConfiguration {

    private final AnsiCode[] dividerColor;
    private final char dividerChar;
    private final MarkupParser parser;

    public static final DividerConfiguration DEFAULT = new DividerConfiguration();

    DividerConfiguration(DividerConfigurationBuilder builder) {
        this(builder.dividerColor, builder.dividerChar, builder.parser);
    }

    DividerConfiguration(){
        this(new DividerConfigurationBuilder());
    }


    DividerConfiguration(AnsiCode[] dividerColor, char dividerChar, MarkupParser parser) {
        this.dividerColor = dividerColor;
        this.dividerChar = dividerChar;
        this.parser = parser;
    }

    /**
     * Returns a new builder for constructing a {@code DividerConfiguration}.
     *
     * @return a new {@link DividerConfigurationBuilder}
     */
    public static DividerConfigurationBuilder builder() {
        return new DividerConfigurationBuilder();
    }

    public AnsiCode[] getDividerColor() {
        return dividerColor.clone();
    }

    public char getDividerChar() {
        return dividerChar;
    }

    public MarkupParser getParser() {
        return parser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DividerConfiguration that = (DividerConfiguration) o;
        return dividerChar == that.dividerChar && Arrays.equals(dividerColor, that.dividerColor) && Objects.equals(parser, that.parser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(dividerColor), dividerChar, parser);
    }

    @Override
    public String toString() {
        return "DividerConfiguration[" +
                "dividerColor=" + Arrays.toString(dividerColor) +
                ", dividerChar=" + dividerChar +
                ", parser=" + parser +
                ']';
    }

    public static class DividerConfigurationBuilder {

		private char dividerChar = '─';

		private AnsiCode[] dividerColor = new AnsiCode[ZERO];

		private MarkupParser parser = MarkupParser.DEFAULT;

		/**
		 * Sets the {@link MarkupParser} used to resolve markup tags in the divider
		 * title.
		 *
		 * @param parser the parser to use; must not be {@code null}
		 * @return this builder
		 * @throws NullPointerException if {@code parser} is {@code null}
		 */
		public DividerConfigurationBuilder parser(MarkupParser parser) {
			this.parser = Objects.requireNonNull(parser, "Parser cannot be null");
			return this;
		}

		/**
		 * Sets the ANSI color to the {@link io.github.kusoroadeolu.clique.components.Divider} characters
		 * during rendering.
		 *
		 * @param colorCodes one or more ANSI codes to apply to Divider;
		 *                   must not be {@code null}
		 * @return this builder
		 * @throws NullPointerException if {@code codes} is {@code null}
		 */
        public DividerConfigurationBuilder dividerColor(AnsiCode... colorCodes) {
            this.dividerColor = Objects.requireNonNull(colorCodes, "Divider color cannot be null").clone();
            return this;
        }

		/**
		 * Sets the ANSI color to the Divider by parsing a markup string using the currently configured parser.
		 * to the {@link io.github.kusoroadeolu.clique.components.Divider} characters
		 *
		 * <p>The markup string is resolved against the parser set via
		 * {@link #parser(MarkupParser)} at the time this method is called.
		 *
		 * @param color a markup string representing the desired Divider color;
		 *              must not be {@code null}
		 * @return this builder
		 * @throws NullPointerException if {@code color} is {@code null}
		 */
        public DividerConfigurationBuilder dividerColor(String color) {
            Objects.requireNonNull(color, "Divider color cannot be null");
            return dividerColor(ParserUtils.getAnsiCodes(color, parser));
        }

		/**
		 * Sets the char used to render the Divider
		 *
		 * @param c the char used to render the Divider
		 * @return this builder
		 * @throws NullPointerException if {@code c} is {@code null}
		 */
		public DividerConfigurationBuilder dividerChar(char c) {

			this.dividerChar = c;
			return this;
		}

		/**
		 * Constructs a new {@link DividerConfiguration} from the current builder state.
		 *
		 * @return a new, immutable {@code DividerConfiguration}
		 */
		public DividerConfiguration build() {
			return new DividerConfiguration(this);
		}
	}

}
