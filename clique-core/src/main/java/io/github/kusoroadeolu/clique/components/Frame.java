package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.BoxType;
import io.github.kusoroadeolu.clique.configuration.FrameAlign;
import io.github.kusoroadeolu.clique.configuration.FrameConfiguration;
import io.github.kusoroadeolu.clique.configuration.TitleAlign;
import io.github.kusoroadeolu.clique.internal.BorderChars;
import io.github.kusoroadeolu.clique.internal.Cell;
import io.github.kusoroadeolu.clique.internal.FrameNode;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.exception.InvalidDimensionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.BoxUtils.applyAnsiToBorders;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.*;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.parseToCell;
import static io.github.kusoroadeolu.clique.style.StyleCode.RESET;

/**
 * A bordered, optionally titled container that renders its content as a formatted
 * terminal box. Frames may contain strings or nested {@link Component} instances,
 * each independently aligned within the frame.
 *
 * <p>Width resolution order:
 * <ol>
 *   <li>If {@link #width(int)} has been set, that value is used as the total inner width.</li>
 *   <li>Otherwise, the width is derived from the widest nested node, plus padding on each side.
 *       If a title is present and wider than the computed content width, the title width takes
 *       precedence.</li>
 * </ol>
 *
 * <p>Content width must not exceed {@code frameWidth - (padding * 2)}; if it does,
 * {@link io.github.kusoroadeolu.clique.internal.exception.InvalidDimensionException} is thrown
 * at render time.
 *
 * <p>This class is <b>not thread-safe</b>.
 * */
@Stable(since = "3.2.0")
public class Frame implements Component {
    private final List<FrameNode> nodes;
    private final FrameConfiguration configuration;
    private final BoxType type;
    private String title;
    private TitleAlign titleAlign;
    private int width;
    private final BorderChars borderChars;
    private static final int NO_WIDTH_SET = 0;
    private static final String NULL_FRAME_ALIGN = "Frame align cannot be null";

    public Frame(FrameConfiguration configuration, BoxType type) {
        this.nodes = new ArrayList<>();
        this.configuration = Objects.requireNonNull(configuration, "Configuration cannot be null");
        this.type = Objects.requireNonNull(type, "Frame type cannot be null");
        this.title = EMPTY;
        this.titleAlign = TitleAlign.CENTER;
        this.width = NO_WIDTH_SET;
        borderChars = BorderChars.from(type);
        this.colorBorders(borderChars);
    }

    public Frame() {
        this(FrameConfiguration.DEFAULT, BoxType.ROUNDED);
    }

    public Frame(BoxType type) {
        this(FrameConfiguration.DEFAULT, type);
    }

    public Frame(FrameConfiguration configuration) {
        this(configuration, BoxType.ROUNDED);
    }

    /**
     * Sets the frame title and its horizontal alignment within the top border.
     *
     * <p>The title is rendered inline with the top border line. If a color parser is configured,
     * a reset sequence is automatically appended to prevent color bleed into the border.
     *
     * @param title     the title text; must not be {@code null}
     * @param titleAlign the horizontal placement of the title; must not be {@code null}
     * @return this frame, for chaining
     * @throws NullPointerException if {@code title} or {@code titleAlign} is {@code null}
     * @throws InvalidDimensionException if the title is wider than the resolved frame width, thrown at render time via {@link #get()}
     */
    public Frame title(String title, TitleAlign titleAlign) {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(titleAlign, NULL_FRAME_ALIGN);
        this.title = title;
        this.titleAlign = titleAlign;
        return this;
    }

    /**
     * @deprecated use {@link #title(String, TitleAlign)} instead
     */
    @Deprecated
    public Frame title(String title, FrameAlign titleAlign) {
        Objects.requireNonNull(titleAlign, NULL_FRAME_ALIGN);

        return title(title, switch (titleAlign) {
            case LEFT -> TitleAlign.LEFT;
            case RIGHT -> TitleAlign.RIGHT;
            case CENTER -> TitleAlign.CENTER;
        });
    }

    /**
     * Sets the frame title with center alignment.
     *
     * <p>Equivalent to {@code title(title, TitleAlign.CENTER)}.
     *
     * @param title the title text; must not be {@code null}
     * @return this frame, for chaining
     * @throws NullPointerException if {@code title} is {@code null}
     */
    public Frame title(String title) {
        return title(title, TitleAlign.CENTER);
    }

    /**
     * Sets the total inner width of the frame in characters, excluding the border columns.
     *
     * <p>When set, this width overrides auto-sizing. The available content width is
     * {@code width - (padding * 2)}. Nested node widths must not exceed this value;
     * violations are detected at render time via {@link #get()}.
     *
     * @param width the total inner width; must be greater than zero
     * @return this frame, for chaining
     * @throws InvalidDimensionException if {@code width} is less than or equal to zero
     */
    public Frame width(int width) {
        if (width <= 0) throw new InvalidDimensionException(
                "Frame width must be greater than zero, got: %d".formatted(width)
        );

        this.width = width;
        return this;
    }

    /**
     * Appends a string node to this frame using the alignment configured in
     * {@link FrameConfiguration}.
     *
     * <p>Equivalent to {@code nest(str, configuration.getFrameAlign())}.
     *
     * @param str the string to nest; must not be {@code null}
     * @return this frame, for chaining
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public Frame nest(String str) {
        return nest(str, configuration.getFrameAlign());
    }


    /**
     * Appends a string node to this frame with the specified alignment.
     *
     * @param str   the string to nest; must not be {@code null}
     * @param align the horizontal alignment of the string within the frame; must not be {@code null}
     * @return this frame, for chaining
     * @throws NullPointerException if {@code str} or {@code align} is {@code null}
     */
    public Frame nest(String str, FrameAlign align) {
        Objects.requireNonNull(str, "String cannot be null");
        Objects.requireNonNull(align, NULL_FRAME_ALIGN);
        if (configuration.getParser() != null) str = str + RESET;
        nodes.add(new FrameNode.StringNode(str, align, configuration.getParser()));
        return this;
    }

    /**
     * Appends a {@link Component} node to this frame using the alignment configured in
     * {@link FrameConfiguration}.
     *
     * <p>Equivalent to {@code nest(component, configuration.getFrameAlign())}.
     *
     * @param component the component to nest; must not be {@code null} and must not be this frame
     * @return this frame, for chaining
     * @throws NullPointerException     if {@code component} is {@code null}
     * @throws IllegalArgumentException if {@code component} is this frame instance
     */
    public Frame nest(Component component) {
        return nest(component, configuration.getFrameAlign());
    }

    /**
     * Appends a {@link Component} node to this frame with the specified alignment.
     *
     * <p>A frame cannot nest itself. Attempting to do so throws {@link IllegalArgumentException}.
     * Circular nesting through intermediate components is not detected and results in
     * a {@link StackOverflowError} at render time.
     *
     * @param component the component to nest; must not be {@code null} and must not be this frame
     * @param align     the horizontal alignment within the frame; must not be {@code null}
     * @return this frame, for chaining
     * @throws NullPointerException     if {@code component} or {@code align} is {@code null}
     * @throws IllegalArgumentException if {@code component} is this frame instance
     */
    public Frame nest(Component component, FrameAlign align) {
        Objects.requireNonNull(component, "Component cannot be null");
        Objects.requireNonNull(align, NULL_FRAME_ALIGN);
        assertNotSelf(component);
        nodes.add(new FrameNode.ComponentNode(component, align));
        return this;
    }

    void assertNotSelf(Component component){
        if (component == this) throw new IllegalArgumentException("Cannot nest a Frame in itself");
    }

    /**
     * Builds the frame and all its nested nodes to a string.
     *
     * <p>Width resolution, title placement, content alignment, and border drawing all
     * occur during this call. No state is cached; each invocation performs a full render.
     *
     * @return the fully rendered frame as a multi-line string, terminated with a newline
     * @throws InvalidDimensionException
     *         if the title width exceeds the resolved frame width, or if any nested node's
     *         content width exceeds the available content area
     */
    public String get() {
        var appendedTitle = title;

        var parser = configuration.getParser();
        if (!title.isEmpty()) appendedTitle = title + RESET; //Add a reset flag to prevent title colors from bleeding


        var parsedTitle = parseToCell(appendedTitle, parser);

        //Note that we align the title width by +1 or -1 based on if the frame align is left or right, so to prevent an issue where the title width is left and the frame size = title size, we add one to the width to make up for the by one offset
        int titleWidth = parsedTitle.width() + 2;
        int nodesMaxWidth = findNodesMaxWidth(); //Max content width

        int givenWidth = (noWidthSet() ? nodesMaxWidth + (configuration.getPadding() * 2) : this.width);

        if (noWidthSet() && !parsedTitle.isEmpty()) {
            givenWidth = Math.max(givenWidth, titleWidth + 1);
        }

        int availableWidth = givenWidth - (configuration.getPadding() * 2);

        if (!parsedTitle.isEmpty()) validateTitleWidth(titleWidth, givenWidth);

        if (nodesMaxWidth > availableWidth) {
            throw new InvalidDimensionException(
                    "Content width (%d) exceeds available frame width (%d). Either increase frame width to at least %d or reduce content size."
                            .formatted(nodesMaxWidth, availableWidth, nodesMaxWidth + (configuration.getPadding() * 2))
            );
        }

        var sb = new StringBuilder();
        appendTitleToBox(parsedTitle, givenWidth, titleWidth, sb); //Using given width, not available width here, since avail width is meant for content not title

        for (FrameNode node : nodes) {
            align(node, availableWidth, sb);
        }

        sb.append(borderChars.bottomLeft())
                .append(borderChars.hLine().repeat(givenWidth))
                .append(borderChars.bottomRight())
                .append(NEWLINE);

        return sb.toString();
    }

    //Given width = well the given width, avail width = width - (padding * 2), in the case of no width set, the avail width = max node width, given width = (width) + (padding * 2)
    void align(FrameNode node ,int availableWidth, StringBuilder sb) {
        var borderChar = this.borderChars;
        var padding = configuration.getPadding();
        int rem = Math.max(ZERO, availableWidth - node.maxWidth());

        String fixed = BLANK.repeat(padding);
        for (var line : node.lines()) {
            int lineWidth = line.width();
            String content = line.styledText();
            switch (node.align()){
                case RIGHT -> sb.append(borderChar.vLine())
                        .append(fixed)
                        .repeat(BLANK,  rem) //We append the remaining width to align right
                        .append(content)
                        .append(fixed)
                        .repeat(BLANK, Math.max(ZERO, availableWidth - lineWidth - rem))
                        .append(borderChar.vLine())
                        .append(NEWLINE);

                case LEFT -> sb.append(borderChar.vLine())
                    .append(fixed)
                    .append(content)
                    .repeat(BLANK, Math.max(ZERO, availableWidth - lineWidth)) //Just append the remaining space here, we don't need rem here
                    .append(fixed)
                    .append(borderChar.vLine())
                    .append(NEWLINE);

                case CENTER -> {
                    int leftPad = rem / 2;
                    int rightPad = (availableWidth - lineWidth) - leftPad;

                    sb.append(borderChar.vLine())
                            .append(fixed)
                            .repeat(BLANK, leftPad) //We need to align everything at a common place
                            .append(content)
                            .repeat(BLANK, rightPad)
                            .append(fixed)
                            .append(borderChar.vLine())
                            .append(NEWLINE);
                }
            }
        }
    }

    void appendTitleToBox(Cell parsedTitle, int givenWidth, int titleWidth, StringBuilder sb){
        if (!parsedTitle.isEmpty()) {
            int leftWidth = findTitleBlockOffset(givenWidth, titleWidth, titleAlign);
            sb.append(borderChars.topLeft())
                    .append(borderChars.hLine().repeat(leftWidth))
                    .append(BLANK).append(parsedTitle.styledText()).append(BLANK)
                    //20 - 20 - 1, Gives - 1 For left width
                    //20 - 20

                    .append(borderChars.hLine().repeat(givenWidth - titleWidth - leftWidth))
                    .append(borderChars.topRight())
                    .append(NEWLINE);
        } else {
            sb.append(borderChars.topLeft())
                    .append(borderChars.hLine().repeat(givenWidth))
                    .append(borderChars.topRight())
                    .append(NEWLINE);
        }
    }

    int findNodesMaxWidth() {
        return nodes.stream()
                .mapToInt(FrameNode::maxWidth)
                .max()
                .orElse(ZERO);
    }

    static int findTitleBlockOffset(int givenWidth, int titleWidth, TitleAlign align) {
        return switch (align) {
            case LEFT -> 1;
            case RIGHT -> (givenWidth - titleWidth) - 1; //20 - 20 - 1, Gives  -1, By adding 1 to the title width, we get
            case CENTER -> (givenWidth - titleWidth) / 2;
        };
    }

    boolean noWidthSet(){
        return this.width == NO_WIDTH_SET;
    }

    void validateTitleWidth(int titleWidth, int resolvedWidth) {
        if (titleWidth > resolvedWidth)
            throw new InvalidDimensionException(
                    "Title width (%d) exceeds frame width (%d). Increase frame width to at least %d or shorten the title."
                            .formatted(titleWidth, resolvedWidth, titleWidth)
            );
    }

    void colorBorders(BorderChars borderChar) {
        if (configuration.getBorderColor() != null) {
            applyAnsiToBorders(borderChar, configuration.getBorderColor());
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        Frame frame = (Frame) object;
        return width == frame.width && Objects.equals(nodes, frame.nodes) && Objects.equals(configuration, frame.configuration) && type == frame.type && Objects.equals(title, frame.title) && titleAlign == frame.titleAlign && Objects.equals(borderChars, frame.borderChars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, nodes, configuration, type, title, titleAlign, borderChars);
    }

    @Override
    public String toString() {
        return "Frame[" +
                "borderChars=" + borderChars +
                ", width=" + width +
                ", titleAlign=" + titleAlign +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", configuration=" + configuration +
                ", nodes=" + nodes +
                ']';
    }
}
