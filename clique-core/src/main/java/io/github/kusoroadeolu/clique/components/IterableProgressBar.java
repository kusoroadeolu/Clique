package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.ProgressBarConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * A single-use {@link Iterable} wrapper around a {@link Collection} that advances
 * a {@link ProgressBar} on each element consumed.
 *
 * <p>Each call to {@link Iterator#next()} on the iterator returned by {@link #iterator()}
 * delegates to the underlying collection iterator and immediately invokes
 * {@link ProgressBar#tick()} on the internal progress bar, re-rendering it in place.
 *
 * <p>This iterable may only be traversed <b>once</b>. A second call to {@link #iterator()}
 * throws {@link IllegalStateException}. The progress bar total is fixed to the collection
 * size at construction time.
 *
 * <p>This class is <b>not</b> thread-safe.
 *
 * @param <T> the type of elements in the collection
 * @since 3.2.1
 */
@Stable(since = "3.2.1")
public class IterableProgressBar<T> implements Iterable<T>{

    private final Iterator<T> iterator;
    private boolean consumed = false;
    final ProgressBar progressBar;
    @SuppressWarnings("java:S106")
    private PrintStream stream = System.out;

    /**
     * Constructs an {@code IterableProgressBar} over the given collection with a
     * custom configuration.
     *
     * <p>The progress bar total is set to {@code collection.size()} at construction time
     * and does not reflect subsequent changes to the collection.
     *
     * @param collection    the collection to iterate over; must not be {@code null}
     * @param configuration the progress bar configuration to use; must not be {@code null}
     * @throws NullPointerException if {@code collection} or {@code configuration} is {@code null}
     */
    public IterableProgressBar(Collection<T> collection, ProgressBarConfiguration configuration) {
        Objects.requireNonNull(collection, "Collection cannot be null");
        Objects.requireNonNull(configuration, "Progress bar configuration cannot be null");
        this.iterator = collection.iterator();
        progressBar = new ProgressBar(collection.size(), configuration);
    }

    /**
     * Constructs an {@code IterableProgressBar} over the given collection using
     * {@link ProgressBarConfiguration#DEFAULT}.
     *
     * @param collection the collection to iterate over; must not be {@code null}
     * @throws NullPointerException if {@code collection} is {@code null}
     */
    public IterableProgressBar(Collection<T> collection) {
        this(collection, ProgressBarConfiguration.DEFAULT);
    }

    /**
     * Returns an iterator that advances the internal progress bar on each element consumed.
     *
     * <p>This method may only be called once. The returned iterator is not reusable and
     * reflects the state of the collection iterator captured at construction time.
     *
     * @return an iterator over the elements of the underlying collection
     * @throws IllegalStateException if this iterable has already been iterated
     */
    @Override
    public Iterator<T> iterator() {
        if (consumed) throw new IllegalStateException("IterableProgressBar can only be iterated once");
        consumed = true;
        return new ProgressBarIterator<>(this);
    }

    /**
     * Sets the print stream for this progress bar.
     *
     * @param stream the stream to print to; must not be {@code null}
     * @throws NullPointerException if {@code stream} is {@code null}
     */
    public IterableProgressBar<T> printStream(PrintStream stream){
        this.stream = Objects.requireNonNull(stream, "Print stream cannot be null");
        return this;
    }

    /**
     * Returns whether the underlying progress bar has reached its total.
     *
     * <p>This returns {@code true} only after all elements have been consumed via
     * {@link Iterator#next()}, since the bar is ticked per element.
     *
     * @return {@code true} if the progress bar is complete; {@code false} otherwise
     */
    public boolean isDone() {
        return progressBar.isDone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        IterableProgressBar<?> that = (IterableProgressBar<?>) o;
        return consumed == that.consumed && Objects.equals(iterator, that.iterator) && Objects.equals(progressBar, that.progressBar) && Objects.equals(stream, that.stream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iterator, consumed, progressBar, stream);
    }

    @Override
    public String toString() {
        return "IterableProgressBar[" +
                "iterator=" + iterator +
                ", consumed=" + consumed +
                ", progressBar=" + progressBar +
                ", stream=" + stream +
                ']';
    }

    private record ProgressBarIterator<T>(IterableProgressBar<T> iterableProgressBar) implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return iterableProgressBar.iterator.hasNext();
        }

        @Override
        public T next() {
            T item = iterableProgressBar.iterator.next();
            var bar = iterableProgressBar.progressBar;
            bar.tick(false)
                    .render(iterableProgressBar.stream);
            return item;
        }
    }
}