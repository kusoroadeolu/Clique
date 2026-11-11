package io.github.kusoroadeolu.clique.parser;


import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.exceptions.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.internal.CompositeColor;
import io.github.kusoroadeolu.clique.internal.markup.GlobalStyleRegistry;
import io.github.kusoroadeolu.clique.internal.utils.AnsiDetector;
import io.github.kusoroadeolu.clique.internal.utils.ParserUtils;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.ColorCode;
import io.github.kusoroadeolu.clique.style.StyleCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MarkupParserTest {

    @BeforeEach
    void setup() {
        AnsiDetector.enableCliqueColors();
    }

    @AfterEach
    void tearDown() {
        AnsiDetector.disableCliqueColors();
    }

    @Test
    void testCustomDelimiter() {

        ParserConfiguration config = ParserConfiguration
                .builder()
                .delimiter(' ')
                .build();
        MarkupParser parser = new MarkupParser(config);
        String output = parser.parse("[red bold]Text[/]");
        assertTrue(output.contains(ColorCode.RED.ansiSequence()) && output.contains(StyleCode.BOLD.ansiSequence()));

    }

    @Test
    void testCompositeStyleRegistration() {
        AnsiCode composite = new CompositeColor(
                ColorCode.RED,
                StyleCode.BOLD
        );
        Clique.registerStyle("error", composite);
        String output = Clique.parser().parse("[error]Text[/]");
        assertTrue(output.contains(composite.ansiSequence()));
    }


    // PARSING AND CONFIG
    @Test
    void testStrictParsingThrows() {
        ParserConfiguration config = ParserConfiguration
                .builder()
                .enableStrictParsing()
                .build();
        MarkupParser parser = new MarkupParser(config);
        assertThrows(UnidentifiedStyleException.class,
                () -> parser.parse("[invalid]Text[/]"));
    }

    @Test
    void testAutoCloseTags() {
        ParserConfiguration config = ParserConfiguration
                .builder()
                .enableAutoReset()
                .build();
        MarkupParser parser = new MarkupParser(config);
        String output = parser.parse("[red]Text");
        assertTrue(output.endsWith(StyleCode.RESET.ansiSequence()));
    }

    @Test
    void testEmptyInput() {
        assertEquals("", Clique.parser().parse(""));
    }

    @Test
    void testInvalidStyleIgnored() {
        String output = Clique.parser().parse("[notacolor]Text");
        assertFalse(output.contains("\u001B["));
    }

    @Test
    void assert_localStyleResolvesBeforeGlobal() {
        var config = ParserConfiguration.builder().addStyle("mycolor", ColorCode.BLUE).build();
        GlobalStyleRegistry.registerStyle("mycolor", ColorCode.RED);
        String output = Clique.parser(config).parse("[mycolor]Hello"); //Should contain blue instead of red
        assertTrue(output.contains(ColorCode.BLUE.ansiSequence()));
    }

    @Test
    void assert_globalStyleResolvesBeforePredefined() {
        GlobalStyleRegistry.registerStyle("blue", ColorCode.RED);
        String output = Clique.parser().parse("[blue]Hello"); //Should contain red instead of blue
        assertTrue(output.contains(ColorCode.RED.ansiSequence()));
    }


    //Ansi code methods
    @Test
    void testAnsiCodes(){
        var ls = Arrays.stream(ParserUtils.getAnsiCodes("red, blue, cyan")).toList();
        assertEquals(3, ls.size());
        AnsiCode[] arr = {ColorCode.RED, ColorCode.BLUE, ColorCode.CYAN};
        for (var ansi : arr){
            assertTrue(ls.contains(ansi));
        }
    }

    //ORIGINAL STRING TESTS
    @Test
    void test_getOriginalString_stripsAnsi(){
        var parser = MarkupParser.DEFAULT;
        var string = parser.parse("[red, bold]Hello");
        assertEquals("Hello", parser.getOriginalString(string));
    }

    @Test
    void test_getOriginalString_withNoAnsi_returnsEqualString(){
        var parser = MarkupParser.DEFAULT;
        var string = parser.parse("Hello");
        assertEquals("Hello", parser.getOriginalString(string));
    }
}
