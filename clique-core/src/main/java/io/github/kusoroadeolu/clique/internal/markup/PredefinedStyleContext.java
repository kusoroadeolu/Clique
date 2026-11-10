package io.github.kusoroadeolu.clique.internal.markup;

import io.github.kusoroadeolu.clique.configuration.StyleContext;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.exception.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.BackgroundCode;
import io.github.kusoroadeolu.clique.style.ColorCode;
import io.github.kusoroadeolu.clique.style.StyleCode;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//A simple class which holds the maps of the syntax
@InternalApi(since = "3.2.0")
public final class PredefinedStyleContext {

    /**
     * A static, unmodifiable map that associates lowercase color names (keys)
     * with their corresponding ColorCode enum constants (values).
     */
    static final StyleContext COLOR_CODES = StyleContext.from(
            Map.ofEntries(
                    // Standard Colors
                    Map.entry("black", ColorCode.BLACK),
                    Map.entry("red", ColorCode.RED),
                    Map.entry("green", ColorCode.GREEN),
                    Map.entry("yellow", ColorCode.YELLOW),
                    Map.entry("blue", ColorCode.BLUE),
                    Map.entry("magenta", ColorCode.MAGENTA),
                    Map.entry("cyan", ColorCode.CYAN),
                    Map.entry("white", ColorCode.WHITE),

                    // Bright Colors
                    Map.entry("*black", ColorCode.BRIGHT_BLACK),
                    Map.entry("*red", ColorCode.BRIGHT_RED),
                    Map.entry("*green", ColorCode.BRIGHT_GREEN),
                    Map.entry("*yellow", ColorCode.BRIGHT_YELLOW),
                    Map.entry("*blue", ColorCode.BRIGHT_BLUE), // Kept your original example format for bright colors
                    Map.entry("*magenta", ColorCode.BRIGHT_MAGENTA),
                    Map.entry("*cyan", ColorCode.BRIGHT_CYAN),
                    Map.entry("*white", ColorCode.BRIGHT_WHITE)
            ));
    /**
     * A static, unmodifiable map that associates lowercase background color names (keys)
     * with their corresponding BackgroundCode enum constants (values).
     */
    static final StyleContext BACKGROUND_CODES = StyleContext.from(Map.ofEntries(
            // Standard Background Colors
            Map.entry("bg_black", BackgroundCode.BLACK),
            Map.entry("bg_red", BackgroundCode.RED),
            Map.entry("bg_green", BackgroundCode.GREEN),
            Map.entry("bg_yellow", BackgroundCode.YELLOW),
            Map.entry("bg_blue", BackgroundCode.BLUE),
            Map.entry("bg_magenta", BackgroundCode.MAGENTA),
            Map.entry("bg_cyan", BackgroundCode.CYAN),
            Map.entry("bg_white", BackgroundCode.WHITE),

            // Bright Background Colors
            Map.entry("*bg_black", BackgroundCode.BRIGHT_BLACK),
            Map.entry("*bg_red", BackgroundCode.BRIGHT_RED),
            Map.entry("*bg_green", BackgroundCode.BRIGHT_GREEN),
            Map.entry("*bg_yellow", BackgroundCode.BRIGHT_YELLOW),
            Map.entry("*bg_blue", BackgroundCode.BRIGHT_BLUE),
            Map.entry("*bg_magenta", BackgroundCode.BRIGHT_MAGENTA),
            Map.entry("*bg_cyan", BackgroundCode.BRIGHT_CYAN),
            Map.entry("*bg_white", BackgroundCode.BRIGHT_WHITE)
    ));
    /**
     * A static, unmodifiable map that associates lowercase style names (keys)
     * with their corresponding StyleCode enum constants (values).
     */
    static final StyleContext STYLE_CODES = StyleContext.from(Map.ofEntries(
            Map.entry("bold", StyleCode.BOLD),
            Map.entry("dim", StyleCode.DIM),
            Map.entry("italic", StyleCode.ITALIC),
            Map.entry("ul", StyleCode.UNDERLINE),
            Map.entry("rv", StyleCode.REVERSE_VIDEO),
            Map.entry("inv", StyleCode.INVISIBLE_TEXT),
            Map.entry("/", StyleCode.RESET),
            Map.entry("dbl_ul", StyleCode.DOUBLE_UNDERLINE),
            Map.entry("strike", StyleCode.STRIKETHROUGH)
    ));

    static final Map<String, AnsiCode> CUSTOM_STYLE_CODES = new ConcurrentHashMap<>();


    //Local -> Global -> Predefined
    public static AnsiCode get(String s, StyleContext ctx){
        AnsiCode code = ctx.get(s);

        if (code != null){
            return code;
        }

        code = PredefinedStyleContext.CUSTOM_STYLE_CODES.get(s);
        if (code != null) {
            return code;
        }

        code = PredefinedStyleContext.COLOR_CODES.get(s);
        if (code != null) {
            return code;
        }

        code = PredefinedStyleContext.BACKGROUND_CODES.get(s);
        if (code != null) {
            return code;
        }

        code = PredefinedStyleContext.STYLE_CODES.get(s);
        return code;
    }

    private PredefinedStyleContext() {
        throw new AssertionError();
    }


    public static AnsiCode getOrThrow(String s, StyleContext ctx){
        AnsiCode code = get(s.toLowerCase(Locale.ROOT).trim(), ctx);
        if (code == null) throw new UnidentifiedStyleException(s);
        return code;
    }
}
