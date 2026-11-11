package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;

import java.util.Objects;

@InternalApi(since = "3.2.0")

//"
public class RGBColor implements RGBAnsiCode {
    private final int red;
    private final int green;
    private final int blue;
    private final boolean isBackground;
    private final String ansiSequence;

    public RGBColor(int red, int green, int blue, boolean isBackground) {
        if (red < 0 || red > 255) throw new IllegalArgumentException("Red must be 0-255");
        if (green < 0 || green > 255) throw new IllegalArgumentException("Green must be 0-255");
        if (blue < 0 || blue > 255) throw new IllegalArgumentException("Blue must be 0-255");
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.isBackground = isBackground;
        int type = isBackground ? 48 : 38;
        ansiSequence = "\u001B[%d;2;%d;%d;%dm".formatted(type, red, green, blue);
    }

    public RGBColor(int red, int green, int blue) {
        this(red, green, blue, false);
    }

    public String ansiSequence() {
        return ansiSequence;
    }

    public int red() {
        return red;
    }

    public int green() {
        return green;
    }

    public int blue() {
        return blue;
    }

    public boolean isBackground() {
        return isBackground;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RGBColor rgbColor = (RGBColor) o;
        return red == rgbColor.red && green == rgbColor.green && blue == rgbColor.blue && isBackground == rgbColor.isBackground && Objects.equals(ansiSequence, rgbColor.ansiSequence);
    }

    @Override
    public int hashCode() {
        int result = red;
        result = 31 * result + green;
        result = 31 * result + blue;
        result = 31 * result + Boolean.hashCode(isBackground);
        result = 31 * result + Objects.hashCode(ansiSequence);
        return result;
    }
}
