# Component

`Component` is the base interface for all renderable Clique components. Every built-in component implements this interface, giving them a consistent API for rendering and string retrieval.

Components that implement `Component`:
- [`Box`](box.md) - bordered text containers
- [`Divider`](divider.md) - horizontal line separators
- [`Frame`](frame.md) - layout containers that stack nested components
- [`Tree`](tree.md) - hierarchical data with connector lines
- [`ItemList`](item-list.md) - symbol-driven nested lists
- [`Table`](tables.md) - structured tabular data. `Clique.table(...)` returns a `PendingTable`, which becomes a `Table` once you set headers. Only the `Table` is a `Component`.
- [`ProgressBar`](progress-bars.md) - visual progress tracking

## Methods

### `get()`

Builds and returns the formatted string representation of the component without printing it. Useful when you want to compose output, store it, or pass it elsewhere.

```java
String rendered = Clique.divider(40).get();
```

> Each call performs a render. Caching is up to the implementation. `Box` and `Table`, for example, cache their output until their content changes.

### `render()`

Prints the component to `System.out`, followed by a newline. This is the most common way to display a component.

```java
Clique.box()
    .content("Hello, World!")
    .render();
```

Some components override the default behavior. `ProgressBar` redraws in place with a carriage return, and only emits a newline once it's complete. `Frame` output already ends with a newline, so `render()` on a frame adds a blank line after it.

### `render(PrintStream stream)`

Prints the component to a specific `PrintStream`. Use this to redirect output to `System.err`, a file stream, or any custom stream.

```java
Clique.box()
    .content("[red, bold]Something went wrong[/]")
    .render(System.err);
```

> Passing `null` as the stream throws a `NullPointerException`.

## Using `Component` as a Type

Since all Clique components share the `Component` interface, you can store and pass them around generically:

```java
Component warning = Clique.box()
    .dimensions(50, 5)
    .content("[yellow]Proceed with caution[/]");

warning.render();
```

This also makes it easy to build collections of components and render them together:

```java
List<Component> report = List.of(
    Clique.divider(60, "blue").title("Summary"),
    Clique.table(TableType.BOX_DRAW).headers("Key", "Value").row("Status", "OK"),
    Clique.divider(60, "blue")
);

report.forEach(Component::render);
```

## Implementing `Component`

You can implement `Component` directly for custom renderable output. Only `get()` is required. `render()` and `render(PrintStream)` are provided as defaults.

`get()` returns the string as-is. Markup is not parsed for you, so call a parser yourself if you want styling:

```java
Component clock = () -> {
    var now = java.time.LocalTime.now();
    return Clique.parser().parse(
        "[cyan]%02d:%02d:%02d[/]".formatted(now.getHour(), now.getMinute(), now.getSecond())
    );
};

clock.render(); // prints current time to stdout
```

Custom components can also be nested inside a `Frame`, since `Frame.nest()` accepts any `Component`:

```java
Clique.frame()
    .nest(clock)
    .render(); // clock is re-evaluated on every render call
```

## Things to Watch Out For

- `get()` never returns `null`
- `render(null)` throws a `NullPointerException`
- `get()` is not cached by the interface. Some components cache internally, so don't assume every call is cheap or fresh
- Custom implementations of `get()` must parse their own markup if they want it styled

## See Also

- [Markup Reference](markup-reference.md) - Styling options for component content
- [Parser Documentation](parser.md) - How markup parsing works