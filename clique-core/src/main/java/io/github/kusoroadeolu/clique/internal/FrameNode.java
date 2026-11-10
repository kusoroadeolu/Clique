package io.github.kusoroadeolu.clique.internal;

import io.github.kusoroadeolu.clique.components.Component;
import io.github.kusoroadeolu.clique.configuration.FrameAlign;
import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.utils.MiscUtils;
import io.github.kusoroadeolu.clique.parser.MarkupParser;

import java.util.List;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.ZERO;
import static io.github.kusoroadeolu.clique.internal.utils.MiscUtils.parseToCell;

@InternalApi(since = "3.2.0")
public sealed interface FrameNode permits FrameNode.StringNode, FrameNode.ComponentNode {
    List<Cell> lines();
    int maxWidth();
    FrameAlign align();
    default FrameNode snapshot() {
        return this;
    }

    static int findMaxWidth(List<Cell> lines) {
        return lines
                .stream()
                .mapToInt(Cell::width)
                .max()
                .orElse(ZERO);
    }

    static List<Cell> splitComponentLines(String str) {
        return str.lines()
                .map(s -> new Cell(MiscUtils.stripAnsi(s), s))
                .toList();//Original to styled string for components, we actually need to parse here with a default parser
    }

    //For raw strings, we need to handle the case in which the string has markup, however for components, when we call the get method, they apply their markup so it's good
    static List<Cell> splitLines(String str, MarkupParser parser){
        return str.lines().map(s -> parseToCell(s, parser)).toList();
    }


     record ComponentNode(Component component, FrameAlign align) implements FrameNode {

        @Override
        public List<Cell> lines() {
            return splitComponentLines(component.get());
        }

        @Override
        public int maxWidth() {
            return findMaxWidth(lines());
        }

        @Override
        public FrameAlign align() {
            return align;
        }

         @Override
         public FrameNode snapshot() {
             return new StringNode(lines(), 0, align);
         }
    }



    record StringNode(List<Cell> lines, int maxWidth, FrameAlign align) implements FrameNode {

        public StringNode(String str, FrameAlign align, MarkupParser parser) {
            this(FrameNode.splitLines(str, parser), 0, align);
        }

        public StringNode {
            maxWidth = findMaxWidth(lines);
        }
    }
}

