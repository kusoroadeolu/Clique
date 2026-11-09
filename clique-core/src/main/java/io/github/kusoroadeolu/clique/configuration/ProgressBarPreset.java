package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;

/**
 * Predefined {@link ProgressBarConfiguration} presets with sensible defaults.
 *
 * <p>Each constant returns a fully built {@link ProgressBarConfiguration} via
 * {@link #getConfiguration()}. Presets can be used directly or as a starting point
 * for further customization via {@link ProgressBarConfiguration#fromPreset(ProgressBarPreset)}.
 *
 * <p>Available presets and their default values:
 * <table border="1">
 *   <caption>Preset defaults</caption>
 *   <tr><th>Preset</th><th>Length</th><th>Complete</th><th>Incomplete</th><th>Format</th></tr>
 *   <tr><td>BLOCKS</td><td>40</td><td>{@code █}</td><td>{@code ░}</td><td>{@code :bar :percent% [:elapsed/:remaining]}</td></tr>
 *   <tr><td>LINES</td><td>50</td><td>{@code ▂}</td><td>{@code ▁}</td><td>{@code :bar :percent%}</td></tr>
 *   <tr><td>BOLD</td><td>40</td><td>{@code ▰}</td><td>{@code ▱}</td><td>{@code :bar :percent% | :progress/:total}</td></tr>
 *   <tr><td>CLASSIC</td><td>50</td><td>{@code #}</td><td>{@code =}</td><td>{@code [:bar] :percent% [:elapsed]}</td></tr>
 *   <tr><td>DOTS</td><td>50</td><td>{@code ●}</td><td>{@code ○}</td><td>{@code :bar :percent%}</td></tr>
 * </table>
 *
 * <p>Literal brackets in the {@code BLOCKS} and {@code CLASSIC} formats are escaped
 * ({@code \[}) so the presets render correctly under strict parsing. The rendered
 * output is unchanged.
 *
 * <p>Example: customizing a preset:
 * <pre>{@code
 * ProgressBarConfiguration config = ProgressBarConfiguration.fromPreset(ProgressBarPreset.CLASSIC)
 *     .length(60)
 *     .build();
 * }</pre>
 *
 * @since 3.0.0
 */
@Stable(since = "3.2.0")
public enum ProgressBarPreset {

    /**
     * Block-style bar using {@code █} and {@code ░} characters.
     * Format: {@code :bar :percent% [:elapsed/:remaining]}.
     * This is the preset used by {@link ProgressBarConfiguration#DEFAULT}.
     */
    BLOCKS {
        @Override
        public ProgressBarConfiguration getConfiguration() {
            return ProgressBarConfiguration
                    .builder()
                    .length(40)
                    .complete('█')
                    .incomplete('░')
                    .format(":bar :percent% \\[:elapsed/:remaining]")
                    .build();
        }
    },

    /**
     * Line-style bar using {@code ▂} and {@code ▁} characters.
     * Format: {@code :bar :percent%}.
     */
    LINES {
        @Override
        public ProgressBarConfiguration getConfiguration() {
            return ProgressBarConfiguration.builder()
                    .length(50)
                    .complete('▂')
                    .incomplete('▁')
                    .format(":bar :percent%")
                    .build();
        }
    },

    /**
     * Bold-style bar using {@code ▰} and {@code ▱} characters.
     * Format: {@code :bar :percent% | :progress/:total}.
     */
    BOLD {
        @Override
        public ProgressBarConfiguration getConfiguration() {
            return ProgressBarConfiguration.builder()
                    .length(40)
                    .complete('▰')
                    .incomplete('▱')
                    .format(":bar :percent% | :progress/:total")
                    .build();
        }
    },

    /**
     * Classic ASCII bar using {@code #} and {@code =} characters.
     * Format: {@code [:bar] :percent% [:elapsed]}.
     */
    CLASSIC {
        @Override
        public ProgressBarConfiguration getConfiguration() {
            return ProgressBarConfiguration.builder()
                    .length(50)
                    .complete('#')
                    .incomplete('=')
                    .format("\\[:bar] :percent% \\[:elapsed]")
                    .build();
        }
    },

    /**
     * Dot-style bar using {@code ●} and {@code ○} characters.
     * Format: {@code :bar :percent%}.
     */
    DOTS {
        @Override
        public ProgressBarConfiguration getConfiguration() {
            return ProgressBarConfiguration.builder()
                    .length(50)
                    .complete('●')
                    .incomplete('○')
                    .format(":bar :percent%")
                    .build();
        }
    };

    /**
     * Returns a new {@link ProgressBarConfiguration} built from this preset's defaults.
     *
     * <p>Each call returns a distinct instance. To customize a preset's values,
     * use {@link ProgressBarConfiguration#fromPreset(ProgressBarPreset)} instead,
     * which returns a pre-seeded builder.
     *
     * @return a new {@link ProgressBarConfiguration}; never {@code null}
     */
    public abstract ProgressBarConfiguration getConfiguration();
}