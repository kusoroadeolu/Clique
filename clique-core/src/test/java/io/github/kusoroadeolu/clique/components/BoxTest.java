package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.BoxConfiguration;
import io.github.kusoroadeolu.clique.configuration.BoxType;
import io.github.kusoroadeolu.clique.configuration.TextAlign;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoxTest {
    @Test
    void testBoxWidth() {
        var box = new Box()
                .dimensions(50, 9)
                .content("Test", TextAlign.TOP_CENTER);
        var output = MarkupParser.DEFAULT.getOriginalString(box.get());
        var lines = output.lines().toList();
        assertEquals(52, lines.getFirst().length()); // Width + borders 50 + 2 borders
    }

    @Test
    void testBoxHeight(){
        var box = new Box()
                .dimensions(50, 9)
                .content("Test");
        var content = box.get();
        assertEquals(11, content.lines().toList().size()); //9 plus 2 borders
    }

    @Test
    void assertThrows_whenWidthIsZero(){
        Box box = new Box();
        assertThrows(IllegalArgumentException.class, () -> box.dimensions(0, 10));
    }

    @Test
    void assertThrows_whenWidthIsNegative(){
        Box box = new Box();
        assertThrows(IllegalArgumentException.class,
                () -> box.dimensions(-1, 10));
    }

    @Test
    void assertThrows_whenHeightIsNegative(){
        Box box = new Box();
        assertThrows(IllegalArgumentException.class,
                () -> box.dimensions(10, -1));
    }

    @Test
    void assertThrows_whenHeightIsZero(){
        Box box = new Box();
        assertThrows(IllegalArgumentException.class,
                () -> box.dimensions(10, 0));
    }


    //Simple regression test
    @Test
    void assertDoesNotThrow_whenNoWidthSet(){
        assertDoesNotThrow(() -> new Box());
    }


    //TESTS FOR API CHANGES
    @Test
    void alignTopLeft_shouldBeShifted_toLeft(){
        var box = new Box()
                .dimensions(50, 9)
                .content("Test", TextAlign.TOP_LEFT);
        var lines = MarkupParser.DEFAULT.getOriginalString(box.get()).lines().toList(); //Strip resets
        String second  = lines.get(1); //So it should be border + padding(2), so 3 substring before we get our content
        String firstChar = second.substring(3, 4);
        assertEquals("T", firstChar);
    }


    @Test
    void alignTopRight_shouldBeShifted_toRight(){
        var box = new Box()
                .dimensions(50, 9)
                .content("Test", TextAlign.TOP_RIGHT);
        var lines = MarkupParser.DEFAULT.getOriginalString(box.get()).lines().toList(); //Strip resets
        String second  = lines.get(1);
        String firstChar = second.substring(second.length() - 7, second.length() - 6);
        assertEquals("T", firstChar);

    }

    @Test
    void textAlign_whenSet_shouldUseGivenAlign(){
        var box = new Box(BoxConfiguration.builder().textAlign(TextAlign.TOP_LEFT).build())
                .dimensions(50, 9)
                .content("Test", TextAlign.TOP_RIGHT);
        assertEquals(TextAlign.TOP_RIGHT, box.textAlign());
    }


    @Test
    void textAlign_shouldBeReassigned_onSubsequentAlignCalls(){
        var box = new Box().content("Test", TextAlign.TOP_LEFT);

        // Assert Top left
        assertEquals(TextAlign.TOP_LEFT, box.textAlign());

    }

    @Test void autosizeFollowsContentChanges() {
        Box box = new Box().content("short");
        box.get();
        assertDoesNotThrow(() -> box.content("a much longer line of text\nand a second line").get());

        String big = box.get();
        String small = box.content("hi").get();
        assertTrue(small.length() < big.length());
    }

    @Test
    void content_withoutAlign_clearsPreviousOverride() {
        var box = new Box().content("a", TextAlign.TOP_LEFT);
        box.content("b");
        assertNull(box.textAlign());
    }

    @Test
    void content_withoutAlign_fallsBackToConfigAlignment() {
        var box = new Box(BoxConfiguration.builder().textAlign(TextAlign.TOP_RIGHT).build())
                .dimensions(50, 9);

        box.content("Test", TextAlign.TOP_LEFT);
        box.content("Test");

        var lines = MarkupParser.DEFAULT.getOriginalString(box.get()).lines().toList();
        String second = lines.get(1);
        // TOP_RIGHT from config: T sits 7 chars from the end, same offset as alignTopRight test
        assertEquals("T", second.substring(second.length() - 7, second.length() - 6));
    }

    @Test
    void content_withAlign_subsequentCall_reassigns() {
        var box = new Box().content("Test", TextAlign.TOP_LEFT);
        box.content("Test", TextAlign.TOP_RIGHT);
        assertEquals(TextAlign.TOP_RIGHT, box.textAlign());
    }
}