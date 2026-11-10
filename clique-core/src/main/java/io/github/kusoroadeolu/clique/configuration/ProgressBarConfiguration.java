package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.parser.MarkupParser;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Immutable configuration for a {@link io.github.kusoroadeolu.clique.components.ProgressBar}.
 *
 * <p>Instances are obtained via {@link #builder()} or {@link #fromPreset(ProgressBarPreset)}.
 * The {@link #DEFAULT} constant is equivalent to {@link ProgressBarPreset#BLOCKS}.
 *
 * The default values are:
 * <ul>
 *   <li>length: {@code 40}</li>
 *   <li>complete: {@code '█'}</li>
 *   <li>incomplete: {@code '░'}</li>
 *   <li>format: {@code ":bar :percent% [:elapsed/:remaining]"}</li>
 *   <li>parser: {@link MarkupParser#DEFAULT}</li>
 *   <li>easing: {@link EasingConfiguration#DEFAULT}</li>
 *   <li>styles: empty (no conditional formats)</li>
 * </ul>
 *
 * <p>The format string controls how the bar is rendered. The following tokens are supported:
 * <ul>
 *   <li>{@code :bar} — the filled/unfilled bar segment</li>
 *   <li>{@code :progress} — current tick, right-aligned to total width</li>
 *   <li>{@code :total} — the total tick count</li>
 *   <li>{@code :percent} — completion percentage, right-aligned to 3 characters</li>
 *   <li>{@code :elapsed} — elapsed time in {@code mm:ss}</li>
 *   <li>{@code :remaining} — estimated remaining time in {@code mm:ss}, or {@code --:--}</li>
 * </ul>
 *
 * <p>Per-range format overrides can be registered via
 * {@link ProgressBarConfigurationBuilder#styleWhen(Predicate, String)} or
 * {@link ProgressBarConfigurationBuilder#styleRange(int, int, String)}. When the current
 * completion percentage matches a registered predicate, its format is used in place of
 * the default. Predicates are evaluated in registration order; the first match wins.
 *
 * <p>This class is immutable and thread-safe. {@link ProgressBarConfigurationBuilder} is
 * <b>not</b> thread-safe; external synchronization is required if a builder instance
 * is shared across threads.
 *
 * <p>Example:
 * <pre>{@code
 * ProgressBarConfiguration config = ProgressBarConfiguration.builder()
 *     .length(50)
 *     .complete('=')
 *     .incomplete('-')
 *     .styleRange(0, 49, "[red]:bar :percent%[/]")
 *     .styleRange(50, 99, "[yellow]:bar :percent%[/]")
 *     .styleRange(100, 100, "[green]:bar :percent%[/]")
 *     .build();
 * }</pre>
 *
 * @since 3.0.0
 */
@Stable(since = "3.2.0")
public final class ProgressBarConfiguration {
    /**
     * A default {@code ProgressBarConfiguration} equivalent to {@link ProgressBarPreset#BLOCKS}.
     */
    public static final ProgressBarConfiguration DEFAULT = ProgressBarPreset.BLOCKS.getConfiguration();

    private final int length;
    private final char complete;
    private final char incomplete;
    private final String format;
    private final MarkupParser parser;
    private final List<ProgressBarPredicate> styles;
    private final EasingConfiguration easingConfiguration;
    private final int ticksPerUnit;


    private ProgressBarConfiguration(ProgressBarConfigurationBuilder builder) {
        this.length = builder.length;
        this.complete = builder.complete;
        this.incomplete = builder.incomplete;
        this.format = builder.format;
        this.parser = builder.parser;
        this.styles = List.copyOf(builder.styles);
        this.easingConfiguration = builder.easing;
        this.ticksPerUnit = builder.tickPerUnit;
    }

    /**
     * Returns a new builder for constructing a {@code ProgressBarConfiguration}.
     *
     * @return a new {@link ProgressBarConfigurationBuilder}
     */
    public static ProgressBarConfigurationBuilder builder() {
        return new ProgressBarConfigurationBuilder();
    }

    /**
     * Returns a builder pre-populated with the values of the given preset.
     *
     * <p>The returned builder can be further customized before calling
     * {@link ProgressBarConfigurationBuilder#build()}. The preset's easing and
     * style predicates are <b>not</b> copied; only {@code length}, {@code complete},
     * {@code incomplete}, and {@code format} are transferred.
     *
     * @param preset the preset to derive initial values from; must not be {@code null}
     * @return a new {@link ProgressBarConfigurationBuilder} seeded from the preset
     * @throws NullPointerException if {@code preset} is {@code null}
     * @since 3.2.1
     */
    @Stable(since = "3.2.1")
    public static ProgressBarConfigurationBuilder fromPreset(ProgressBarPreset preset) {
        Objects.requireNonNull(preset, "Preset cannot be null");
        var config = preset.getConfiguration();
        return ProgressBarConfiguration
                .builder()
                .length(config.length)
                .complete(config.complete)
                .incomplete(config.incomplete)
                .format(config.format);
    }

    @InternalApi(since = "3.2.1")
    public static ProgressBarConfiguration fromEasing(EasingConfiguration easing) {
        Objects.requireNonNull(easing, "Easing config cannot be null");
        return ProgressBarConfiguration
                .builder()
                .easing(easing)
                .build();
    }

    // Get the format based on current percent
    @InternalApi(since = "3.2.1")
    public String getFormatForPercent(int percent) {
        return styles.stream()
                .filter(style -> style.matches(percent))
                .findFirst()
                .map(ProgressBarPredicate::format)
                .orElse(format);  // Fall back to default format
    }

    /**
     * Returns the markup parser used to interpret inline style tags in the rendered output.
     *
     * @return the parser; never {@code null}
     */
    public MarkupParser getParser() {
        return parser;
    }

    /**
     * Returns the total character length of the bar segment rendered for {@code :bar}.
     *
     * @return the bar length; always {@code >= 0}
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the character used to represent completed progress in the bar segment.
     *
     * @return the completion character
     */
    public char getComplete() {
        return complete;
    }

    /**
     * Returns the character used to represent remaining progress in the bar segment.
     *
     * @return the incomplete character
     */
    public char getIncomplete() {
        return incomplete;
    }

    /**
     * Returns the default format string used when no style predicate matches.
     *
     * <p>See the class-level documentation for the list of supported tokens.
     *
     * @return the format string; never {@code null}
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the easing configuration applied during animated ticks.
     *
     * @return the easing configuration; never {@code null}
     */
    public EasingConfiguration getEasing() {
        return easingConfiguration;
    }

    /**
     * Returns the number of ticks that represent one logical unit of progress.
     *
     * <p>Used to compute the {@code :units} and {@code :total-units} format tokens,
     * which display progress scaled to a user-defined unit rather than raw ticks.
     *
     * @return the ticks-per-unit value; always {@code >= 1}
     */
    public int getTicksPerUnit() {
        return ticksPerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ProgressBarConfiguration that = (ProgressBarConfiguration) o;
        return length == that.length && complete == that.complete && incomplete == that.incomplete && ticksPerUnit == that.ticksPerUnit && format.equals(that.format) && parser.equals(that.parser) && styles.equals(that.styles) && easingConfiguration.equals(that.easingConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, complete, incomplete, format, parser, styles, easingConfiguration, ticksPerUnit);
    }

    @Override
    public String toString() {
        return "ProgressBarConfiguration[" +
                "length=" + length +
                ", complete=" + complete +
                ", incomplete=" + incomplete +
                ", format='" + format + '\'' +
                ", parser=" + parser +
                ", styles=" + styles +
                ", easingConfiguration=" + easingConfiguration +
                ", ticksPerUnit=" + ticksPerUnit +
                ']';
    }

    /**
     * Builder for {@link ProgressBarConfiguration}.
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class ProgressBarConfigurationBuilder {
        private int length = 40;
        private char complete = '█';
        private char incomplete = '░';
        private int tickPerUnit = 1;
        private String format = ":bar :percent% [:elapsed/:remaining]";
        private MarkupParser parser = MarkupParser.DEFAULT;
        private final List<ProgressBarPredicate> styles = new ArrayList<>();
        private EasingConfiguration easing = EasingConfiguration.DEFAULT;
        private static final String FORMAT_ERR_MESSAGE = "Format cannot be null";

        /**
         * Sets the character length of the bar segment rendered for {@code :bar}.
         *
         * @param length the bar length; must be {@code >= 0}
         * @return this builder
         * @throws IllegalArgumentException if {@code length} is negative
         */
        public ProgressBarConfigurationBuilder length(int length) {
            if (length < 0) throw new IllegalArgumentException("Length must be positive");
            this.length = length;
            return this;
        }

        /**
         * Sets the character used to represent completed progress in the bar segment.
         *
         * @param complete the completion character
         * @return this builder
         */
        public ProgressBarConfigurationBuilder complete(char complete) {
            this.complete = complete;
            return this;
        }

        /**
         * Sets the number of ticks that represent one logical unit of progress.
         *
         * <p>For example, if each tick represents one byte and you want to display
         * progress in kilobytes, set this to {@code 1024}.
         *
         * @param ticksPerUnit the ticks-per-unit value; must be {@code >= 1}
         * @return this builder
         * @throws IllegalArgumentException if {@code tickPerUnit} is less than {@code 1}
         */
        public ProgressBarConfigurationBuilder ticksPerUnit(int ticksPerUnit) {
            if (ticksPerUnit <= 0) throw new IllegalArgumentException("Ticks per unit cannot be less than 1");
            this.tickPerUnit = ticksPerUnit;
            return this;
        }

        /**
         * Sets the character used to represent remaining progress in the bar segment.
         *
         * @param incomplete the incomplete character
         * @return this builder
         */
        public ProgressBarConfigurationBuilder incomplete(char incomplete) {
            this.incomplete = incomplete;
            return this;
        }

        /**
         * Sets the default format string used to render the progress bar.
         *
         * <p>See {@link ProgressBarConfiguration} for the list of supported tokens.
         *
         * @param format the format string; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code format} is {@code null}
         */
        public ProgressBarConfigurationBuilder format(String format) {
            requireNonNull(format, FORMAT_ERR_MESSAGE);
            this.format = format;
            return this;
        }

        /**
         * Sets the markup parser used to interpret inline style tags in progress bar
         *
         * @param parser the parser to use; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code parser} is {@code null}
         */
        public ProgressBarConfigurationBuilder parser(MarkupParser parser) {
            this.parser = Objects.requireNonNull(parser);
            return this;
        }

        /**
         * Registers a conditional format that is applied when {@code condition} matches
         * the current completion percentage.
         *
         * <p>Predicates are evaluated in registration order; the first match wins.
         * If no predicate matches, the default format set via {@link #format(String)} is used.
         *
         * @param condition a predicate over the completion percentage {@code [0, 100]}; must not be {@code null}
         * @param format    the format string to use when the condition matches; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code condition} or {@code format} is {@code null}
         */
        public ProgressBarConfigurationBuilder styleWhen(Predicate<Integer> condition, String format) {
            requireNonNull(format, FORMAT_ERR_MESSAGE);
            requireNonNull(condition, "Condition cannot be null");
            this.styles.add(new ProgressBarPredicate(condition, format));
            return this;
        }


        /**
         * Registers a conditional format applied when the completion percentage falls within
         * {@code [min, max]}, inclusive.
         *
         * <p>Delegates to {@link #styleWhen(Predicate, String)}.
         *
         * @param min    the lower bound of the percentage range; must be {@code >= 0}
         * @param max    the upper bound of the percentage range
         * @param format the format string to use within this range; must not be {@code null}
         * @return this builder
         * @throws IllegalArgumentException if {@code min} is negative
         * @throws NullPointerException     if {@code format} is {@code null}
         */
        public ProgressBarConfigurationBuilder styleRange(int min, int max, String format) {
            if (min < 0) throw new IllegalArgumentException("Min must be positive");
            if (max < min) throw new IllegalArgumentException("Max must be greater than or equal to min");
            return styleWhen(p -> p >= min && p <= max, requireNonNull(format, FORMAT_ERR_MESSAGE));
        }

        /**
         * Adds a collection of {@link ProgressBarPredicate} style entries to this builder.
         *
         * <p>Predicates are appended in iteration order and evaluated after any previously
         * registered styles.
         *
         * @param formats the style predicates to add; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code formats} is {@code null}
         */
        public ProgressBarConfigurationBuilder styles(Collection<ProgressBarPredicate> formats) {
            requireNonNull(formats, "Format styles cannot be null");
            this.styles.addAll(formats);
            return this;
        }

        /**
         * Sets the easing configuration applied during animated ticks.
         *
         * @param easing the easing configuration; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code easing} is {@code null}
         */
        public ProgressBarConfigurationBuilder easing(EasingConfiguration easing) {
            requireNonNull(easing, "Easing configuration cannot be null");
            this.easing = easing;
            return this;
        }

        /**
         * Builds and returns a new {@link ProgressBarConfiguration} from the current builder state.
         *
         * @return a new {@code ProgressBarConfiguration}
         */
        public ProgressBarConfiguration build() {
            return new ProgressBarConfiguration(this);
        }
    }
}