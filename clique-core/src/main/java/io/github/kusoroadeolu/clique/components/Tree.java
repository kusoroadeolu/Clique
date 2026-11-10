package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.TreeConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.EMPTY;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.NEWLINE;

/**
 * A tree component that renders hierarchical data as an indented, connector-linked structure.
 *
 * <p>Each {@code Tree} instance represents a node. Children are added via {@link #add(String)}
 * or {@link #add(Tree)}, and both return the child node, allowing subtrees to be built
 * by chaining calls on the returned reference.</p>
 *
 * <p>Rendering is performed from the root node via {@link #get()}. Calling {@code get()}
 * on a child node renders only the subtree rooted at that node.</p>
 *
 * <p><b>Note:</b> This class is not thread-safe.</p>
 *
 * @since 3.1.0
 */
@Stable(since = "3.1.0")
public class Tree implements Component {
    private final String label;
    private final List<Tree> children; //Accumulates children
    private TreeConfiguration configuration;
    static final String CONNECTOR = "├─ ";
    static final String END_CONNECTOR = "└─ ";
    static final String SPACE = "   ";
    static final String CONNECTING_LINE = "│  ";
    private AnsiCode[] connectorColor;
    private Tree parent;

    public Tree(String label) {
        this(label, TreeConfiguration.DEFAULT, null);
    }

    public Tree(String label, TreeConfiguration configuration) {
        this(label, configuration, null);
    }

    private Tree(String label, TreeConfiguration configuration, Tree parent) {
        validateLabel(label);
        Objects.requireNonNull(configuration, "Tree configuration cannot be null");
        this.label = label;
        children = new ArrayList<>();
        this.configuration = configuration;
        this.parent = parent;
        connectorColor = configuration.getConnectorColor();
    }

    /**
     * Creates a new child node with the given label and appends it to this node's children.
     *
     * <p>The child inherits this node's {@link TreeConfiguration}. The returned node
     * is the newly created child, not this node.</p>
     *
     * @param label the label for the child node; must not be {@code null}
     * @return the newly created child node
     * @throws NullPointerException if {@code label} is {@code null}
     */
    public Tree add(String label) {
        validateLabel(label);
        var child = new Tree(label, configuration, this);
        children.add(child);
        return child;
    }

    /**
     * Appends an existing {@code Tree} as a child of this node.
     *
     * <p>The provided tree is re-parented to this node. The returned node is the
     * provided child, not this node.</p>
     *
     * @param tree the tree to append as a child; must not be {@code null} or this node
     * @return the provided child node
     * @throws NullPointerException          if {@code tree} is {@code null}
     * @throws UnsupportedOperationException if {@code tree} is this node
     */
    public Tree add(Tree tree) {
        Objects.requireNonNull(tree, "Tree cannot be null");
        assertNoCycle(tree);

        if (tree.parent != null) tree.parent.children.removeIf(c -> c == tree);
        tree.parent = this;
        tree.recursivelySetConfiguration(configuration);
        children.add(tree);
        return tree;
    }

    // Walks up from this node. If `tree` is this node or any ancestor, adding it would make a cycle.
    void assertNoCycle(Tree tree) {
        for (Tree node = this; node != null; node = node.parent) {
            if (node == tree) {
                throw new UnsupportedOperationException("Cannot nest a tree within itself or one of its descendants");
            }
        }
    }

    void recursivelySetConfiguration(TreeConfiguration config) {
        configuration = config;
        connectorColor = config.getConnectorColor();
        for (Tree child : children) child.recursivelySetConfiguration(config);
    }

    /**
     * Returns the parent of this node, or an empty {@link Optional} if this is the root.
     *
     * @return an {@code Optional} containing the parent node, or empty if none
     */
    public Optional<Tree> parent(){
        return Optional.ofNullable(parent);
    }

    private void buildTree(Tree node, String prefix, boolean isLast, StyleBuilder sb) {
        String connector = isLast ? END_CONNECTOR : CONNECTOR;
        var parser = configuration.getParser();
        sb.append(NEWLINE)
                .append(prefix, this.connectorColor)
                .appendAndReset(connector)
                .appendAndReset(parser.parse(node.label));

        var childPrefix = prefix + (isLast ? SPACE : CONNECTING_LINE);
        for (int i = 0; i < node.children.size(); i++) {
            boolean lastChild = i == (node.children.size() - 1);
            buildTree(node.children.get(i), childPrefix, lastChild, sb);
        }
    }

    void validateLabel(String label){
        Objects.requireNonNull(label, "Label cannot be null");
    }

    /**
     * Renders this node and all its descendants as a formatted tree string.
     *
     * <p>If a parser is configured via {@link TreeConfiguration}, markup in node
     * labels is resolved before the string is returned.</p>
     *
     * @return the rendered tree as a string
     */
    @Override
    public String get() {
        var sb = new StyleBuilder();
        var parser = configuration.getParser();
        sb.appendAndReset(parser.parse(label));
        for (int i = 0; i < children.size(); i++) {
            buildTree(children.get(i), EMPTY, i == children.size() - 1, sb);
        }
        return sb.toString();
    }

    //For Tests
    List<Tree> children(){
        return children;
    }

    void assertNotSelf(Tree tree){
        if (tree == this) throw new UnsupportedOperationException("Cannot nest a tree within itself");
    }

    @Override
    public String toString() {
        return "Tree[label='" + label + "', children=" + children + ']';
    }
}