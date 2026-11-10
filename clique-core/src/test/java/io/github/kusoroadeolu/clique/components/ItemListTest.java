package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.ItemListConfiguration;
import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.exceptions.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.style.ColorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemListTest {
    @Test
    void assertThrow_onSelfNest(){
        var list = new ItemList();
        assertThrows(IllegalArgumentException.class, () -> list.item("", "", list));
    }

    @Test
    void assert_onAdd_containsItem(){
        var list = new ItemList();
        String s = list.item("->", "Content").get();
        assertTrue(s.contains("Content"));
    }

    @Test
    void nestingAncestorThrows() {
        ItemList a = new ItemList();
        ItemList b = new ItemList();
        a.item("-", "child", b);
        assertThrows(IllegalArgumentException.class, () -> b.item("-", "loop", a));
    }

    @Test
    void test_indentSpacing(){
        var list = new ItemList();
        String s = list.item("->", "Content", new ItemList().item("<-", "Content 2")).get();
        var lines = s.lines().toList();
        assertEquals(lines.get(1).substring(0, 2), " ".repeat(2)); //1 depth * default 2 indent level
    }

    @Test
    void test_symbolSpacing(){
        var list = new ItemList();
        String s = list.item("->", "Content").get();
        String sub = s.substring(2);
        assertTrue(sub.startsWith(" "));
    }


    @Test
    void assertConfigurationCoalesces(){
        var config = ItemListConfiguration.builder().indentSize(1).build();
        var list = new ItemList(config);
        list.item("->", "Content", new ItemList().item("<-", "Content 2"));
        var child = list.child();
        assertSame(child.configuration(), config);
    }

    private static final MarkupParser STRICT =
            new MarkupParser(ParserConfiguration.builder().enableStrictParsing().build());

    private static ItemList strictList() {
        return new ItemList(ItemListConfiguration.builder().parser(STRICT).build());
    }

    // RED before fix
    @Test
    void strayBracketInOneItemDoesNotBreakLaterItems() {
        ItemList list = new ItemList()
                .item("-", "arr[0")
                .item("-", "[green]ok[/]");

        String out = list.get();

        assertFalse(out.contains("[green]"), "tag should have been parsed, got: " + out);
        assertTrue(out.contains(ColorCode.GREEN.ansiSequence()));
        assertTrue(out.contains("arr[0"));
    }

    // RED before fix: a stray [ in the symbol used to eat the tag in the content
    @Test
    void strayBracketInSymbolDoesNotBreakContent() {
        ItemList list = new ItemList().item("[", "[green]ok[/]");

        String out = list.get();

        assertFalse(out.contains("[green]"));
        assertTrue(out.contains(ColorCode.GREEN.ansiSequence()));
    }

    // RED before fix: poison crosses into the sublist
    @Test
    void strayBracketInParentDoesNotBreakSublist() {
        ItemList list = new ItemList()
                .item("-", "arr[0", new ItemList().item("-", "[green]ok[/]"));

        String out = list.get();

        assertFalse(out.contains("[green]"));
        assertTrue(out.contains(ColorCode.GREEN.ansiSequence()));
    }

    // RED before fix: "[0" and "b]" merge across the newline, strict throws
    @Test
    void strictModeDoesNotMergeBracketsAcrossItems() {
        ItemList list = strictList()
                .item("-", "arr[0")
                .item("-", "b]");

        assertDoesNotThrow(list::get);
    }

    // Baseline
    @Test
    void strictModeStillThrowsOnBadTagInsideOneItem() {
        ItemList list = strictList().item("-", "a[0]");

        assertThrows(UnidentifiedStyleException.class, list::get);
    }

    // Guard against double parsing
    @Test
    void escapedBracketRendersLiterallyExactlyOnce() {
        ItemList list = new ItemList().item("-", "\\[red]");

        String out = list.get();

        assertTrue(out.contains("[red]"));
        assertFalse(out.contains(ColorCode.RED.ansiSequence()));
    }
}