package io.github.kusoroadeolu.clique.internal.markup;

import io.github.kusoroadeolu.clique.configuration.StyleContext;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.exception.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.ESC;


/**
 * This class extracts valid token forms from the given string
 * Note that this class will ignore malformed tags and print them as is
 */
@InternalApi(since = "3.2.0")
public final class Tokenizer {
    private static final char FORM_START = '[';
    private static final char FORM_CLOSE = ']';
    private static final char ESCAPE_SEQUENCE = '\\';
    private Tokenizer(){}

    public static ParseResult tokenize(String input, String delimiter, boolean enableStrictParsing) {
        return tokenize(input, delimiter, enableStrictParsing, StyleContext.builder().build());
    }


        /**
         * Extracts valid tokens and form tags from the given string
         *
         * @param input The string to parse
         * @return A parse result containing the form tags and the parser tokens
         *
         */

        /*
         * [red]                 valid, will get parsed
         * [notastyle]           valid syntax, unknown style: ignored (throws in strict mode)
         * [nested[nested]]      not parsed, passed through as-is
         * [not closed [red]x    nothing is parsed: the unclosed [ leaves the bracket depth above 0,
         *                       so this and every later tag in the same string is skipped
         * \[red]                escaped, rendered as literal [red]
         */
    public static ParseResult tokenize(String input, String delimiter, boolean enableStrictParsing, StyleContext context) {
        List<ParseToken> tokens = new ArrayList<>();
        final String delimiterPattern = Pattern.quote(delimiter);

        if (input == null || input.isEmpty()) return new ParseResult(List.of());

        final int len = input.length();
        int idx = 0;
        int fsDepth = 0; //Tracking boolean to keep track of the number of form starts we've seen
        int fcDepth = 0;

        for (int i = 0; i < len; i++) {
            final char c = input.charAt(i);
            if (c == FORM_START && charNotEquals(input, i - 1, ESC) && charNotEquals(input, i - 1, ESCAPE_SEQUENCE)) { //This will always switch the form start, if it finds another [ after this
                //If we're still tracking, this means we have nested tag, just skip it
                fcDepth = 0; //Next form start, reset fc depth. Basically means we had something like this ]some_string[
                idx = i;
                ++fsDepth;
            }

            if (c == FORM_CLOSE) { //Only parse the input if we're still tracking the valid tag
                ++fcDepth;

                if (fsDepth == 1 && fcDepth == 1){ //Only if we dont have nested tags, with both open and closed tags
                    final String fullTag = input.substring(idx, i + 1); //Parse the extracted input, something like this[tag]
                    final List<AnsiCode> validStyles = getValidStyles(fullTag, delimiterPattern, context ,enableStrictParsing);
                    if (!validStyles.isEmpty()) {
                        tokens.add(new ParseToken(idx, i, validStyles));
                    }
                }
                fsDepth = Math.max(0, --fsDepth);
            }
        }

        return new ParseResult(tokens);
    }

    private static boolean charNotEquals(String input, int i, char c) {
        if (i < 0) return true;
        return input.charAt(i) != c;
    }

    /*
    * [red] valid, will get parsed
    * [notastyle] valid, won't get parsed
    * [nested[nested]] won't get parsed
    * [not closed [red, blue] hello    //Read nor blue will get parsed
    * */


    //Check if there are valid styles in the extracted string
    //ESP -> Enable strict parsing
    private static List<AnsiCode> getValidStyles(String tag, String delimiterPattern, StyleContext context ,boolean esp) {
        if (tag.length() <= 2) return List.of();  //Check if the extracted string is just empty braces, or a malformed tag
        tag = removeForms(tag); //Clean the string

        final String[] styles = tag.split(delimiterPattern);
        final List<AnsiCode> validStyles = new ArrayList<>();
        for (String s : styles) {
            s = s.toLowerCase(Locale.ROOT).trim();
            addValidStyles(s, validStyles, context ,esp);
        }

        return validStyles;
    }


    private static String removeForms(String s) {
        return s.substring(1, s.length() - 1);
    }

    // A helper method that checks if each map contains a key of the given string
    private static void addValidStyles(String s, List<AnsiCode> list, StyleContext context, boolean enableStrictParsing) {
        AnsiCode code = PredefinedStyleContext.get(s, context);
        if (code != null){
            list.add(code);
            return;
        }

        if (enableStrictParsing) {
            throw new UnidentifiedStyleException(s);
        }
    }

}