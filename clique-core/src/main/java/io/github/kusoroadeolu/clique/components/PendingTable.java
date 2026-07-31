package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;


/**
 * @since 4.0.0
 * */
@Stable(since = "4.0.0")
public interface PendingTable {
    Table headers(String... headers);

    /**
     * @deprecated in favor of the {@link SequencedCollection} overload
     * */
    @Deprecated(since = "4.0.3")
    Table headers(Collection<String> headers);

    Table headers(SequencedCollection<String> headers);

    Table fromColumns(SequencedMap<String, ? extends SequencedCollection<String>> columns);
}
