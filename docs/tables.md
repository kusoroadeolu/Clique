# Tables

Tables help you display structured data in a clean, formatted way. Clique supports multiple table styles with extensive customization options. Tables don't support multi-line content.

## Table Types

Clique provides 5 built-in table styles:

1. **ASCII** - Standard table with ASCII characters
2. **COMPACT** (or **MINIMAL**) - Minimalist table with fewer borders
3. **BOX_DRAW** - Table using box drawing characters
4. **ROUNDED_BOX_DRAW** - Box-draw table with rounded corners
5. **MARKDOWN** - Markdown-style table format

## Basic Usage

### Creating a Simple Table
```java
PendingTable table = Clique.table(TableType.ASCII);
table.headers("Name", "Age", "Class")
    .row("John", "25", "Class A")
    .row("Doe", "26", "Class B");

table.render(); // Print the table to terminal
```

## Table Manipulation

Tables support dynamic updates after creation:

### Update a Cell
```java
Table table = Clique.table(TableType.ASCII)
        .headers("Name", "Age", "Status")
        .row("Alice", "25", "Active")
        .row("Bob", "30", "Inactive");

// Update a specific cell (row 1, column 2)
table.updateCell(1, 2, "Active");
```

### Remove a Cell
```java
// Remove a specific cell (replaces with null replacement)
table.removeCell(1, 1);
```

### Remove a Row
```java
// Remove an entire row (cannot remove headers at row 0)
table.removeRow(1);
```

### Get Table as String
```java
// Get table as string instead of printing
String tableString = table.get();
System.out.println(tableString);
```

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
SequencedMap<String, List<String>> map = new LinkedHashMap();
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

### Configuration Options

#### Alignment

Control how content is aligned within cells:
```java
TableConfiguration config = TableConfiguration
    .builder()
    .alignment(CellAlign.CENTER)           // Center everything
    .columnAlignment(0, CellAlign.LEFT)    // Column 0 left-aligned
    .columnAlignment(2, CellAlign.RIGHT)   // Column 2 right-aligned
    .build();
```

**Note:** Column alignment always takes precedence over table alignment.

Available alignments:
- `CellAlign.LEFT` - Left-aligned (default)
- `CellAlign.CENTER` - Centered
- `CellAlign.RIGHT` - Right-aligned

You can also set multiple column alignments at once by passing a `Map<Integer, CellAlign>`:
```java
Map<Integer, CellAlign> colAligns = new HashMap<>();
colAligns.put(0, CellAlign.LEFT);
colAligns.put(2, CellAlign.RIGHT);

TableConfiguration config = TableConfiguration
    .builder()
    .columnAlignment(colAligns)
    .build();
```

#### Padding

Add whitespace around cell content to prevent cramping. Default is `1`. Padding cannot be negative.
```java
TableConfiguration config = TableConfiguration
    .builder()
    .padding(3)  // 3 spaces on the left/right side, halved on each side if cell is centered
    .build();
```

#### Null Handling

When cells are null or removed, Clique replaces them with a configurable value:
```java
TableConfiguration config = TableConfiguration.builder()
    .nullReplacement("N/A")  // Default is empty string
    .build();

Clique.table(TableType.ASCII, config)
    .headers("Name", "Age", "City")
    .row("Alice", null, "NYC")  // null becomes "N/A"
    .render();
```

#### Border Coloring

Set a uniform border color using a color name string or `AnsiCode` values directly:

> **Note:** Uniform styling is recommended for tables. Due to their more complex layout, passing multiple `AnsiCode` values may not fully align visually.

```java
// Using a color name string
TableConfiguration config = TableConfiguration
    .builder()
    .borderColor("blue")
    .build();

// Using AnsiCode values directly
TableConfiguration config = TableConfiguration
    .builder()
    .borderColor(ColorCode.BLUE)
    .build();
```

#### Custom Parser

Provide a custom configured parser for markup processing:
```java
ParserConfiguration parserConfig = ParserConfiguration
    .builder()
    .delimiter(' ')
    .build();

TableConfiguration config = TableConfiguration
    .builder()
    .parser(Clique.parser(parserConfig))
    .build();
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