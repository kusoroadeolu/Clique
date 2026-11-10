package io.github.kusoroadeolu.clique.internal;

public record Hyperlink(String url) {
    private static final String HYPER_WRAPPER = "\033]8;;%s\033\\%s\033]8;;\033\\";

    public String apply(String text) {
        return HYPER_WRAPPER.formatted(url, text);
    }
}
