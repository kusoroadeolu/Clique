package io.github.kusoroadeolu.clique.style;

import io.github.kusoroadeolu.clique.configuration.StyleContext;
import io.github.kusoroadeolu.clique.internal.Gradient;
import io.github.kusoroadeolu.clique.internal.Hyperlink;
import io.github.kusoroadeolu.clique.internal.RGBColor;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.markup.PredefinedStyleContext;
import io.github.kusoroadeolu.clique.internal.utils.StringUtils;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.markup.PredefinedStyleContext.getOrThrow;

/**
 * A lightweight, functional, chainable ANSI string builder.
 *
 * <p>Similar in spirit to <a href="https://github.com/chalk/chalk">Chalk</a>. Each method
 * returns a new {@code Ink} instance, leaving the original unchanged. Call {@link #on(String)}
 * to produce the final styled string.
 *
 * <p>Outputs raw ANSI escape sequences directly - no markup parser involved.
 *
 * <p>Usage:
 * <pre>{@code
 * Clique.ink().red().bold().on("Error")
 *
 * Clique.ink().of("ctp_mauve").bold().on("Hello")
 * }</pre>
 *
 * @since 4.0.0
 */
@Stable(since = "4.0.1")
public final class Ink {

    private final List<AnsiCode> codes;
    private final StyleContext context;
    private final Hyperlink hyperlink;
    private final Gradient gradient;

    public Ink() {
        this(StyleContext.NONE);
    }

    public Ink(StyleContext context) {
        this(Collections.emptyList(), context, null, null);
    }

    Ink(List<AnsiCode> codes, StyleContext context) {
        this(codes, context, null, null);
    }

    Ink(List<AnsiCode> codes, StyleContext context, Hyperlink hyperLink, Gradient gradient) {
        Objects.requireNonNull(context, "Style context cannot be null");
        this.codes = Collections.unmodifiableList(codes);
        this.context = context;
        this.hyperlink = hyperLink;
        this.gradient = gradient;
    }

    private Ink with(AnsiCode code) {
        List<AnsiCode> next = new ArrayList<>(codes);
        next.add(code);
        return new Ink(next, context, hyperlink, gradient);
    }

    private Ink with(Hyperlink hyperlink) {
        return new Ink(new ArrayList<>(codes), context, hyperlink, gradient);
    }

    private Ink with(Gradient gradient) {
        return new Ink(new ArrayList<>(codes), context, hyperlink, gradient);
    }


    /**
     * Applies the accumulated ANSI codes to the given value and returns the styled string.
     * Appends a reset sequence at the end.
     *
     * @param value the String to style;
     * @throws NullPointerException if value is null
     * @return the styled string with ANSI sequences applied, or just the raw String if no styles were accumulated
     */
    public String on(String value) {
        Objects.requireNonNull(value, "Value cannot be null");
        if (codes.isEmpty() && gradient == null && hyperlink == null) return value;

        StringBuilder sb = new StringBuilder();
        String styled;
        for (AnsiCode code : codes) {
            sb.append(code.ansiSequence());
        }

        styled = sb.append(value)
                .append(StyleCode.RESET)
                .toString();


        if (gradient != null){
            styled = gradient.apply(styled);
        }

        if (hyperlink != null){
            styled = hyperlink.apply(styled);
        }

        return styled;
    }

    /**
     * Applies the accumulated ANSI codes to the given value and returns the styled string.
     * Appends a reset sequence at the end.
     *
     * @param value the Object to style; {@code toString()} is called on it
     * @throws NullPointerException if value is null
     * @return the styled string with ANSI sequences applied, or just
     *         {@code value.toString()} if no styles were accumulated
     */
    public String on(Object value){
        Objects.requireNonNull(value, "Value cannot be null");
        return on(value.toString());
    }



    /**
     * Adds a named style, looked up from the instance's {@link StyleContext}
     * and {@link PredefinedStyleContext}. Throws {@code UndefinedStyleException} if not found.
     *
     * @param style the registered style name (e.g. {@code "ctp_mauve"})
     * @throws NullPointerException if style is null
     * @return a new {@code Ink} instance with the style accumulated
     */
    public Ink of(String style) {
        Objects.requireNonNull(style, "Style cannot be null");
        AnsiCode code = getOrThrow(style, context);
        return with(code);
    }


    /**
     * Adds a named style, looked up from the instance's {@link StyleContext}
     * and {@link PredefinedStyleContext}. Throws {@code UndefinedStyleException} if not found.
     *
     * @param code the AnsiCode to be applied
     * @throws NullPointerException if style is null
     * @return a new {@code Ink} instance with the style accumulated
     */
    public Ink of(AnsiCode code) {
        Objects.requireNonNull(code, "Ansi code cannot be null");
        return with(code);
    }

    /**
     * Applies a hyperlink to this {@code Ink} instance using the given URL.
     * The hyperlink wraps rendered text in OSC 8 ANSI escape sequences
     * ({@code \033]8;;<url>\033\\<text>\033]8;;\033\\}), supported by
     * most modern terminals.
     *
     * @param url the URL to link to; must not be {@code null}
     * @return a new {@code Ink} instance with the hyperlink accumulated
     * @throws NullPointerException if {@code url} is {@code null}
     */
    public Ink hyperlink(String url) {
        Objects.requireNonNull(url, "Hyper link url cannot be null");
        return with(new Hyperlink(url));
    }

    /**
     * Applies a color gradient to this {@code Ink} instance, transitioning
     * from one {@link RGBAnsiCode} to another across the rendered text.
     *
     * <p>The gradient is computed by linearly interpolating the R, G, and B
     * channels between {@code from} and {@code to} across each visible character.
     * Existing ANSI escape sequences embedded in the text are preserved and
     * excluded from the interpolation; only printable characters are colorized.
     * A {@code RESET} sequence is appended at the end of the output.
     *
     * @param from the starting {@link RGBAnsiCode} of the gradient; must not be {@code null}
     * @param to   the ending {@link RGBAnsiCode} of the gradient; must not be {@code null}
     * @return a new {@code Ink} instance with the gradient accumulated
     * @throws NullPointerException if {@code from} or {@code to} is {@code null}
     */
    public Ink gradient(RGBAnsiCode from, RGBAnsiCode to) {
        Objects.requireNonNull(from, "From RGB code cannot be null");
        Objects.requireNonNull(to, "To RGB code cannot be null");
        return with(new Gradient(from, to));
    }

    /**
     * Applies a color gradient to this {@code Ink} instance, transitioning
     * from one hex code to another across the rendered text.
     *
     * <p>The gradient is computed by linearly interpolating the R, G, and B
     * channels between {@code fromHex} and {@code toHex} across each visible character.
     * Existing ANSI escape sequences embedded in the text are preserved and
     * excluded from the interpolation; only printable characters are colorized.
     * A {@code RESET} sequence is appended at the end of the output.
     *
     * @param fromHex the starting hexCode of the gradient; must not be {@code null}
     * @param toHex   the ending hexCode of the gradient; must not be {@code null}
     * @return a new {@code Ink} instance with the gradient accumulated
     * @throws NullPointerException if {@code from} or {@code to} is {@code null}
     */
    public Ink gradient(String fromHex, String toHex) {
        Objects.requireNonNull(fromHex, "From hex code cannot be null");
        Objects.requireNonNull(toHex, "To hex code cannot be null");
        return with(new Gradient(fromHex, toHex));
    }



    /**
     * Applies a foreground hex color to this {@code Ink} instance.
     *
     * <p>The hex string must be in {@code #RRGGBB} format. The value is parsed
     * into an {@link RGBColor} and applied to the rendered text.
     *
     * @param hexCode the hex color string; must not be {@code null} and must be in {@code #RRGGBB} format
     * @return a new {@code Ink} instance with the hex color accumulated
     * @throws NullPointerException     if {@code hexCode} is {@code null}
     * @throws IllegalArgumentException if {@code hexCode} is not in {@code #RRGGBB} format
     */
    public Ink hex(String hexCode){
        Objects.requireNonNull(hexCode,"Hex code cannot be null");
        return with(StringUtils.hex(hexCode));
    }

    /**
     * Applies a background hex color to this {@code Ink} instance.
     *
     * <p>The hex string must be in {@code #RRGGBB} format. The value is parsed
     * into an {@link RGBColor} and applied to the rendered text.
     *
     * @param hexCode the hex color string; must not be {@code null} and must be in {@code #RRGGBB} format
     * @return a new {@code Ink} instance with the hex color accumulated
     * @throws NullPointerException     if {@code hexCode} is {@code null}
     * @throws IllegalArgumentException if {@code hexCode} is not in {@code #RRGGBB} format
     */
    public Ink bgHex(String hexCode){
        Objects.requireNonNull(hexCode);
        return with(StringUtils.bgHex(hexCode));
    }


    public Ink black()         { return with(ColorCode.BLACK);          }
    public Ink red()           { return with(ColorCode.RED);            }
    public Ink green()         { return with(ColorCode.GREEN);          }
    public Ink yellow()        { return with(ColorCode.YELLOW);         }
    public Ink blue()          { return with(ColorCode.BLUE);           }
    public Ink magenta()       { return with(ColorCode.MAGENTA);        }
    public Ink cyan()          { return with(ColorCode.CYAN);           }
    public Ink white()         { return with(ColorCode.WHITE);          }

    public Ink rgb(int red, int green, int blue) {
        return with(new RGBColor(red, green, blue));
    }

    // Bright foreground colors
    public Ink brightBlack()   { return with(ColorCode.BRIGHT_BLACK);   }
    public Ink brightRed()     { return with(ColorCode.BRIGHT_RED);     }
    public Ink brightGreen()   { return with(ColorCode.BRIGHT_GREEN);   }
    public Ink brightYellow()  { return with(ColorCode.BRIGHT_YELLOW);  }
    public Ink brightBlue()    { return with(ColorCode.BRIGHT_BLUE);    }
    public Ink brightMagenta() { return with(ColorCode.BRIGHT_MAGENTA); }
    public Ink brightCyan()    { return with(ColorCode.BRIGHT_CYAN);    }
    public Ink brightWhite()   { return with(ColorCode.BRIGHT_WHITE);   }


    public Ink bgBlack()         { return with(BackgroundCode.BLACK);          }
    public Ink bgRed()           { return with(BackgroundCode.RED);            }
    public Ink bgGreen()         { return with(BackgroundCode.GREEN);          }
    public Ink bgYellow()        { return with(BackgroundCode.YELLOW);         }
    public Ink bgBlue()          { return with(BackgroundCode.BLUE);           }
    public Ink bgMagenta()       { return with(BackgroundCode.MAGENTA);        }
    public Ink bgCyan()          { return with(BackgroundCode.CYAN);           }
    public Ink bgWhite()         { return with(BackgroundCode.WHITE);          }

    public Ink bgRgb(int red, int green, int blue) {
        return with(new RGBColor(red, green, blue, true));
    }


    public Ink brightBgBlack()   { return with(BackgroundCode.BRIGHT_BLACK);   }
    public Ink brightBgRed()     { return with(BackgroundCode.BRIGHT_RED);     }
    public Ink brightBgGreen()   { return with(BackgroundCode.BRIGHT_GREEN);   }
    public Ink brightBgYellow()  { return with(BackgroundCode.BRIGHT_YELLOW);  }
    public Ink brightBgBlue()    { return with(BackgroundCode.BRIGHT_BLUE);    }
    public Ink brightBgMagenta() { return with(BackgroundCode.BRIGHT_MAGENTA); }
    public Ink brightBgCyan()    { return with(BackgroundCode.BRIGHT_CYAN);    }
    public Ink brightBgWhite()   { return with(BackgroundCode.BRIGHT_WHITE);   }



    public Ink bold()            { return with(StyleCode.BOLD);             }
    public Ink dim()             { return with(StyleCode.DIM);              }
    public Ink italic()          { return with(StyleCode.ITALIC);           }
    public Ink underline()       { return with(StyleCode.UNDERLINE);        }
    public Ink doubleUnderline() { return with(StyleCode.DOUBLE_UNDERLINE); }
    public Ink strikethrough()   { return with(StyleCode.STRIKETHROUGH);    }
    public Ink reverseVideo()    { return with(StyleCode.REVERSE_VIDEO);    }
    public Ink invisible()       { return with(StyleCode.INVISIBLE_TEXT);   }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Ink ink = (Ink) o;
        return Objects.equals(codes, ink.codes) && Objects.equals(context, ink.context) && Objects.equals(hyperlink, ink.hyperlink) && Objects.equals(gradient, ink.gradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codes, context, hyperlink, gradient);
    }

    @Override
    public String toString() {
        return "Ink[" +
                "codes=" + codes +
                ", context=" + context +
                ", hyperlink=" + hyperlink +
                ", gradient=" + gradient +
                ']';
    }
}