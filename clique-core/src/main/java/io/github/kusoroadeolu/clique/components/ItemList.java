package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.ItemListConfiguration;
import io.github.kusoroadeolu.clique.internal.ListItem;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.*;
import static io.github.kusoroadeolu.clique.style.StyleCode.RESET;

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
 * automatically reconfigured to inherit the configuration of the parent list.
 * Self-nesting is strictly prohibited.
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
    private String lastSymbol;
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
        lastSymbol = symbol;
        assertNotSelf(subList);
        items.add(new ListItem(symbol, content, subList.recursivelySetConfiguration(configuration)));
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
        lastSymbol = symbol;
        items.add(new ListItem(symbol, content, NONE));
        return this;
    }

    /**
     * Adds a leaf item to the list with no sublist.
     * Uses the last used symbol for this item
     *
     * @param content the text content of the item
     * @return this list instance for method chaining
     */
    public ItemList item(String content) {
        if (lastSymbol == null) throw new IllegalStateException("No symbol has been set yet");
        return item(lastSymbol, content);
    }


    /**
     * {@inheritDoc}
     *
     * <p>Returns the fully formatted hierarchical list as a string, with markup
     * resolved according to the current configuration.
     */
    @Override
    public String get() {
        StringBuilder sb = new StringBuilder();
        buildList(sb, ZERO ,this);
        return parseString(sb.toString());
    }

    void assertNotSelf(ItemList list){
        if (list == this) throw new IllegalArgumentException("Cannot nest list within itself");
    }

    void buildList(StringBuilder sb, int depth, ItemList root) {
        for (ListItem item : root.items) {
            sb.repeat(BLANK, depth * configuration.getIndentSize())
                    .append(item.symbol())
                    .repeat(BLANK, configuration.getSymbolSpacing())
                    .append(item.content())
                    .append(RESET)
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

    private String parseString(String str) {
        return StringUtils.parse(str, this.configuration.getParser());
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