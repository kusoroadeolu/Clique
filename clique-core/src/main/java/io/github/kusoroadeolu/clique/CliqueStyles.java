package io.github.kusoroadeolu.clique;

import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.configuration.StyleContext;
import io.github.kusoroadeolu.clique.internal.CompositeColor;
import io.github.kusoroadeolu.clique.internal.RGBColor;
import io.github.kusoroadeolu.clique.internal.loader.ThemeLoader;
import io.github.kusoroadeolu.clique.internal.markup.GlobalStyleRegistry;
import io.github.kusoroadeolu.clique.internal.utils.MiscUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.spi.CliqueTheme;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;
import io.github.kusoroadeolu.clique.style.Ink;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.*;

/**
 * Sub-facade for style, parsing, RGB, and theme-related functionality.
 */
final class CliqueStyles {

    private CliqueStyles() {}

    // STYLE BUILDER

    public static StyleBuilder styleBuilder() {
        return new StyleBuilder();
    }

    // PARSER

    public static MarkupParser parser() {
        return new MarkupParser();
    }

    public static MarkupParser parser(ParserConfiguration configuration) {
        return new MarkupParser(configuration);
    }

    public static Ink ink() {
        return new Ink();
    }

    public static Ink ink(StyleContext context) {
        return new Ink(context);
    }


    // RGB

    public static RGBAnsiCode rgb(int r, int g, int b) {
        return new RGBColor(r, g, b, false);
    }

    public static RGBAnsiCode rgb(int r, int g, int b, boolean background) {
        return new RGBColor(r, g, b, background);
    }

    public static RGBAnsiCode hex(String hexCode) {
        return MiscUtils.hex(Objects.requireNonNull(hexCode));
    }

    public static RGBAnsiCode hex(String hexCode, boolean background) {
        return MiscUtils.hexBase(Objects.requireNonNull(hexCode), background);
    }

    public static AnsiCode compose(AnsiCode... ansiCodes) {
        return new CompositeColor(Objects.requireNonNull(ansiCodes));
    }

    public static AnsiCode compose(Collection<AnsiCode> ansiCodes) {
        return new CompositeColor(Objects.requireNonNull(ansiCodes));
    }

    // STYLE REGISTRATION

    public static void registerStyle(String style, AnsiCode code) {
        GlobalStyleRegistry.registerStyle(style, code);
    }

    public static void registerStyles(Map<String, AnsiCode> codes) {
        GlobalStyleRegistry.registerStyles(codes);
    }

    // THEMES

    public static void registerTheme(String name) {
        ThemeLoader.register(name);
    }

    public static void registerThemes(String... themes) {
        ThemeLoader.registerThemes(themes);
    }

    public static void registerTheme(CliqueTheme theme) {
        ThemeLoader.register(theme);
    }

    public static void registerThemes(Collection<String> themes) {
        ThemeLoader.registerThemes(themes);
    }

    public static void registerAvailableThemes() {
        ThemeLoader.registerAll();
    }

    public static List<CliqueTheme> findAvailableThemes() {
        return ThemeLoader.findAvailableThemes();
    }

    public static Optional<CliqueTheme> findTheme(String name) {
        return ThemeLoader.find(name);
    }
}