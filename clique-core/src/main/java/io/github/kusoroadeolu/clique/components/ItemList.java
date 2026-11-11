package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.ItemListConfiguration;
import io.github.kusoroadeolu.clique.internal.ListItem;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.*;

/**
 * A component representing a hierarchical list of items with customizable symbols and indentation.
 *
 * <p>Lists support nesting to create multi-level structures. Each level of nesting
 * increases the indentation based on the {@link ItemListConfiguration}.
 *
 * <p><b>Resolution Order:</b>
 * <ol>
 * <li>The parent list is traversed sequentially.</li>
 * <li>For each item, the symbol and content are appended.</li>
 * <li>If a sublist exists, it is recursively resolved with an incremented depth
 * before the next item in the parent list is processed.</li>
 * </ol>
 *
 * <p><b>Scoping:</b> Sublists added via {@link #item(String, String, ItemList)} are
 * reconfigured <i>in place</i> to use the parent's configuration, replacing whatever
 * configuration they had. If the same sublist instance is added to multiple parents,
 * the last parent's configuration wins for all of them.
 *
 * <p><b>Thread Safety:</b> This class is <b>not thread-safe</b>. The internal item storage
 * is mutable, and the configuration may be updated internally during nesting operations.
 *
 * @since 4.0.0
 */
@Stable(since = "4.0.1")
public class ItemList implements Component {
    private final List<ListItem> items;
    private ItemListConfiguration configuration;
    private String previousSymbol;
    private static final ItemList NONE = new ItemList();

    public ItemList(ItemListConfiguration configuration) {
        this.items = new ArrayList<>();
        this.configuration = configuration;
    }

    public ItemList() {
        this(ItemListConfiguration.DEFAULT);
    }

    /**
     * Adds an item to the list with an associated sublist.
     *
     * <p>The sublist will be modified to use the parent's configuration.
     *
     * @param symbol the character or string to use as the item marker (e.g., "*", "-")
     * @param content the text content of the item
     * @param subList the nested sublist; must not be {@code null}
     * @return this list instance for method chaining
     * @throws IllegalArgumentException if {@code subList} is this instance
     * @throws NullPointerException if {@code subList} is {@code null}
     */
    public ItemList item(String symbol, String content, ItemList subList){
        Objects.requireNonNull(subList, "Sublist cannot be null");
        assertNoCycle(subList);
        items.add(new ListItem(symbol, content, subList.recursivelySetConfiguration(configuration)));
        previousSymbol = symbol;
        return this;
    }
    /**
     * Adds a leaf item to the list with no sublist.
     *
     * @param symbol the character or string to use as the item marker
     * @param content the text content of the item
     * @return this list instance for method chaining
     */
    public ItemList item(String symbol, String content) {
        previousSymbol = symbol;
        items.add(new ListItem(symbol, content, NONE));
        return this;
    }

    /**
     * Adds a leaf item to the list with no sublist.
     * Uses the last used symbol for this item
     *
     * @param content the text content of the item
     * @return this list instance for method chaining
     *
     * @throws IllegalStateException if no symbol has been set by a previous call
     */
    public ItemList item(String content) {
        if (previousSymbol == null) throw new IllegalStateException("No symbol has been set yet");
        return item(previousSymbol, content);
    }


    /**
     * {@inheritDoc}
     *
     * <p>Returns the fully formatted hierarchical list as a string, with markup
     * resolved according to the current configuration.
     */
    @Override
    public String get() {
        var sb = new StyleBuilder();
        buildList(sb, ZERO, this);
        String out = sb.toString();
        return out.endsWith(NEWLINE) ? out.substring(0, out.length() - NEWLINE.length()) : out;
    }

    void assertNoCycle(ItemList subList) {
        if (subList == this || subList.contains(this)) {
            throw new IllegalArgumentException("Cannot nest a list within itself or one of its descendants");
        }
    }

    private boolean contains(ItemList target) {
        for (ListItem item : items) {
            ItemList sub = item.sublist();
            if (sub == NONE) continue;
            if (sub == target || sub.contains(target)) return true;
        }
        return false;
    }

    void buildList(StyleBuilder sb, int depth, ItemList root) {
        var parser = configuration.getParser();
        for (ListItem item : root.items) {
            sb.append(BLANK.repeat(depth * configuration.getIndentSize()))
                    .append(parser.parse(item.symbol()))
                    .append(BLANK.repeat(configuration.getSymbolSpacing()))
                    .appendAndReset(parser.parse(item.content()))
                    .append(NEWLINE);

            if (item.sublist() != NONE) {
                buildList(sb, depth + 1, item.sublist());
            }
        }
    }


    ItemList recursivelySetConfiguration(ItemListConfiguration configuration){
        this.configuration = configuration;

        for (ListItem item : items) {
            if (item.sublist() != NONE) {
                item.sublist().recursivelySetConfiguration(configuration);
            }
        }

        return this;
    }


    /**
     * Internal test helper to retrieve the first child sublist.
     */
    ItemList child(){
        return items.stream()
                .map(ListItem::sublist)
                .toList()
                .getFirst();
    }

    /**
     * Internal test helper to retrieve the current configuration.
     */
    ItemListConfiguration configuration(){
        return configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ItemList itemList = (ItemList) o;
        return Objects.equals(items, itemList.items) && Objects.equals(configuration, itemList.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, configuration);
    }

    @Override
    public String toString() {
        return "ItemList[" +
                "items=" + items +
                ", configuration=" + configuration +
                ']';
    }
}