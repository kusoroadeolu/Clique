package io.github.kusoroadeolu.clique.exception;

public class UnidentifiedStyleException extends RuntimeException {
    public UnidentifiedStyleException(String s) {
        super("Unrecognized ANSI style '%s'. Ensure it matches a defined Ansi Code, or register a custom mapping first.".formatted(s));
    }
}
