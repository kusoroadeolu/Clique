package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.internal.utils.Constants;
import io.github.kusoroadeolu.clique.internal.utils.MiscUtils;
import io.github.kusoroadeolu.clique.spi.RGBAnsiCode;
import io.github.kusoroadeolu.clique.style.StyleCode;

public record Gradient(RGBAnsiCode from, RGBAnsiCode to) {

    public Gradient(String fromHex, String toHex) {
        this(MiscUtils.hex(fromHex), MiscUtils.hex(toHex));
    }

    public String apply(String text) {
        int visible = countVisible(text);
        int d = Math.max(visible - 1, 1);

        int colorIdx = 0;
        int i = 0;
        boolean inEscape = false;
        StringBuilder sb = new StringBuilder();

        while (i < text.length()) {
            int cp = text.codePointAt(i);
            int len = Character.charCount(cp);

            if (cp == Constants.ESC && MiscUtils.nextCharEquals(text, i + 1, Constants.LBRACKET)) {
                inEscape = true;
                sb.appendCodePoint(cp);
            } else if (inEscape) {
                sb.appendCodePoint(cp);
                if (cp == Constants.ANSI_END) inEscape = false;
            } else {
                double t = (double) colorIdx / d;
                int r = (int) (from.red() + t * (to.red() - from.red()));
                int g = (int) (from.green() + t * (to.green() - from.green()));
                int b = (int) (from.blue() + t * (to.blue() - from.blue()));

                sb.append(new RGBColor(r, g, b).ansiSequence()).appendCodePoint(cp);
                colorIdx++;
            }

            i += len;
        }

        return sb.append(StyleCode.RESET).toString();
    }

    private int countVisible(String text) {
        int count = 0;
        int i = 0;
        boolean inEscape = false;

        while (i < text.length()) {
            int cp = text.codePointAt(i);
            if (cp == Constants.ESC && MiscUtils.nextCharEquals(text, i + 1, Constants.LBRACKET)) {
                inEscape = true;
            } else if (inEscape) {
                if (cp == Constants.ANSI_END) inEscape = false;
            } else {
                count++;
            }
            i += Character.charCount(cp);
        }
        return count;
    }
}
