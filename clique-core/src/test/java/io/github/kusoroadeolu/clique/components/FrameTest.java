package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.FrameAlign;
import io.github.kusoroadeolu.clique.configuration.TitleAlign;
import io.github.kusoroadeolu.clique.internal.exception.InvalidDimensionException;
import io.github.kusoroadeolu.clique.internal.utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.NEWLINE;
import static org.junit.jupiter.api.Assertions.*;

class FrameTest {

    private Frame sharedFrame;

    @BeforeEach
    void setup(){
        this.sharedFrame = new Frame();
    }

    private static Component component(String output) {
        return () -> output;
    }

    private static String[] lines(String rendered) {
        return rendered.split(NEWLINE);
    }

    private static String stripAnsi(String s) {
        return StringUtils.stripAnsi(s);
    }


    @Test
    void throwsWhenTitleWiderThanFrameAtRenderTime() {
        sharedFrame.width(5).title("This title is way too long");
        assertThrows(InvalidDimensionException.class, () -> sharedFrame.get());
    }

    @Test
    void throwsWhenNodeContentWiderThanExplicitWidth() {
        sharedFrame.width(3).nest("this string is too wide");
        assertThrows(InvalidDimensionException.class, () ->
                sharedFrame.get()
        );
    }

    @Test
    void throwsWhenWidthIsNegative() {
        assertThrows(InvalidDimensionException.class, () ->
                sharedFrame.width(-1)
        );
    }

    @Test
    void throwsWhenSelfNested(){
        assertThrows(IllegalArgumentException.class, () ->
                sharedFrame.nest(sharedFrame)
        );
    }

    // -------------------------
    // Title rendering tests
    // -------------------------

    @Test
    void frameWithNoTitleHasPlainTopBorder() {
        String rendered = stripAnsi(
                sharedFrame.nest("hello")
                        .get()
        );
        String topLine = lines(rendered)[0];
        // Top border should have no spaces, since no title has been embedded
        assertFalse(topLine.contains(" "));
    }

    @Test
    void titleIsSurroundedBySpacesInTopBorder() {
        String rendered = stripAnsi(
                sharedFrame.title("T")
                        .nest("some content")
                        .get()
        );
        String topLine = lines(rendered)[0];
        assertTrue(topLine.contains(" T "));
    }

    @Test
    void topAndBottomBorderHaveSameWidth() {
        String rendered = stripAnsi(
                sharedFrame.title("Title")
                        .nest("hello world")
                        .get()
        );
        String[] ls = lines(rendered);
        assertEquals(ls[0].length(), ls[ls.length - 1].length());
    }

    @Test
    void titleAlignPositionsTitleCorrectly() {
        String left = stripAnsi(
                new Frame()
                        .width(20)
                        .title("Title", TitleAlign.LEFT)
                        .nest("content")
                        .get()
        );

        String center = stripAnsi(
                new Frame()
                        .width(20)
                        .title("Title", TitleAlign.CENTER)
                        .nest("content")
                        .get()
        );

        String right = stripAnsi(
                new Frame()
                        .width(20)
                        .title("Title", TitleAlign.RIGHT)
                        .nest("content")
                        .get()
        );

        String leftTop = lines(left)[0];
        String centerTop = lines(center)[0];
        String rightTop = lines(right)[0];

        assertTrue(leftTop.indexOf(" Title ") < centerTop.indexOf(" Title "));
        assertTrue(centerTop.indexOf(" Title ") < rightTop.indexOf(" Title "));
    }

    @SuppressWarnings("deprecation")
    @Test
    void deprecatedFrameAlignTitleStillWorks() {
        Frame frame = new Frame();
        assertDoesNotThrow(() ->
                frame.nest("Hello")
                        .title("Title", FrameAlign.LEFT)
                        .get()
        );
    }

    // -------------------------
    // Nested component tests
    // -------------------------

    @Test
    void nestedComponentOutputAppearsInFrame() {
        Component comp = component("foo");
        String rendered = stripAnsi(
                sharedFrame.nest(comp)
                        .get()
        );
        assertTrue(rendered.contains("foo"));
    }

    @Test
    void allContentLinesHaveSameWidth() {
        String rendered = stripAnsi(
                sharedFrame.nest("short", FrameAlign.LEFT)
                        .width(28)
                        .nest("a much longer line")
                        .get()
        );
        String[] ls = lines(rendered);
        int expectedWidth = ls[0].length();
        for (String line : ls) {
            assertEquals(expectedWidth, line.length(), "Line width mismatch: [" + line + "]");
        }
    }

    @Test
    void frameWidthDerivedFromWidestNode() {
        String wide = "a much longer string";
        String narrow = "hi";
        String rendered = stripAnsi(
                sharedFrame.nest(wide)
                        .nest(narrow)
                        .get()
        );
        // Every line should be the same width, driven by the wider content
        String[] ls = lines(rendered);
        int expectedWidth = ls[0].length();
        for (String line : ls) {
            assertEquals(expectedWidth, line.length());
        }
    }

    @Test
    void nestedComponentWithMultilineOutput() {
        Component comp = component("line one\nline two\nline three");
        String rendered = stripAnsi(
                sharedFrame.nest(comp)
                        .get()
        );
        assertTrue(rendered.contains("line one"));
        assertTrue(rendered.contains("line two"));
        assertTrue(rendered.contains("line three"));
    }

    @Test
    void leftAlignedNodeIsPaddedToLeftBorder() {
        String rendered = stripAnsi(
                sharedFrame.nest("hi", FrameAlign.LEFT)
                        .nest("much wider content here")
                        .get()
        );
        // Line with "hi" should have it immediately after the vline with no leading spaces
        String hiLine = Arrays.stream(lines(rendered))
                .filter(l -> l.contains("hi") && !l.contains("much"))
                .findFirst()
                .get();

        // after the vline char, next char should be 'h'
        assertEquals('h', hiLine.charAt(3));
    }

    @Test
    void test_frameHeight(){
        String str = new Frame()
                .nest("Hello")
                .get();

        assertEquals(3, str.lines().toList().size());
    }


    @Test
    void test_autosizeFrame_whenTitleWidth_greaterThanFrameContent_andAlignedLeft(){
        Frame frame = new Frame();
        frame.nest("Hello").title(" ".repeat(20), TitleAlign.LEFT);
        assertDoesNotThrow(frame::get);
    }

    @Test
    void test_autosizeFrame_whenTitleWidth_greaterThanFrameContent_andAlignedRight(){
        Frame frame = new Frame();
        frame.nest("Hello").title(" ".repeat(20), TitleAlign.RIGHT);
        assertDoesNotThrow(frame::get);
    }

    @Test
    void test_autosizeFrame_whenTitleWidth_greaterThanFrameContent_andAlignedCenter(){
        Frame frame = new Frame();
        frame.nest("Hello").title(" ".repeat(20), TitleAlign.CENTER);
        assertDoesNotThrow(frame::get);
    }

    @Test
    void test_givenWidthFrame_whenTitleWidth_equalToContent_andAlignedLeft(){
        Frame frame = new Frame();
        frame.nest("Hello").title(" ".repeat(5), TitleAlign.LEFT).width(9);
        assertDoesNotThrow(frame::get);
    }
}
