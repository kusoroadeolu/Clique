# Frames

Frames are layout containers that vertically stack nested Clique components inside a bordered rectangle. Unlike boxes, frames have no opinion about text, they don't wrap, align, or manipulate string content. They just draw a border around whatever their children produce.

## Frame Types

Clique provides 4 built-in frame styles:

1. **ASCII** - Standard frame with ASCII borders
2. **CLASSIC** - Classic frame style
3. **ROUNDED** - Frame with rounded corners
4. **DOUBLE_LINE** - Frame with double-line borders

**Note:** Frame shares the same enum with `Box`

## Basic Usage

### Creating a Simple Frame
```java
Clique.frame()
    .nest("Hello from a frame")
    .render();
```

### Nesting Components

Frames are designed to wrap existing Clique components:
```java
Table table = Clique.table(TableType.BOX_DRAW)
        .headers("Name", "Age")
        .row("Alice", "25")
        .row("Bob", "30");

ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.BLOCKS);
bar.tick(70);

Clique.frame()
    .nest(table)
    .nest(bar)
    .render();
```

### Adding a Title
```java
Clique.frame()
    .title("[bold]My App[/]")
    .nest(table)
    .render();
```

## Nesting Content

### Nesting Components

Any Clique component that implements `Component` can be nested:
```java
Clique.frame()
    .nest(table)
    .nest(progressBar)
    .nest(itemList)
    .render();
```

### Nesting Raw Strings

Raw strings and markup strings are both supported:
```java
Clique.frame()
    .nest("Plain string")
    .nest("[green, bold]Styled string[/]")
    .render();
```

### Per-Node Alignment

Control how each child sits within the frame width using `TitleAlign`:
```java
Clique.frame()
    .nest(header, TitleAlign.CENTER)
    .nest(table, TitleAlign.LEFT)
    .nest("[dim]footer[/]", TitleAlign.RIGHT)
    .render();
```

## Title Alignment

The frame title can be independently aligned within the top border:
```java
// Title on the left
Clique.frame()
    .title("My App", TitleAlign.LEFT)
    .render();

// Title centered (default)
Clique.frame()
    .title("My App")
    .render();

// Title on the right
Clique.frame()
    .title("My App", TitleAlign.RIGHT)
    .render();
```

Available alignments:
- `TitleAlign.LEFT` - Left-aligned
- `TitleAlign.CENTER` - Centered
- `TitleAlign.RIGHT` - Right-aligned

## Width

By default, the frame derives its width from the widest nested component automatically. You can also set it explicitly:
```java
Clique.frame()
    .width(60)
    .nest(table)
    .render();
```

If you don't call `.width()`, the frame measures all nested children and sizes itself to the widest one. Width cannot be set to zero or a negative value.

## Frame Configuration

Use `FrameConfiguration` to customize frame appearance and behavior. Access the builder via `FrameConfiguration.builder()`, which returns a `FrameConfigurationBuilder`.

### Default Values

| Option | Default |
|---|---|
| `TitleAlign` | `TitleAlign.CENTER` |
| `parser` | `MarkupParser.DEFAULT` |
| `borderColor` | `{}` (no color) |
| `padding` | `2` |

### Basic Configuration
```java
FrameConfiguration config = FrameConfiguration.builder()
    .TitleAlign(TitleAlign.LEFT)
    .padding(1)
    .build();

Clique.frame(config)
    .title("My Frame")
    .nest(table)
    .render();
```

### Configuration Options

#### Default Alignment

Set the default alignment for all nested children. Individual `.nest()` calls can still override this per node:
```java
FrameConfiguration config = FrameConfiguration.builder()
    .TitleAlign(TitleAlign.CENTER)
    .build();
```

#### Padding

Set the horizontal padding inside the frame. Default is `2`. Padding cannot be negative.
```java
FrameConfiguration config = FrameConfiguration.builder()
    .padding(4)
    .build();
```

#### Border Coloring

Set a uniform border color using a color name string or `AnsiCode` values directly:
```java
// Using a color name string
FrameConfiguration config = FrameConfiguration.builder()
    .borderColor("red")
    .build();

// Using AnsiCode values directly
FrameConfiguration config = FrameConfiguration.builder()
    .borderColor(ColorCode.RED)
    .build();
```

#### Custom Parser

Provide a custom configured parser for markup processing in string nodes and titles:
```java
ParserConfiguration parserConfig = ParserConfiguration
    .builder()
    .delimiter(' ')
    .build();

FrameConfiguration frameConfig = FrameConfiguration.builder()
    .parser(Clique.parser(parserConfig))
    .build();
```

### Full Configuration Example
```java
FrameConfiguration config = FrameConfiguration.builder()
    .TitleAlign(TitleAlign.CENTER)
    .padding(3)
    .borderColor("blue")
    .build();

Clique.frame(BoxType.DOUBLE_LINE, config)
    .title("[bold, cyan]Dashboard[/]", TitleAlign.CENTER)
    .nest(headerTable, TitleAlign.CENTER)
    .nest(progressBar, TitleAlign.LEFT)
    .nest("[dim]last updated: just now[/]", TitleAlign.RIGHT)
    .render();
```

## Lazy Evaluation

Components nested inside a frame are evaluated lazily — every call to `get()` or `render()` re-invokes each `Component` to produce fresh output. This means dynamic components like clocks, counters, or live status strings just work without any extra wiring.
```java
Component clock = () -> {
    var now = LocalTime.now();
    return "[cyan]%02d:%02d:%02d[/]".formatted(now.getHour(), now.getMinute(), now.getSecond());
};

// clock is re-evaluated on every render call
Clique.frame().nest(clock).render();
```

Raw strings passed to `nest()` are static — only `Component` instances get re-evaluated.

## Getting the Frame as a String

Use `get()` to retrieve the rendered frame as a string without printing:
```java
String frameString = Clique.frame()
    .title("Report")
    .nest(table)
    .get();

System.out.println(frameString);
```

## Examples

### Nested Tree
```java
Tree tree = Clique.tree("[*magenta, bold]clique-lib/");

Tree src = tree.add("[*cyan, bold]src/");
Tree core = src.add("[cyan]core/");
core.add("[green]Parser.java         [dim]✓ 312 lines");
core.add("[green]StyleResolver.java  [dim]✓ 198 lines");
core.add("[yellow]Renderer.java       [dim]⚠ needs review");

Tree tests = tree.add("[*cyan, bold]tests/");
tests.add("[green, bold]ParserTest.java     [dim]✓ 14/14 pass");
tests.add("[red, bold]RendererTest.java   [dim]✗  9/14 pass");
tests.add("[dim, strike]TreeTest.java       skipped");

tree.add("[white]README.md");
tree.add("[dim].gitignore");

Clique.frame(BoxType.CLASSIC)
    .title("Project Structure", TitleAlign.LEFT)
    .nest(tree)
    .render();
```

## Things to Watch Out For

- A nested component's content width **cannot exceed the frame's width**. If it does, an exception is thrown.
- The **title width cannot exceed the frame width**. Keep titles shorter than the frame's content.
- Frame width is derived from the **widest child** automatically, so you generally don't need to set it manually.
- `null` values for `TitleAlign`, `parser`, or `borderColor` in the builder will throw a `NullPointerException`.
- Padding cannot be negative — an `IllegalArgumentException` will be thrown.

## See Also

- [Boxes Documentation](box.md) - Text containers with borders
- [Tables Documentation](tables.md) - Structured data display
- [Markup Reference](markup-reference.md) - Styling options for titles and string nodes
- [Parser Documentation](parser.md) - How markup parsing works