package io.github.kusoroadeolu.clique.internal.utils;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;

import java.io.Console;
import java.lang.reflect.Method;
import java.util.Locale;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.*;

@InternalApi(since = "3.2.0")
public class AnsiDetector {

    private AnsiDetector() {}

    private static final Method IS_TERMINAL = lookupIsTerminal();

    private static Method lookupIsTerminal() {
        if (Runtime.version().feature() < 22) return null;

        try {
            return Console.class.getMethod("isTerminal");
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static boolean consoleIsTerminal() {
        Console c = System.console();
        if (c == null) return false;
        if (IS_TERMINAL == null) return true;
        try {
            return (Boolean) IS_TERMINAL.invoke(c);
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    public static void refresh() {
        autoDetect();
    }

    public static boolean ansiEnabled() {
        return autoDetect();
    }

    public static void enableCliqueColors() {
        System.setProperty(CLIQUE_COLOR, ALWAYS);
    }

    public static void disableCliqueColors() {
        System.setProperty(CLIQUE_COLOR, NEVER);
    }

    private static boolean autoDetect() {
        String noColor = System.getenv(NO_COLOR);
        if (noColor != null && !noColor.isEmpty()) return false;

        String cliqueColor = System.getProperty(CLIQUE_COLOR);
        if (ALWAYS.equals(cliqueColor)) return true;
        if (NEVER.equals(cliqueColor)) return false;

        String cliColorForce = System.getenv(CLI_COLOR_FORCE);
        if (cliColorForce != null && !cliColorForce.isEmpty() && !cliColorForce.equals("0")) return true;

        String forceColor = System.getenv(FORCE_COLOR);
        if (forceColor != null && !forceColor.isEmpty()) return true;

        String colorTerm = System.getenv(COLOR_TERM);
        if (colorTerm != null) return true;
        String os = System.getProperty(OS_NAME, EMPTY).toLowerCase(Locale.ROOT);
        if (!consoleIsTerminal() && !os.contains(WIN)) return false;

        final String term = System.getenv(TERM);
        if (term == null) {
            if (System.getenv(WT_SESSION) != null) return true;
            return os.contains(WIN);
        }

        return !term.equalsIgnoreCase(DUMB) && !term.equalsIgnoreCase(PLAIN);
    }



}
