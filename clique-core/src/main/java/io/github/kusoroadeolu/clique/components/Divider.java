package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.DividerConfiguration;
import io.github.kusoroadeolu.clique.configuration.TitleAlign;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.CharWidth;
import io.github.kusoroadeolu.clique.internal.utils.MiscUtils;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;

/**
 * A horizontal divider line that can optionally display a title.
 * <p>
 * Divider renders a horizontal line of configurable width. When a title is
 * set via {@link #title(String)}, the title is centered within the divider line.
 *
 * @since 4.0.2
 */
@Stable(since = "4.0.2")
public class Divider implements Component {

    private final DividerConfiguration configuration;
    private final int width;
    private String title;
    private TitleAlign titleAlign = TitleAlign.CENTER;

    public Divider(int width, DividerConfiguration configuration) {
        if (width <= 0) throw new IllegalArgumentException("Width must be greater than 0.");
        this.width = width;
        this.configuration = Objects.requireNonNull(configuration, "Divider configuration cannot be null");
    }

    @Override
    public String get() {
        String dividerChar = String.valueOf(configuration.getDividerChar());
        AnsiCode[] color = configuration.getDividerColor();

        if (title == null || title.isEmpty()) {
            return new StyleBuilder()
                    .appendAndReset(dividerChar.repeat(width), color)
                    .toString();
        }

        String content = BLANK + MiscUtils.parse(title, configuration.getParser()) + BLANK;
        // title() guarantees remaining >= 1
        int remaining = width - visibleWidth(title) - 2;

        int left = switch (titleAlign) {
            case LEFT -> 1;
            case RIGHT -> remaining - 1;
            case CENTER -> remaining / 2;
        };

        return new StyleBuilder()
                .appendAndReset(dividerChar.repeat(left), color)
                .appendAndReset(content)
                .appendAndReset(dividerChar.repeat(remaining - left), color)
                .toString();
    }

    /**
     * Sets the title, centered within the divider line.
     *
     * @param title the title to display; markup is resolved with the configured parser
     * @return this divider
     * @throws NullPointerException     if {@code title} is {@code null}
     * @throws IllegalArgumentException if the title's visible width plus 2 (for the
     *                                  surrounding spaces) is not less than the divider width
     */
    public Divider title(String title) {
        return title(title, TitleAlign.CENTER);
    }

    /**
     * Sets the title and where it sits on the line.
     *
     * @param title      the title to display; markup is resolved with the configured parser
     * @param titleAlign where the title sits on the line
     * @return this divider
     * @throws NullPointerException     if {@code title} or {@code titleAlign} is {@code null}
     * @throws IllegalArgumentException if the title's visible width plus 2 (for the
     *                                  surrounding spaces) is not less than the divider width
     */
    public Divider title(String title, TitleAlign titleAlign) {
        Objects.requireNonNull(title, "Divider title cannot be null");
        Objects.requireNonNull(titleAlign, "Title align cannot be null");

        if (visibleWidth(title) + 2 >= width) {
            throw new IllegalArgumentException("Title's visible width plus padding must be less than divider width.");
        }

        this.title = title;
        this.titleAlign = titleAlign;
        return this;
    }

    private int visibleWidth(String text) {
        return CharWidth.of(configuration.getParser().getOriginalString(text));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Divider that = (Divider) o;
        return width == that.width
                && titleAlign == that.titleAlign
                && Objects.equals(configuration, that.configuration)
                && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, configuration, title, titleAlign);
    }

    @Override
    public String toString() {
        return "Divider[configuration=" + configuration
                + ", title='" + title + '\''
                + ", titleAlign=" + titleAlign
                + ", width=" + width + ']';
    }
}