# Tables

Tables help you display structured data in a clean, formatted way. Clique supports multiple table styles with extensive customization options. Tables don't support multi-line content.

## Table Types

Clique provides 5 built-in table styles:

1. **ASCII** - Standard table with ASCII characters
2. **COMPACT** - Minimalist table with fewer borders
3. **BOX_DRAW** - Table using box drawing characters
4. **ROUNDED_BOX_DRAW** - Box-draw table with rounded corners
5. **MARKDOWN** - Markdown-style table format

## Basic Usage

### Creating a Simple Table
```java
  Table table = Clique.table(TableType.ASCII)
        .headers("Name", "Age", "Class")
        .row("John", "25", "Class A")
        .row("Doe", "26", "Class B");

  table.render();
```

Pending table prepares a table whose headers have not yet been initialized. It is not a component 

### Using Markup in Tables
Tables automatically parse markup tags when you add content:
```java
Table table = Clique.table(TableType.BOX_DRAW)
        .headers(
                "[cyan, bold]Product[/]",
                "[cyan, bold]Price[/]",
                "[cyan, bold]Stock[/]"
        )
        .row(
                "[yellow]Widget[/]",
                "[green]$19.99[/]",
                "[red, bold]Low[/]"
        )
        .row(
                "[yellow]Gadget[/]",
                "[green]$29.99[/]",
                "In Stock"
        );

table.render();
```

### Initializing from Existing Data

If your data already exists as rows or columns, `fromRows()` and `fromColumns()` build the table in one call instead of chaining `.row()` repeatedly.

#### From Rows

The first row is treated as headers; every row after that is added as data:

```java
SequencedCollection<List<String>> rows = List.of(
        List.of("Name", "Age", "Class"),
        List.of("John", "25", "Class A"),
        List.of("Doe", "26", "Class B")
);

Table table = Clique.table(TableType.ASCII)
    .fromRows(rows);

table.render();
```

#### From Columns

Maps each header to its column values, top to bottom:

```java
SequencedMap<String, List<String>> map = new LinkedHashMap<>();
map.put("Name", List.of("John", "Doe"));
map.put("Age", List.of("25", "26"));
map.put("Class", List.of("Class A", "Class B"));
Table table = Clique.table(TableType.ASCII)
    .fromColumns(map);

table.render();
```

> Columns of differing lengths are padded with the table's configured `nullReplacement` (default: empty string). See [Null Handling](#null-handling).

Both methods throw `IllegalArgumentException` if the given collection/map is null or empty.

## Table Configuration

Use `TableConfiguration` to customize table appearance and behavior. Access the builder via `TableConfiguration.builder()`, which returns a `TableConfigurationBuilder`.

**Note:** Markup parsing is enabled by default.

### Default Values

| Option | Default |
|---|---|
| `alignment` | `CellAlign.LEFT` |
| `parser` | `MarkupParser.DEFAULT` |
| `borderColor` | `{}` (no color) |
| `padding` | `1` |
| `nullReplacement` | `""` (empty string) |
| `columnAlignment` | `{}` (empty map) |

### Basic Configuration
```java
TableConfiguration config = TableConfiguration
    .builder()
    .alignment(CellAlign.CENTER)  // Center all cells
    .padding(2)                   // Add 2 spaces padding
    .build();

Clique.table(TableType.ASCII, config)
    .headers("Name", "Age", "Class")
    .row("John", "25", "Class A")
    .render();
```

### Full Configuration Example
```java
TableConfiguration config = TableConfiguration
    .builder()
    .columnAlignment(0, CellAlign.LEFT)
    .borderColor("red")
    .alignment(CellAlign.CENTER)
    .padding(1)
    .build();

Clique.table(TableType.MARKDOWN, config)
    .headers("[green, bold]Name[/]", "[green, bold]Age[/]", "[green, bold]Class[/]")
    .row("[red]John[/]", "25", "Class A")
    .row("[red]Doe[/]", "26", "Class B")
    .render();
```

## Things to Watch Out For
- **Column alignment** always overrides table-wide alignment settings.
- `null` values for `alignment`, `parser`, `borderColor`, `nullReplacement`, or `columnAlignment` in the builder will throw a `NullPointerException`.
- Padding cannot be negative — an `IllegalArgumentException` will be thrown.
- Column index in `columnAlignment(int, CellAlign)` cannot be negative — an `IllegalArgumentException` will be thrown.

## See Also
- [Markup Reference](markup-reference.md) - Styling options for table content
- [Parser Documentation](parser.md) - How markup parsing works