package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.BoxConfiguration;
import io.github.kusoroadeolu.clique.configuration.BoxType;
import io.github.kusoroadeolu.clique.configuration.TextAlign;
import io.github.kusoroadeolu.clique.internal.BorderChars;
import io.github.kusoroadeolu.clique.internal.BoxWrapper;
import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.exceptions.InvalidDimensionException;
import io.github.kusoroadeolu.clique.internal.utils.StringUtils;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.BoxUtils.alignText;
import static io.github.kusoroadeolu.clique.internal.utils.BoxUtils.colorBorderChars;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.NEWLINE;
import static java.util.Objects.requireNonNull;

/**
 * A single-cell bordered container that renders text content within a styled box.
 *
 * <p>Boxes can be sized explicitly via {@link #dimensions(int, int)} or left to
 * autosize based on content. When no dimensions are set, width and height are
 * derived from the longest line and line count of the content respectively.</p>
 *
 * <p>The rendered string is cached after the first call to {@link #get()} and
 * invalidated whenever {@link #content(String)} or {@link #content(String, TextAlign)}
 * is called.</p>
 *
 * <p><b>Note:</b> This class is not thread-safe.</p>
 *
 * @since 1.1.0
 */
@Stable(since = "3.2.0")
public class Box implements Component {
    private int width;
    private int height; //If width and height == 0
    private String content;
    private final BoxConfiguration configuration;
    private TextAlign align = null; //Takes precedence over box config only if not null
    private final BorderChars borderChars;
    private static final String BOX_CONTENT_NOT_NULL = "Box content cannot be null";
    private static final String TEXT_ALIGN_NOT_NULL = "Text align cannot be null";

    public Box(BoxType type, BoxConfiguration configuration) {
        validateTypeAndConfig(type, configuration);
        this.borderChars = BorderChars.from(type);
        this.configuration = configuration;
    }

    void validateTypeAndConfig(BoxType type, BoxConfiguration config) {
        requireNonNull(type, "Box type cannot be null");
        requireNonNull(config, "Box configuration cannot be null");
    }

    public Box(BoxType type) {
        this(type, BoxConfiguration.DEFAULT);
    }

    public Box(BoxConfiguration configuration) {
        this(BoxType.ROUNDED, configuration);
    }

    public Box() {
        this(BoxConfiguration.DEFAULT);
    }

    /**
     * Renders and returns the box as a string.
     *
     * <p>The result is cached after the first invocation and reused on subsequent
     * calls until the content is updated. Text alignment is resolved from the
     * per-call override set via {@link #content(String, TextAlign)} if present,
     * falling back to the value in {@link BoxConfiguration}.</p>
     *
     * @return the rendered box as a string
     */
    public String get() {
        WidthAwareList cells = resolveLines();
        Dimensions dims = resolveDimensions(cells);
        var ta = this.align == null ? configuration.getTextAlign() : this.align;


        final StyleBuilder sb = new StyleBuilder();
        final var chars = colorBorderChars(sb, borderChars, configuration.getBorderColor());
        final BoxWrapper wrapper = new BoxWrapper(
                dims.width(), dims.height(), this.configuration,
                cells.cells(), chars.hLine(), chars.vLine(),
                chars.topLeft(), chars.topRight(), chars.bottomRight(), chars.bottomLeft()
        );

        drawBox(sb, wrapper, ta);
        return sb.toString();
    }


    /**
     * Sets the content of this box and invalidates the render cache.
     *
     * <p>Text alignment is inherited from the {@link BoxConfiguration} provided
     * at construction time.</p>
     *
     * @param content the text content to display; must not be {@code null}
     * @return this box instance
     * @throws NullPointerException if {@code content} is {@code null}
     */
    public Box content(String content) {
        Objects.requireNonNull(content, BOX_CONTENT_NOT_NULL);
        this.content = content;
        align = null;
        return this;
    }

    /**
     * Sets the content and text alignment of this box, invalidating the render cache.
     *
     * <p>The provided {@code align} takes precedence over the alignment specified
     * in the {@link BoxConfiguration}.</p>
     *
     * @param content the text content to display; must not be {@code null}
     * @param align   the text alignment to apply; must not be {@code null}
     * @return this box instance
     * @throws NullPointerException if {@code content} or {@code align} is {@code null}
     */
    public Box content(String content, TextAlign align) {
        Objects.requireNonNull(content, BOX_CONTENT_NOT_NULL);
        Objects.requireNonNull(align, TEXT_ALIGN_NOT_NULL);
        this.content = content;
        this.align = align;
        return this;
    }

    TextAlign textAlign(){
        return this.align;
    }

    /**
     * Sets explicit dimensions for this box.
     *
     * <p>When dimensions are set, content that exceeds the usable inner width
     * (width minus padding) or the specified height will throw an
     * {@link InvalidDimensionException} at render time. If this method is never
     * called, the box auto sizes to fit its content.</p>
     *
     * @param width  the total outer width of the box; must be greater than 0
     * @param height the total outer height of the box; must be greater than 0
     * @return this box instance
     * @throws IllegalArgumentException if {@code width} or {@code height} is not greater than 0
     */
    public Box dimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "Width and height must be greater than 0. To skip dimensions and instead autosize, don't call this method."
            );
        }
        this.height = height;
        this.width = width;
        return this;
    }

    //Splits the box content per newline, maps each chunk to a cell and encompasses them in a list
    WidthAwareList resolveLines(){
        if (content == null || content.isEmpty()) return new WidthAwareList();
        var parser = configuration.getParser();
        var cellList = content.lines()
                .map(s -> StringUtils.parseToCell(s, parser))
                .toList();
        return new WidthAwareList(cellList);
    }

    private record Dimensions(int width, int height) {}


    private Dimensions resolveDimensions(WidthAwareList cells) {
        int padding = configuration.getPadding();

        // width/height are only ever set by dimensions(), so 0/0 means autosize
        if (width == 0 && height == 0) {
            return new Dimensions(cells.longest() + (padding * 2), cells.size());
        }

        int usableWidth = width - (padding * 2);
        int contentWidth = cells.longest();
        int contentHeight = cells.size();

        if (contentWidth > usableWidth) throw new InvalidDimensionException("Content overflows: content is %s wide but usable inner width is only %s".formatted(contentWidth, usableWidth));
        if (contentHeight > height) throw new InvalidDimensionException("Content overflows: %s lines of content cannot fit in a box of height %s".formatted(contentHeight, height));

        return new Dimensions(width, height);
    }

     static void drawBox(StyleBuilder sb, BoxWrapper boxWrapper, TextAlign textAlign) {
        final int width = boxWrapper.width();
        final int padding = boxWrapper.configuration().getPadding();
        final String spaces = BLANK.repeat(width);
        final String hLines = boxWrapper.hLine().repeat(width);

        sb.append(boxWrapper.tLeft()).append(hLines).append(boxWrapper.tRight()).append(NEWLINE);

        int startLine = 0;
        final int textHeight = boxWrapper.wordWrap().size();
        final int availableLines = boxWrapper.height();

        if (textAlign == TextAlign.CENTER || textAlign == TextAlign.CENTER_LEFT || textAlign == TextAlign.CENTER_RIGHT) {
            startLine = (availableLines - textHeight) / 2; //Adding one to this like the others makes the vertical alignment(the top half, hog 1 more space than needed, starving the bottom half)
        } else if (textAlign == TextAlign.BOTTOM_LEFT || textAlign == TextAlign.BOTTOM_CENTER || textAlign == TextAlign.BOTTOM_RIGHT) {
            startLine = (availableLines - textHeight);
        }

        for (int i = 0; i < boxWrapper.height(); i++) {
            if (i >= startLine && i < (startLine + textHeight)) {
                int textIndex = i - startLine;
                alignText(sb, textIndex, textAlign, spaces, boxWrapper.wordWrap(), boxWrapper.vLine(), padding);
            } else {
                sb.append(boxWrapper.vLine()).append(spaces).append(boxWrapper.vLine()).append(NEWLINE);
            }
        }

        sb.append(boxWrapper.bLeft()).append(hLines).append(boxWrapper.bRight());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;
        return width == box.width && height == box.height && Objects.equals(content, box.content) && Objects.equals(configuration, box.configuration) && align == box.align && Objects.equals(borderChars, box.borderChars);
    }

    @Override
    public int hashCode() {
       return Objects.hash(width, height, content, configuration, align, borderChars);
    }

    @Override
    public String toString() {
        return "Box[" +
                "width=" + width +
                ", height=" + height +
                ", content='" + content + '\'' +
                ", configuration=" + configuration +
                ", align=" + align +
                ", borderChars=" + borderChars +
                ']';
    }



}