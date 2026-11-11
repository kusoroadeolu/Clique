package io.github.kusoroadeolu.clique;

import io.github.kusoroadeolu.clique.components.*;
import io.github.kusoroadeolu.clique.configuration.*;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.AnsiDetector;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.spi.CliqueTheme;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;
import io.github.kusoroadeolu.clique.style.Ink;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Main facade for the Clique library.
 *
 * <p>{@code Clique} provides static factory methods for all library components.
 * This is the primary entry point for interacting with Clique.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Clique.parser().print("[red, bold]Error:[/] Something went wrong");
 *
 * Clique.table()
 *     .headers("Name", "Age")
 *     .row("Alice", "25")
 *     .render();
 * }</pre>
 *
 * @since 1.0.0
 */
@Stable(since = "3.2.0")
public final class Clique {

    private Clique() {
        throw new AssertionError();
    }

    // -------------------------------------------------------------------------
    // STYLE BUILDER
    // -------------------------------------------------------------------------

    /**
     * Creates a new {@link StyleBuilder} for fluently composing styled strings.
     *
     * @return a new {@code StyleBuilder} instance
     */
    public static StyleBuilder styleBuilder() { return CliqueStyles.styleBuilder(); }

    // -------------------------------------------------------------------------
    // PARSER
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link MarkupParser} with default configuration.
     *
     * @return a new {@code MarkupParser} instance
     */
    public static MarkupParser parser() { return CliqueStyles.parser(); }

    /**
     * Creates a {@link MarkupParser} with the given configuration.
     *
     * @param configuration the parser configuration to use
     * @return a new {@code MarkupParser} instance
     */
    public static MarkupParser parser(ParserConfiguration configuration) { return CliqueStyles.parser(configuration); }

    // -------------------------------------------------------------------------
    // INK
    // -------------------------------------------------------------------------

    /**
     * Creates a new {@link Ink} instance.
     *
     * <p>{@code Ink} provides an immutable, fluent API for applying styles to strings.</p>
     *
     * @return a new {@code Ink} instance
     */
    public static Ink ink() {
        return CliqueStyles.ink();
    }

    /**
     * Creates a new {@link Ink} instance with the given {@link StyleContext}.
     *
     * @param context the style context to apply
     * @return a new {@code Ink} instance
     */
    public static Ink ink(StyleContext context) {
        return CliqueStyles.ink(context);
    }

    // -------------------------------------------------------------------------
    // TABLE
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link PendingTable} with the {@link TableType#BOX_DRAW} type and {@link TableConfiguration#DEFAULT}.
     *
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table() { return CliqueComponents.table(); }

    /**
     * Creates a {@link PendingTable} with the {@link TableType#BOX_DRAW} type and given configuration.
     *
     * @param configuration the table configuration to use
     * @throws NullPointerException if the Table configuration is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(TableConfiguration configuration) { return CliqueComponents.table(configuration); }

    /**
     * Creates a {@link PendingTable} with the given type.
     *
     * @param type the table type to use
     * @throws NullPointerException if the table type is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(TableType type) { return CliqueComponents.table(type); }

    /**
     * Creates a {@link PendingTable} with the given type and configuration.
     *
     * @param type          the table type to use
     * @param configuration the table configuration to use
     * @throws NullPointerException if the table type or configuration is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(TableType type, TableConfiguration configuration) { return CliqueComponents.table(type, configuration); }

    /**
     * Creates a {@link PendingTable} with the given type and border color.
     *
     * @param type        the table type to use
     * @param borderColor one or more ANSI codes to apply to the border
     * @throws NullPointerException if the table type or border color is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(TableType type, AnsiCode... borderColor) { return CliqueComponents.table(type, borderColor); }

    /**
     * Creates a {@link PendingTable} with the {@link TableType#BOX_DRAW} type and given border color.
     *
     * @param borderColor one or more ANSI codes to apply to the border
     * @throws NullPointerException if the table type or border color is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(AnsiCode... borderColor) { return CliqueComponents.table(borderColor); }

    /**
     * Creates a {@link PendingTable} with the given type and a named border color.
     *
     * @param type        the table type to use
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the table type or border color is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(TableType type, String borderColor) { return CliqueComponents.table(type, borderColor); }

    /**
     * Creates a {@link PendingTable} with the {@link TableType#BOX_DRAW} type and a named border color.
     *
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the border color is null
     * @return a new {@code PendingTable} instance
     */
    public static PendingTable table(String borderColor) { return CliqueComponents.table(borderColor); }

    // -------------------------------------------------------------------------
    // BOX
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link Box} with the {@link BoxType#ROUNDED} type and {@link BoxConfiguration#DEFAULT} configuration.
     *
     * @return a new {@code Box} instance
     */
    public static Box box() { return CliqueComponents.box(); }

    /**
     * Creates a {@link Box} with the given configuration.
     *
     * @param configuration the box configuration to use
     * @throws NullPointerException if the configuration is null
     * @return a new {@code Box} instance
     */
    public static Box box(BoxConfiguration configuration) { return CliqueComponents.box(configuration); }

    /**
     * Creates a {@link Box} with the given type and configuration.
     *
     * @param type          the box type to use
     * @param configuration the box configuration to use
     * @throws NullPointerException if the box type or configuration is null
     * @return a new {@code Box} instance
     */
    public static Box box(BoxType type, BoxConfiguration configuration) { return CliqueComponents.box(type, configuration); }

    /**
     * Creates a {@link Box} with the given type and {@link BoxConfiguration#DEFAULT} configuration.
     *
     * @param type the box type to use
     * @throws NullPointerException if the box type is null
     * @return a new {@code Box} instance
     */
    public static Box box(BoxType type) { return CliqueComponents.box(type); }

    /**
     * Creates a {@link Box} with the given type and border color.
     *
     * @param type the box type to use
     * @param borderColor one or more ANSI codes to apply to the border
     * @return a new {@code Box} instance
     */
    public static Box box(BoxType type, AnsiCode... borderColor) { return CliqueComponents.box(type, borderColor); }

    /**
     * Creates a {@link Box} with the given border color.
     *
     * @param borderColor one or more ANSI codes to apply to the border
     * @throws NullPointerException if the box type or configuration is null
     * @return a new {@code Box} instance
     */
    public static Box box(AnsiCode... borderColor) { return CliqueComponents.box(borderColor); }

    /**
     * Creates a {@link Box} with the given type and a named border color.
     *
     * @param type        the box type to use
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the box type or border color is null
     * @return a new {@code Box} instance
     */
    public static Box box(BoxType type, String borderColor) { return CliqueComponents.box(type, borderColor); }

    /**
     * Creates a {@link Box} with a named border color.
     *
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the border color is null
     * @return a new {@code Box} instance
     */
    public static Box box(String borderColor) { return CliqueComponents.box(borderColor); }

    // -------------------------------------------------------------------------
    // DIVIDER
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link Divider} with a given width.
     *
     * @param width the width of the divider.
     * @return a new {@link Divider} instance
     */
    public static Divider divider(int width ) {
        return CliqueComponents.divider(width);
    }

    /**
     * Creates a {@link Divider} with a given width and color.
     *
     * @param width the width of the divider
     * @param color the color of the divider
     * @return a new {@link Divider} instance
     */
    public static Divider divider(int width, String color) {
        return CliqueComponents.divider(width, color);
    }

    /**
     * Creates a {@link Divider} with a given width and color.
     *
     * @param width the width of the divider
     * @param color the color of the divider
     * @return a new {@link Divider} instance
     */
    public static Divider divider(int width, AnsiCode... color) {
        return CliqueComponents.divider(width, color);
    }

    /**
     * Creates a {@link Divider} with a given width and configuration.
     *
     * @param width the width of the divider
     * @param config the divider configuration to use
     * @return a new {@link Divider} instance
     */
    public static Divider divider(int width, DividerConfiguration config) {
        return CliqueComponents.divider(width, config);
    }

    // -------------------------------------------------------------------------
    // ITEM LIST
    // -------------------------------------------------------------------------

    /**
     * Creates an {@link ItemList} with the {@link ItemListConfiguration#DEFAULT} configuration.
     *
     * @return a new {@code ItemList} instance
     */
    public static ItemList list() { return CliqueComponents.list(); }

    /**
     * Creates an {@link ItemList} with the given configuration.
     *
     * @param itemListConfiguration the item list configuration to use
     * @throws NullPointerException if the configuration is null
     * @return a new {@code ItemList} instance
     */
    public static ItemList list(ItemListConfiguration itemListConfiguration) { return CliqueComponents.list(itemListConfiguration); }
    // -------------------------------------------------------------------------
    // PROGRESS BAR
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link ProgressBar} with the given total and {@link ProgressBarConfiguration#DEFAULT} configuration.
     *
     * @param total the total number of ticks to completion
     * @throws IllegalArgumentException if total is negative
     * @return a new {@code ProgressBar} instance
     */
    public static ProgressBar progressBar(int total) { return CliqueComponents.progressBar(total); }

    /**
     * Creates a {@link ProgressBar} with the given total and configuration.
     *
     * @param total         the total number of ticks to completion
     * @param configuration the progress bar configuration to use
     * @throws IllegalArgumentException if total is negative
     * @throws NullPointerException if the configuration is null
     * @return a new {@code ProgressBar} instance
     */
    public static ProgressBar progressBar(int total, ProgressBarConfiguration configuration) { return CliqueComponents.progressBar(total, configuration); }

    /**
     * Creates a {@link ProgressBar} with the given total and preset style.
     *
     * @param total  the total number of ticks to completion
     * @param preset the predefined style preset to use
     * @throws IllegalArgumentException if total is negative
     * @throws NullPointerException if the preset is null
     * * @return a new {@code ProgressBar} instance
     */
    public static ProgressBar progressBar(int total, ProgressBarPreset preset) { return CliqueComponents.progressBar(total, preset); }

    /**
     * Creates a {@link ProgressBar} with the given total and easing configuration.
     *
     * @param total  the total number of ticks to completion
     * @param easing the easing configuration to apply to the animation
     * @throws IllegalArgumentException if total is negative
     * @throws NullPointerException if the configuration is null
     * * @return a new {@code ProgressBar} instance
     */
    public static ProgressBar progressBar(int total, EasingConfiguration easing) {
        return CliqueComponents.progressBar(total, easing);
    }

    /**
     * Creates an {@link IterableProgressBar} from the given collection and {@link ProgressBarConfiguration#DEFAULT} configuration.
     *
     * <p>The progress bar automatically advances as the collection is iterated.</p>
     *
     * @param <T>        the type of elements in the collection
     * @param collection the collection to iterate over
     * @throws NullPointerException if the collection is null
     * @return a new {@code IterableProgressBar} instance
     */
    public static <T> IterableProgressBar<T> progressBar(Collection<T> collection) {
        return CliqueComponents.progressBar(collection);
    }

    /**
     * Creates an {@link IterableProgressBar} from the given collection with a custom configuration.
     *
     * @param <T>           the type of elements in the collection
     * @param collection    the collection to iterate over
     * @param configuration the progress bar configuration to use
     * @throws NullPointerException if the collection or configuration is null
     * @return a new {@code IterableProgressBar} instance
     */
    public static <T> IterableProgressBar<T> progressBar(Collection<T> collection, ProgressBarConfiguration configuration) {
        return CliqueComponents.progressBar(collection, configuration);
    }

    /**
     * Creates an {@link IterableProgressBar} from the given collection with a preset style.
     *
     * @param <T>        the type of elements in the collection
     * @param collection the collection to iterate over
     * @throws NullPointerException if the collection or preset is null
     * @return a new {@code IterableProgressBar} instance
     */
    public static <T> IterableProgressBar<T> progressBar(Collection<T> collection, ProgressBarPreset preset) {
        return CliqueComponents.progressBar(collection, preset.getConfiguration());
    }

    // -------------------------------------------------------------------------
    // FRAME
    // -------------------------------------------------------------------------

    /**
     * Creates a {@link Frame} with the {@link BoxType#ROUNDED} type and {@link FrameConfiguration#DEFAULT} configuration.
     *
     * @return a new {@code Frame} instance
     */
    public static Frame frame() { return CliqueComponents.frame(); }

    /**
     * Creates a {@link Frame} with the {@link BoxType#ROUNDED} type and the given configuration.
     *
     * @param configuration the frame configuration to use
     * @throws NullPointerException if the configuration is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(FrameConfiguration configuration) { return CliqueComponents.frame(configuration); }

    /**
     * Creates a {@link Frame} with the given box type and {@link FrameConfiguration#DEFAULT} configuration.
     *
     * @param type the box type to use for the frame border
     * @throws NullPointerException if the box type is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(BoxType type) { return CliqueComponents.frame(type); }

    /**
     * Creates a {@link Frame} with the given box type and configuration.
     *
     * @param type          the box type to use for the frame border
     * @param configuration the frame configuration to use
     * @throws NullPointerException if the box type or configuration is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(BoxType type, FrameConfiguration configuration) { return CliqueComponents.frame(type, configuration); }

    /**
     * Creates a {@link Frame} with the given box type and border color.
     *
     * @param type        the box type to use for the frame border
     * @param borderColor one or more ANSI codes to apply to the border
     * @throws NullPointerException if the box type or border color is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(BoxType type, AnsiCode... borderColor) { return CliqueComponents.frame(type, borderColor); }

    /**
     * Creates a {@link Frame} with the {@link BoxType#ROUNDED} type and given border color.
     *
     * @param borderColor one or more ANSI codes to apply to the border
     * @throws NullPointerException if the border color is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(AnsiCode... borderColor) { return CliqueComponents.frame(borderColor); }

    /**
     * Creates a {@link Frame} with the given box type and a named border color.
     *
     * @param type        the box type to use for the frame border
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the box type or border color is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(BoxType type, String borderColor) { return CliqueComponents.frame(type, borderColor); }

    /**
     * Creates a {@link Frame} with the {@link BoxType#ROUNDED} type and named border color.
     *
     * @param borderColor the name of a registered color to apply to the border
     * @throws NullPointerException if the border color is null
     * @return a new {@code Frame} instance
     */
    public static Frame frame(String borderColor) { return CliqueComponents.frame(borderColor); }

    // -------------------------------------------------------------------------
    // TREE
    // -------------------------------------------------------------------------

    /**
     * Creates a root node with the given label and {@link TreeConfiguration#DEFAULT}.
     *
     * @param label the label for this node; must not be {@code null}
     * @throws NullPointerException if {@code label} is {@code null}
     */
    public static Tree tree(String label) { return CliqueComponents.tree(label); }

    /**
     * Creates a root node with the given label and configuration.
     *
     * @param label         the label for this node; must not be {@code null}
     * @param configuration the configuration used by this node and inherited by
     *                      any children added later; must not be {@code null}
     * @throws NullPointerException if {@code label} or {@code configuration} is {@code null}
     */
    public static Tree tree(String label, TreeConfiguration configuration) { return CliqueComponents.tree(label, configuration); }

    /**
     * Creates a {@link Tree} with the given root label and a named connector color.
     *
     * @param label          the label for the root node
     * @param connectorColor the name of a registered color to apply to connectors
     * @throws NullPointerException if the label or connector color is null
     * @return a new {@code Tree} instance
     */
    public static Tree tree(String label, String connectorColor) { return CliqueComponents.tree(label, connectorColor); }

    /**
     * Creates a {@link Tree} with the given root label and connector color.
     *
     * @param label          the label for the root node
     * @param connectorColor one or more ANSI codes to apply to connectors
     * @throws NullPointerException if the label or connector color is null
     * @return a new {@code Tree} instance
     */
    public static Tree tree(String label, AnsiCode... connectorColor) { return CliqueComponents.tree(label, connectorColor); }

    // -------------------------------------------------------------------------
    // CLIQUE CONFIG
    // -------------------------------------------------------------------------

    /**
     * Disables ANSI color output globally for all Clique components.
     *
     * <p>Useful for environments that do not support ANSI escape codes.</p>
     */
    public static void disableCliqueColors() {
        AnsiDetector.disableCliqueColors();
    }

    /**
     * Enables ANSI color output globally for all Clique components.
     *
     * <p>Colors are enabled by default; use this to re-enable after calling
     * {@link #disableCliqueColors()}.</p>
     */
    public static void enableCliqueColors() { AnsiDetector.enableCliqueColors(); }

    // -------------------------------------------------------------------------
    // RGB & Ansi Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates an RGB foreground color.
     *
     * @param r the red component (0–255)
     * @param g the green component (0–255)
     * @param b the blue component (0–255)
     * @throws IllegalArgumentException if {@code r, g, b < 0 || r, g, b > 255}
     * @return a new {@link RGBAnsiCode} for the foreground
     */
    public static RGBAnsiCode rgb(int r, int g, int b) { return CliqueStyles.rgb(r, g, b); }

    /**
     *
     * Creates an RGB color, optionally applied to the background.
     *
     * @param r          the red component (0–255)
     * @param g          the green component (0–255)
     * @param b          the blue component (0–255)
     * @throws IllegalArgumentException if {@code r, g, b < 0 || r, g, b > 255}
     * @param background {@code true} to apply as a background color, {@code false} for foreground
     * @return a new {@link RGBAnsiCode}
     */
    public static RGBAnsiCode rgb(int r, int g, int b, boolean background) { return CliqueStyles.rgb(r, g, b, background); }


    /**
     * Applies a 24-bit RGB foreground color via a {@code #RRGGBB} hex string.
     *
     * @param hexCode the hex color string; must not be {@code null} and must be in {@code #RRGGBB} format
     * @return a new {@link RGBAnsiCode}
     * @throws NullPointerException     if {@code hexCode} is {@code null}
     * @throws IllegalArgumentException if {@code hexCode} is not in {@code #RRGGBB} format
     */
    public static RGBAnsiCode hex(String hexCode) { return CliqueStyles.hex(hexCode); }

    /**
     * Applies a 24-bit RGB color via a {@code #RRGGBB} hex string, optionally to the background.
     *
     * @param hexCode    the hex color string; must not be {@code null} and must be in {@code #RRGGBB} format
     * @param background {@code true} to apply as a background color, {@code false} for foreground
     * @return a new {@link RGBAnsiCode}
     * @throws NullPointerException     if {@code hexCode} is {@code null}
     * @throws IllegalArgumentException if {@code hexCode} is not in {@code #RRGGBB} format
     */
    public static RGBAnsiCode hex(String hexCode, boolean background) { return CliqueStyles.hex(hexCode, background); }

    /**
     * Creates a composite {@link AnsiCode} that combines multiple {@link AnsiCode} instances
     * into a single sequence applied left to right.
     *
     * @param ansiCodes the codes to compose; must not be {@code null}
     * @return a new composite {@link AnsiCode}
     * @throws NullPointerException if {@code ansiCodes} is {@code null}
     */
    public static AnsiCode compose(AnsiCode... ansiCodes) { return CliqueStyles.compose(ansiCodes); }

    /**
     * Creates a composite {@link AnsiCode} that combines a collection of {@link AnsiCode} instances
     * into a single sequence applied in iteration order.
     *
     * @param ansiCodes the codes to compose; must not be {@code null}
     * @return a new composite {@link AnsiCode}
     * @throws NullPointerException if {@code ansiCodes} is {@code null}
     */
    public static AnsiCode compose(Collection<AnsiCode> ansiCodes) { return CliqueStyles.compose(ansiCodes); }

    // -------------------------------------------------------------------------
    // STYLE REGISTRATION
    // -------------------------------------------------------------------------

    /**
     * Registers a single custom style that can be referenced by name in markup.
     *
     * <pre>{@code
     * Clique.registerStyle("error", new RGBColor(255, 0, 0, false));
     * Clique.parser().print("[error]Something went wrong[/]");
     * }</pre>
     *
     * @param style the name to register the style under
     * @param code  the ANSI code to associate with the name
     * @throws NullPointerException if the style or code is null
     */
    public static void registerStyle(String style, AnsiCode code) { CliqueStyles.registerStyle(style, code); }

    /**
     * Registers multiple custom styles from a map of names to ANSI codes.
     *
     * @param codes a map of style names to their corresponding {@link AnsiCode} values
     * @throws NullPointerException if the map is null
     */
    public static void registerStyles(Map<String, AnsiCode> codes) { CliqueStyles.registerStyles(codes); }

    // -------------------------------------------------------------------------
    // THEMES
    // -------------------------------------------------------------------------

    /**
     * Registers a theme by name, making its color tokens available in markup.
     *
     * @param themeName the name of the theme to register
     * @throws NullPointerException if the theme name is null
     */
    public static void registerTheme(String themeName) { CliqueStyles.registerTheme(themeName); }

    /**
     * Registers a {@link CliqueTheme} instance directly.
     *
     * @param theme the theme to register
     * @throws NullPointerException if the theme is null
     */
    public static void registerTheme(CliqueTheme theme) { CliqueStyles.registerTheme(theme); }

    /**
     * Registers multiple themes by name.
     *
     * @param themeNames the names of the themes to register
     * @throws NullPointerException if the names are null
     */
    public static void registerThemes(String... themeNames) { CliqueStyles.registerThemes(themeNames); }

    /**
     * Registers multiple themes from a collection of names.
     *
     * @param themes a collection of theme names to register
     * @throws NullPointerException if the themes are null
     */
    public static void registerThemes(Collection<String> themes) { CliqueStyles.registerThemes(themes); }

    /**
     * Registers all themes discoverable on the classpath via the {@link CliqueTheme} SPI.
     */
    public static void registerAvailableThemes() { CliqueStyles.registerAvailableThemes(); }

    /**
     * Returns a list of all {@link CliqueTheme} instances discoverable on the classpath.
     *
     * @return a list of available themes
     */
    public static List<CliqueTheme> findAvailableThemes() { return CliqueStyles.findAvailableThemes(); }

    /**
     * Finds a registered theme by name.
     *
     * @param themeName the name of the theme to look up
     * @return an {@link Optional} containing the theme if found, or empty if not
     * @throws NullPointerException if the theme name is null
     */
    public static Optional<CliqueTheme> findTheme(String themeName) { return CliqueStyles.findTheme(themeName); }

}