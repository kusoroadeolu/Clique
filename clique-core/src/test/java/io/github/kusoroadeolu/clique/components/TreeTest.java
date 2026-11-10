package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.configuration.TreeConfiguration;
import io.github.kusoroadeolu.clique.exceptions.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.style.ColorCode;
import org.junit.jupiter.api.Test;

import static io.github.kusoroadeolu.clique.components.Tree.CONNECTOR;
import static io.github.kusoroadeolu.clique.components.Tree.END_CONNECTOR;
import static org.junit.jupiter.api.Assertions.*;

class TreeTest {

    @Test
    void assertRoot_hasNoParent(){
        Tree tree = Clique.tree("Some label");
        assertTrue(tree.parent().isEmpty());
    }

    //Here the tree does not create a new child node
    @Test
    void testManualTreeAddition(){
        Tree tree = Clique.tree("Some label");
        Tree child = Clique.tree("Child label");
        Tree returned = tree.add(child);
        assertSame(child, returned);
    }


    @Test
    void treeNodeShouldReturnParent(){
        Tree tree = Clique.tree("Some label");
        Tree node = tree.add("Node");
        assertSame(tree, node.parent().get());
    }

    @Test
    void treeShouldContainConnector(){
        Tree tree = Clique.tree("Some label");
        tree.add("Node");
        tree.add("Node");
        String str = tree.get();
        assertTrue(str.contains(CONNECTOR));
    }

    @Test
    void treeShouldContainEndConnector(){
        Tree tree = Clique.tree("Some label");
        tree.add("Node");
        tree.add("Node");
        String str = tree.get();
        assertTrue(str.contains(END_CONNECTOR));
    }

    @Test
    void treeShouldContainChild(){
        Tree tree = Clique.tree("Some label");
        Tree child = tree.add("Node");
        assertTrue(tree.children().contains(child));
    }

    @Test
    void assertThrowsUnsupportedOperationExWhenTreeRefIsAddedToItself(){
        Tree tree = Clique.tree("Some label");
        assertThrows(UnsupportedOperationException.class, () -> tree.add(tree));
    }

    @Test
    void removesCorrectSiblingOnReparent() {
        Tree root = Clique.tree("root");
        Tree a = root.add("same");
        Tree b = root.add("same");
        Tree other = Clique.tree("other");
        other.add(b);
        assertEquals(1, root.children().size());
        assertSame(a, root.children().getFirst());
    }

    @Test
    void rejectsCycles() {
        Tree a = Clique.tree("a");
        Tree b = a.add("b");
        assertThrows(UnsupportedOperationException.class, () -> b.add(a));
    }

    @Test
    void reparentedTreeInheritsConnectorColor() {
        var cfg = TreeConfiguration.builder().connectorColor("red").build();
        Tree root = Clique.tree("root", cfg);
        Tree loose = Clique.tree("loose");
        loose.add("child");
        root.add(loose);
        assertTrue(loose.get().contains("\u001B[31m"));
    }

    @Test
    void toStringAndHashCodeDontOverflow() {
        Tree child = Clique.tree("root").add("child");
        assertDoesNotThrow(child::toString);
        assertDoesNotThrow(child::hashCode);
    }

    private static final MarkupParser STRICT =
            new MarkupParser(ParserConfiguration.builder().enableStrictParsing().build());

    private static Tree strictTree(String root) {
        return new Tree(root, TreeConfiguration.builder().parser(STRICT).build());
    }

    // RED before fix: the stray [ poisons the later [green] tag
    @Test
    void strayBracketInOneLabelDoesNotBreakLaterLabels() {
        Tree tree = new Tree("x");
        tree.add("arr[0");
        tree.add("[green]ok[/]");

        String out = tree.get();

        assertFalse(out.contains("[green]"), "tag should have been parsed, got: " + out);
        assertTrue(out.contains(ColorCode.GREEN.ansiSequence()));
        assertTrue(out.contains("arr[0"), "stray bracket should stay literal");
    }

    // RED before fix: same poison, but via a nested child
    @Test
    void strayBracketInParentDoesNotBreakNestedChild() {
        Tree tree = new Tree("x");
        Tree parent = tree.add("arr[0");
        parent.add("[green]ok[/]");

        String out = tree.get();

        assertFalse(out.contains("[green]"));
        assertTrue(out.contains(ColorCode.GREEN.ansiSequence()));
    }

    // RED before fix: "[0" and "b]" merge into one bogus tag spanning the newline, strict throws
    @Test
    void strictModeDoesNotMergeBracketsAcrossLabels() {
        Tree tree = strictTree("x");
        tree.add("arr[0");
        tree.add("b]");

        assertDoesNotThrow(tree::get);
    }

    // Baseline, passes before and after
    @Test
    void strictModeStillThrowsOnBadTagInsideOneLabel() {
        Tree tree = strictTree("x");
        tree.add("a[0]");

        assertThrows(UnidentifiedStyleException.class, tree::get);
    }

    // Guard against double parsing, passes before and after
    @Test
    void escapedBracketRendersLiterallyExactlyOnce() {
        Tree tree = new Tree("x");
        tree.add("\\[red]");

        String out = tree.get();

        assertTrue(out.contains("[red]"));
        assertFalse(out.contains(ColorCode.RED.ansiSequence()),
                "escaped bracket was parsed a second time");
    }
}