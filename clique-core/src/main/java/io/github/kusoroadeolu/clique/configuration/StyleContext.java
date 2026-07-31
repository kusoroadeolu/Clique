package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.CompositeColor;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.exception.NoSuchThemeException;
import io.github.kusoroadeolu.clique.internal.loader.ThemeLoader;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.spi.CliqueTheme;

import java.util.*;

/**
 * A scoped registry of custom markup styles, mapping markup names to {@link AnsiCode}
 * instances.
 *
 * <p>A {@code StyleContext} attached to a {@link io.github.kusoroadeolu.clique.configuration.ParserConfiguration}
 * takes precedence over global custom styles and predefined styles during tag resolution.
 * See {@link ParserConfiguration} for the full resolution order.
 *
 * <p>Instances are obtained via {@link #builder()}, {@link #from(Map)}, or
 * {@link #from(StyleContext)}. {@link #NONE} is a sentinel representing an empty context
 * with no registered styles.
 *
 * <p>This class is immutable and thread-safe. {@link StyleContextBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * StyleContext ctx = StyleContext.builder()
 *     .add("highlight", ColorCode.YELLOW)
 *     .add("muted", StyleCode.DIM)
 *     .build();
 *
 * ParserConfiguration config = ParserConfiguration.builder()
 *     .styleContext(ctx)
 *     .build();
 * }</pre>
 *
 * @since 4.0.0
 */
@Stable(since = "4.0.2")
public final class StyleContext {

    private final Map<String, AnsiCode> localStyles;

    /**
     * A sentinel {@code StyleContext} with no registered styles.
     *
     * <p>Passing this to {@link ParserConfiguration.ParserConfigurationBuilder#styleContext(StyleContext)}
     * is a no-op; no local styles will be added to the parser's resolution chain.
     */
    public static final StyleContext NONE = new StyleContext();

    /**
     * Returns a new builder for constructing a {@code StyleContext}.
     *
     * @return a new {@link StyleContextBuilder}
     */
    public static StyleContextBuilder builder() {
        return new StyleContextBuilder();
    }

    /**
     * Creates a {@code StyleContext} from an existing map of style names to
     * {@link AnsiCode} instances.
     *
     * <p>The provided map is copied; subsequent modifications to it do not affect
     * the returned context.
     *
     * @param codes a map of markup names to ANSI codes; must not be {@code null}
     * @return a new {@code StyleContext} containing the given styles
     * @throws NullPointerException if {@code codes} is {@code null}
     */
    public static StyleContext from(Map<String, AnsiCode> codes) {
        return new StyleContextBuilder().add(codes).build();
    }

    /**
     * Creates a shallow copy of an existing {@code StyleContext}.
     *
     * <p>The registered styles are copied; the returned context is independent of
     * the source.
     *
     * @param context the context to copy; must not be {@code null}
     * @return a new {@code StyleContext} containing the same styles
     * @throws NullPointerException if {@code context} is {@code null}
     */
    public static StyleContext from(StyleContext context) {
        Objects.requireNonNull(context, "Style context cannot be null");
        return StyleContext.from(context.localStyles);
    }

    /**
     * Creates a {@code StyleContext} from a CliqueTheme.
     *
     * <p>The registered styles are copied; the returned context is independent of
     * the source.
     *
     * @param theme the name of the theme to be registered; must not be {@code null}
     * @return a new {@code StyleContext} containing the same styles
     * @throws NullPointerException if {@code theme} is {@code null}
     * @throws NoSuchThemeException if no theme was found
     */
    public static StyleContext from(String theme) {
        var opt = ThemeLoader.find(theme);
        if (opt.isEmpty()) throw new NoSuchThemeException("No such theme: %s".formatted(theme));
        var ct = opt.get();
        return StyleContext.from(ct.styles());
    }

    /**
     * Returns the {@link AnsiCode} registered under the given markup name, or
     * {@code null} if no style is registered for that name.
     *
     * @param s the markup name to look up
     * @return the associated {@link AnsiCode}, or {@code null} if not present
     */
    public AnsiCode get(String s) {
        return localStyles.get(s);
    }

    StyleContext(StyleContextBuilder builder) {
        this.localStyles = builder.localStyles;
    }

    StyleContext() {
        this.localStyles = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        StyleContext that = (StyleContext) o;
        return Objects.equals(localStyles, that.localStyles);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(localStyles);
    }

    @Override
    public String toString() {
        return "StyleContext[" +
                "localStyles=" + localStyles +
                ']';
    }

    /**
     * Builder for {@link StyleContext}.
     *
     * <p>Multiple {@code add} calls accumulate; later registrations for the same
     * markup name overwrite earlier ones. All overloads ultimately store a single
     * {@link AnsiCode} per name — multi-code overloads wrap their arguments in a
     * {@link CompositeColor}.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class StyleContextBuilder {
        private final Map<String, AnsiCode> localStyles;
        private static final String NULL_STYLE_NAME = "Style name cannot be null";

        private StyleContextBuilder() {
            this.localStyles = new HashMap<>();
        }

        /**
         * Registers a single {@link AnsiCode} under the given markup name.
         *
         * @param style the markup name; must not be {@code null}
         * @param code  the ANSI code to associate; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code style} or {@code code} is {@code null}
         */
        public StyleContextBuilder add(String style, AnsiCode code) {
            Objects.requireNonNull(style, NULL_STYLE_NAME);
            Objects.requireNonNull(code, "Ansi code cannot be null");
            localStyles.put(style, code);
            return this;
        }

        /**
         * Registers multiple {@link AnsiCode} instances under the given markup name,
         * combining them into a {@link CompositeColor}.
         *
         * @param style the markup name; must not be {@code null}
         * @param code  one or more ANSI codes to combine; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code style} or {@code code} is {@code null}
         */
        public StyleContextBuilder add(String style, AnsiCode... code) {
            Objects.requireNonNull(style, NULL_STYLE_NAME);
            Objects.requireNonNull(code, "Ansi codes cannot be null");
            localStyles.put(style, new CompositeColor(code));
            return this;
        }

        /**
         * Registers a collection of {@link AnsiCode} instances under the given markup
         * name, combining them into a {@link CompositeColor}.
         *
         * @param style the markup name; must not be {@code null}
         * @param code  a collection of ANSI codes to combine; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code style} or {@code code} is {@code null}
         */
        public StyleContextBuilder add(String style, Collection<AnsiCode> code) {
            Objects.requireNonNull(style, NULL_STYLE_NAME);
            Objects.requireNonNull(code, "Ansi codes cannot be null");
            localStyles.put(style, new CompositeColor(code));
            return this;
        }

        /**
         * Bulk-registers all entries from the given map.
         *
         * <p>Entries are added in iteration order; if the map contains a name already
         * registered in this builder, the map's value overwrites the existing one.
         *
         * @param codes a map of markup names to ANSI codes; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code codes} is {@code null}
         */
        public StyleContextBuilder add(Map<String, AnsiCode> codes) {
            Objects.requireNonNull(codes, "Ansi code map cannot be null");
            localStyles.putAll(codes);
            return this;
        }

        /**
         * Merges all styles from an existing {@link StyleContext} into this builder.
         *
         * <p>If a name from {@code context} is already registered in this builder,
         * the incoming value overwrites the existing one.
         *
         * @param context the context whose styles to merge; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code context} is {@code null}
         */
        public StyleContextBuilder add(StyleContext context) {
            Objects.requireNonNull(context, "Style context cannot be null");
            localStyles.putAll(context.localStyles);
            return this;
        }

        /**
         * Constructs a new {@link StyleContext} from the current builder state.
         *
         * @return a new, immutable {@code StyleContext}
         */
        public StyleContext build() {
            return new StyleContext(this);
        }
    }
}