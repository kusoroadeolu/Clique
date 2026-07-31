package io.github.kusoroadeolu.clique.parser;


import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.markup.ParseResult;
import io.github.kusoroadeolu.clique.internal.markup.ParseToken;
import io.github.kusoroadeolu.clique.internal.markup.StyleResolver;
import io.github.kusoroadeolu.clique.internal.markup.Tokenizer;

import java.util.List;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.markup.MarkupPostProcessor.postProcess;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.EMPTY;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.stripAnsi;
import static java.util.Objects.isNull;


/**
 * This class parses markup-tagged strings into ANSI-styled output.
 *
 * <p><b>Parser rules:</b>
 * <ul>
 *   <li>{@code [red]} — valid; parsed and styled.</li>
 *   <li>{@code [notastyle]} — valid syntax, unrecognized style; passed through as-is unless
 *       strict parsing is enabled.</li>
 *   <li>{@code [nested[nested]]} — nested tags are not supported; passed through as-is.</li>
 *   <li>{@code \[red]} — escaped bracket; rendered as the literal text {@code [red]}.</li>
 *   <li>If a {@code [} is encountered before the current tag closes, the entire tag is ignored.</li>
 * </ul>
 *
 * Formerly known as {@code AnsiStringParser} prior to version 4.0.0.
 *
 * @since 1.0.0
 */
@Stable(since = "3.2.0")
@SuppressWarnings("java:S106")
public record MarkupParser(ParserConfiguration parserConfiguration) {

    /**
     * A parser using {@link ParserConfiguration#DEFAULT}.
     */
    public static final MarkupParser DEFAULT = new MarkupParser();

    public MarkupParser() {
        this(ParserConfiguration.DEFAULT);
    }

    /**
     * Parses a markup-tagged string and returns the ANSI-styled result.
     *
     * @param string the input string, may contain markup tags; may be {@code null}
     * @return the styled string, or the original value if {@code null} or blank
     */
    public String parse(String string) {
        if (isNull(string) || string.isBlank()) return string;
        final ParseResult result = this.getParseResult(string);
        String styled = StyleResolver.resolve(result.tokens(), string, this.parserConfiguration.getEnableAutoReset());
        return postProcess(styled);
    }

    /**
     * Parses the {@link Object#toString()} representation of {@code object}.
     *
     * @param object the object to parse; must not be {@code null}
     * @return the styled string
     * @throws NullPointerException if {@code object} is {@code null}
     */
    public String parse(Object object) {
        Objects.requireNonNull(object, "Object cannot be null");
        return parse(object.toString());
    }

    /**
     * Parses {@code string} and prints the result to standard output, followed by a newline.
     *
     * @param string the input string, may contain markup tags; may be {@code null}
     */
    public void print(String string) {
        System.out.println(parse(string));
    }

    /**
     * Parses the {@link Object#toString()} representation of {@code object} and prints
     * the result to standard output, followed by a newline.
     *
     * @param object the object to parse and print; must not be {@code null}
     * @throws NullPointerException          if {@code object} is {@code null}
     */
    public void print(Object object) {
        Objects.requireNonNull(object, "Object cannot be null");
        print(object.toString());
    }

    /**
     * Strips markup tags and residual ANSI sequences from a previously tokenized string,
     * returning the plain text content.
     *
     * <p>If {@code tokenedString} is {@code null} or blank, it is returned as-is.
     *
     * <p>Example:
     * <pre>{@code
     * parser.getOriginalString("[bold]Hello[/]"); // returns "Hello"
     * }</pre>
     *
     * @param tokenedString a string that may contain markup tags or ANSI sequences;
     *                      may be {@code null}
     * @return the plain text content with all markup and ANSI sequences removed,
     *         or the original value if {@code null} or blank
     */
    public String getOriginalString(String tokenedString) {
        if (isNull(tokenedString) || tokenedString.isBlank()) return EMPTY;
        ParseResult result = this.getParseResult(tokenedString);

        if (!result.isPresent()) {
            return stripAnsi(postProcess(tokenedString));
        }

        final List<ParseToken> tokens = result.tokens();
        final StringBuilder sb = new StringBuilder(tokenedString.length());
        int cursor = 0;

        for (ParseToken token : tokens) {
            sb.append(tokenedString, cursor, token.start());
            cursor = token.end() + 1;
        }

        sb.append(tokenedString, cursor, tokenedString.length());

        return stripAnsi(postProcess(sb.toString()));
    }

    ParseResult getParseResult(String input) {
        return Tokenizer.tokenize(
                input,
                parserConfiguration.getDelimiter(),
                parserConfiguration.getEnableStrictParsing(),
                parserConfiguration.getStyleContext()
        );
    }
}