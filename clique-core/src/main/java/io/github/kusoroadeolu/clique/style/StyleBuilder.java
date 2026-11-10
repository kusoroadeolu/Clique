package io.github.kusoroadeolu.clique.style;


import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.io.PrintStream;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.AnsiDetector.ansiEnabled;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.clearStringBuilder;
import static io.github.kusoroadeolu.clique.style.StyleCode.RESET;

/**
 * A fluent builder for constructing ANSI-styled strings from multiple segments.
 *
 * <p>Text is accumulated via {@link #append} and {@link #appendAndReset}.
 * Use {@link #append} when you want styles to carry over into subsequent segments,
 * and {@link #appendAndReset} when each segment should be visually isolated.
 *
 * <pre>{@code
 * Clique.styleBuilder()
 *     .appendAndReset("Success: ", ColorCode.GREEN, StyleCode.BOLD)
 *     .appendAndReset("Operation completed", ColorCode.WHITE)
 *     .print();
 * }</pre>
 * @since 1.0.0
 */
@Stable(since = "3.2.0")
@SuppressWarnings("java:S106")
public final class StyleBuilder {

    private final StringBuilder textBuilder;
    private final boolean ansiEnabled;


    public StyleBuilder() {
        textBuilder = new StringBuilder();
        ansiEnabled = ansiEnabled();
    }

    /**
     * Appends styled text to this builder without resetting the terminal.
     * Styles accumulate across subsequent calls.
     *
     * @param text      the text to style
     * @param ansiCodes the ANSI codes to apply
     * @return this builder
     */
    public StyleBuilder append(String text, AnsiCode... ansiCodes) {
        textBuilder.append(style(text, false ,ansiCodes));
        return this;
    }


    /**
     * Appends an ANSI code to this text and then resets the terminal
     *
     * @param text      The text we're applying the ansiCode to
     * @param ansiCodes The ANSI code we're applying to the text
     * @return An instance of this
     *
     */
    public StyleBuilder appendAndReset(String text, AnsiCode... ansiCodes) {
        textBuilder.append(style(text, true ,ansiCodes));
        return this;
    }

    /**
     * Clears the content of the style builder
     */
    public void clear() {
        clearStringBuilder(textBuilder);
    }

    //A helper method to style text with the given codes
    private StringBuilder style(String text, boolean reset, AnsiCode... ansiCodes) {
        StringBuilder sb = new StringBuilder();
        Objects.requireNonNull(text, "Text cannot be null");
        Objects.requireNonNull(ansiCodes, "Ansi codes cannot be null");

        //Check if ansi is enabled
        if (ansiEnabled) {
            for (AnsiCode code : ansiCodes) {
                if (code != null) sb.append(code.ansiSequence());

            }
        }

        sb.append(text);

        if (reset && ansiEnabled) sb.append(RESET);
        return sb;
    }

    /**
     * Returns the accumulated styled string.
     *
     * @return the styled string
     */
    public String toString() {
        return textBuilder.toString();
    }

    /**
     * Prints the accumulated styled string to the given stream.
     *
     * @param stream the stream to print to
     * @throws NullPointerException if {@code stream} is null
     */
    public void print(PrintStream stream) {
        Objects.requireNonNull(stream, "Print stream cannot be null");
        stream.println(this);
    }

    /**
     * Prints the accumulated styled string to {@code System.out}.
     */
    public void print() {
        print(System.out);
    }


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        var that = (StyleBuilder) object;
        return textBuilder.toString().contentEquals(that.textBuilder) && ansiEnabled == that.ansiEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ansiEnabled, textBuilder.toString());
    }

}
