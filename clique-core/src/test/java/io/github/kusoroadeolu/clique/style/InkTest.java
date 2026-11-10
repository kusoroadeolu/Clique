package io.github.kusoroadeolu.clique.style;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.StyleContext;
import io.github.kusoroadeolu.clique.exception.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.internal.utils.AnsiDetector;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class InkTest {

    private Ink ink;

    @AfterEach
    void tearDown() {
        AnsiDetector.disableCliqueColors();
    }

    @BeforeEach
    void setUp() {
        Clique.enableCliqueColors();
        ink = new Ink(StyleContext.NONE);
    }

    @Nested
    class OnMethod {

        @Test
        void noStyles_returnsPlainValue() {
            assertEquals("hello", ink.on("hello"));
        }

        @Test
        void noStyles_nullValue_throwNullPointerEx() {
            assertThrows(NullPointerException.class, () -> ink.on(null));
        }


        @Test
        void wrapsValueWithReset() {
            String result = ink.red().on("Error");
            assertTrue(result.endsWith(StyleCode.RESET.ansiSequence()));
        }
    }


    @Nested
    class Immutability {

        @Test
        void chainReturnsNewInstance() {
            Ink original = ink;
            Ink chained = ink.red();
            assertNotSame(original, chained);
        }

        @Test
        void originalNotMutated() {
            ink.red().bold();
            assertEquals("hello", ink.on("hello")); // ink still has no styles
        }
    }


    @Nested
    class ForegroundColors {

        @Test void black()         { assertAnsi(ColorCode.BLACK,          ink.black());         }
        @Test void red()           { assertAnsi(ColorCode.RED,            ink.red());           }
        @Test void green()         { assertAnsi(ColorCode.GREEN,          ink.green());         }
        @Test void yellow()        { assertAnsi(ColorCode.YELLOW,         ink.yellow());        }
        @Test void blue()          { assertAnsi(ColorCode.BLUE,           ink.blue());          }
        @Test void magenta()       { assertAnsi(ColorCode.MAGENTA,        ink.magenta());       }
        @Test void cyan()          { assertAnsi(ColorCode.CYAN,           ink.cyan());          }
        @Test void white()         { assertAnsi(ColorCode.WHITE,          ink.white());         }
        @Test void brightBlack()   { assertAnsi(ColorCode.BRIGHT_BLACK,   ink.brightBlack());   }
        @Test void brightRed()     { assertAnsi(ColorCode.BRIGHT_RED,     ink.brightRed());     }
        @Test void brightGreen()   { assertAnsi(ColorCode.BRIGHT_GREEN,   ink.brightGreen());   }
        @Test void brightYellow()  { assertAnsi(ColorCode.BRIGHT_YELLOW,  ink.brightYellow());  }
        @Test void brightBlue()    { assertAnsi(ColorCode.BRIGHT_BLUE,    ink.brightBlue());    }
        @Test void brightMagenta() { assertAnsi(ColorCode.BRIGHT_MAGENTA, ink.brightMagenta()); }
        @Test void brightCyan()    { assertAnsi(ColorCode.BRIGHT_CYAN,    ink.brightCyan());    }
        @Test void brightWhite()   { assertAnsi(ColorCode.BRIGHT_WHITE,   ink.brightWhite());   }
    }


    @Nested
    class BackgroundColors {

        @Test void bgBlack()         { assertAnsi(BackgroundCode.BLACK,          ink.bgBlack());         }
        @Test void bgRed()           { assertAnsi(BackgroundCode.RED,            ink.bgRed());           }
        @Test void bgGreen()         { assertAnsi(BackgroundCode.GREEN,          ink.bgGreen());         }
        @Test void bgYellow()        { assertAnsi(BackgroundCode.YELLOW,         ink.bgYellow());        }
        @Test void bgBlue()          { assertAnsi(BackgroundCode.BLUE,           ink.bgBlue());          }
        @Test void bgMagenta()       { assertAnsi(BackgroundCode.MAGENTA,        ink.bgMagenta());       }
        @Test void bgCyan()          { assertAnsi(BackgroundCode.CYAN,           ink.bgCyan());          }
        @Test void bgWhite()         { assertAnsi(BackgroundCode.WHITE,          ink.bgWhite());         }
        @Test void brightBgBlack()   { assertAnsi(BackgroundCode.BRIGHT_BLACK,   ink.brightBgBlack());   }
        @Test void brightBgRed()     { assertAnsi(BackgroundCode.BRIGHT_RED,     ink.brightBgRed());     }
        @Test void brightBgGreen()   { assertAnsi(BackgroundCode.BRIGHT_GREEN,   ink.brightBgGreen());   }
        @Test void brightBgYellow()  { assertAnsi(BackgroundCode.BRIGHT_YELLOW,  ink.brightBgYellow());  }
        @Test void brightBgBlue()    { assertAnsi(BackgroundCode.BRIGHT_BLUE,    ink.brightBgBlue());    }
        @Test void brightBgMagenta() { assertAnsi(BackgroundCode.BRIGHT_MAGENTA, ink.brightBgMagenta()); }
        @Test void brightBgCyan()    { assertAnsi(BackgroundCode.BRIGHT_CYAN,    ink.brightBgCyan());    }
        @Test void brightBgWhite()   { assertAnsi(BackgroundCode.BRIGHT_WHITE,   ink.brightBgWhite());   }
    }

    @Nested
    class TextStyles {

        @Test void bold()            { assertAnsi(StyleCode.BOLD,             ink.bold());            }
        @Test void dim()             { assertAnsi(StyleCode.DIM,              ink.dim());             }
        @Test void italic()          { assertAnsi(StyleCode.ITALIC,           ink.italic());          }
        @Test void underline()       { assertAnsi(StyleCode.UNDERLINE,        ink.underline());       }
        @Test void doubleUnderline() { assertAnsi(StyleCode.DOUBLE_UNDERLINE, ink.doubleUnderline()); }
        @Test void strikethrough()   { assertAnsi(StyleCode.STRIKETHROUGH,    ink.strikethrough());   }
        @Test void reverseVideo()    { assertAnsi(StyleCode.REVERSE_VIDEO,    ink.reverseVideo());    }
        @Test void invisible()       { assertAnsi(StyleCode.INVISIBLE_TEXT,   ink.invisible());       }
    }


    @Nested
    class Combinations {

        @Test
        void colorAndStyle() {
            String expected = ColorCode.RED.ansiSequence()
                    + StyleCode.BOLD.ansiSequence()
                    + "Error"
                    + StyleCode.RESET.ansiSequence();

            assertEquals(expected, ink.red().bold().on("Error"));
        }

        @Test
        void fgBgAndStyle() {
            String expected = ColorCode.WHITE.ansiSequence()
                    + BackgroundCode.RED.ansiSequence()
                    + StyleCode.BOLD.ansiSequence()
                    + "Alert"
                    + StyleCode.RESET.ansiSequence();

            assertEquals(expected, ink.white().bgRed().bold().on("Alert"));
        }

        @Test
        void multipleStyles() {
            String expected = StyleCode.BOLD.ansiSequence()
                    + StyleCode.UNDERLINE.ansiSequence()
                    + StyleCode.ITALIC.ansiSequence()
                    + "text"
                    + StyleCode.RESET.ansiSequence();

            assertEquals(expected, ink.bold().underline().italic().on("text"));
        }
    }

    @Nested
    class OfMethod {

        @Test
        void resolvesPredefinedColor() {
            String expected = ColorCode.RED.ansiSequence() + "hello" + StyleCode.RESET.ansiSequence();
            assertEquals(expected, ink.of("red").on("hello"));
        }

        @Test
        void resolvesPredefinedStyle() {
            String expected = StyleCode.BOLD.ansiSequence() + "hello" + StyleCode.RESET.ansiSequence();
            assertEquals(expected, ink.of("bold").on("hello"));
        }

        @Test
        void resolvesCustomStyle() {
            StyleContext ctx = StyleContext.builder()
                    .add("ctp_mauve", ColorCode.MAGENTA) // stand-in for a real theme color
                    .build();

            Ink inkWithCtx = new Ink(ctx);
            String expected = ColorCode.MAGENTA.ansiSequence() + "hello" + StyleCode.RESET.ansiSequence();
            assertEquals(expected, inkWithCtx.of("ctp_mauve").on("hello"));
        }

        @Test
        void throwsForUnknownStyle() {
            assertThrows(UnidentifiedStyleException.class, () -> ink.of("not_a_real_style"));
        }
    }

    @Nested
    class HexMethod {

        @Test
        void nullHexCode_throwsNullPointerEx() {
            assertThrows(NullPointerException.class, () -> ink.hex(null));
        }

        @Test
        void invalidFormat_missingHash_throwsIllegalArgumentEx() {
            assertThrows(IllegalArgumentException.class, () -> ink.hex("FF5733"));
        }

        @Test
        void invalidFormat_wrongLength_throwsIllegalArgumentEx() {
            assertThrows(IllegalArgumentException.class, () -> ink.hex("#FF57"));
        }

        @Test
        void validHex_wrapsValueWithReset() {
            String result = ink.hex("#FF5733").on("hello");
            assertTrue(result.endsWith(StyleCode.RESET.ansiSequence()));
        }

        @Test
        void validHex_returnsNewInstance() {
            Ink hexInk = ink.hex("#FF5733");
            assertNotSame(ink, hexInk);
        }

        @Test
        void validHex_originalNotMutated() {
            ink.hex("#FF5733");
            assertEquals("hello", ink.on("hello"));
        }
    }

    /**
     * Asserts that the given Ink instance, when called with on("x"),
     * produces the expected ANSI sequence wrapping "x".
     */
    private void assertAnsi(io.github.kusoroadeolu.clique.spi.AnsiCode code, Ink styledInk) {
        String expected = code.ansiSequence() + "x" + StyleCode.RESET.ansiSequence();
        assertEquals(expected, styledInk.on("x"));
    }
}