# Divider

Dividers are horizontal lines that span a given width. They're useful for separating sections of terminal output. A divider can optionally display a title within the line.

## Basic Usage

### Creating a Simple Divider
```java
Divider divider = Clique.divider(40);
divider.render(); // Print the divider to terminal
```

### Adding a Title
```java
Clique.divider(40)
    .title("Section One")
    .render();
// ─────────────  Section One  ──────────────
```

The title is centered by default. See [Title Alignment](#title-alignment) to place it elsewhere.

### Applying a Color

You can pass a color string or `AnsiCode` values directly:
```java
// Using a color name string
Clique.divider(40, "blue").render();

// Using AnsiCode values directly
Clique.divider(40, ColorCode.BLUE).render();
```

### Using Markup in Titles

The title supports inline markup tags:
```java
Clique.divider(50)
    .title("[bold, red]Critical[/]")
    .render();
```

## Title Alignment

`TitleAlign` controls where the title sits on the line. The default is `TitleAlign.CENTER`.

Title alignment is set per call, not in `DividerConfiguration`. `title(String)` always centers the title. `title(String, TitleAlign)` lets you choose the position:

```java
import io.github.kusoroadeolu.clique.configuration.TitleAlign;

Clique.divider(30).title("Errors", TitleAlign.LEFT).render();
Clique.divider(30).title("Errors", TitleAlign.CENTER).render();
Clique.divider(30).title("Errors", TitleAlign.RIGHT).render();
```

Output for a 30-wide divider with the title "Errors":

─ Errors ─────────────────────

─────────── Errors ───────────

───────────────────── Errors ─


The title always has one space on each side. The remaining width is filled with dash characters:

| `TitleAlign` | Dashes before title | Dashes after title |
|---|---|---|
| `LEFT` | 1 | remainder |
| `CENTER` | half of remainder | the other half |
| `RIGHT` | remainder | 1 |

When the remainder is odd, `CENTER` puts the extra dash after the title.

## Divider Configuration

Use `DividerConfiguration` to customize the divider's character, color, and title parser. Access the builder via `DividerConfiguration.builder()`. Title alignment is not part of this configuration. Set it per call as shown above.

### Default Values

| Option | Default |
|---|---|
| `dividerChar` | `'─'` |
| `dividerColor` | `{}` (no color) |
| `parser` | `MarkupParser.DEFAULT` |

### Configuration Options

#### Divider Character

Change the character used to draw the line:
```java
DividerConfiguration config = DividerConfiguration.builder()
    .dividerChar('=')
    .build();
```

#### Divider Color

Set the line color using a color name string or `AnsiCode` values:
```java
// Using a color name string
DividerConfiguration config = DividerConfiguration.builder()
    .dividerColor("green")
    .build();

// Using AnsiCode values directly
DividerConfiguration config = DividerConfiguration.builder()
    .dividerColor(ColorCode.GREEN)
    .build();
```

#### Custom Parser

Provide a custom parser for markup resolution in the title:
```java
ParserConfiguration parserConfig = ParserConfiguration.builder()
    .delimiter(' ')
    .build();

DividerConfiguration config = DividerConfiguration.builder()
    .parser(Clique.parser(parserConfig))
    .build();
```

### Full Configuration Example
```java
DividerConfiguration config = DividerConfiguration.builder()
    .dividerChar('━')
    .dividerColor("cyan")
    .parser(Clique.parser())
    .build();

Clique.divider(60, config)
    .title("[bold]Results[/]")
    .render();
```

## Examples

### Section Separator
```java
Clique.divider(50, "blue")
    .title("Configuration")
    .render();
```

### Plain Rule
```java
Clique.divider(80).render();
```

### Styled Error Divider
```java
DividerConfiguration config = DividerConfiguration.builder()
    .dividerChar('!')
    .dividerColor("red")
    .build();

Clique.divider(40, config)
    .title("[bold, red]ERROR[/]")
    .render();
```

## Things to Watch Out For

- `width` must be greater than `0`. An `IllegalArgumentException` is thrown otherwise.
- The title's **visible length** (excluding markup tags) plus 2 must be less than the divider width. The 2 accounts for the spaces around the title. In a 30-wide divider, the longest title you can use is 27 visible characters. An `IllegalArgumentException` is thrown if the limit is exceeded.
- `DividerConfiguration` is immutable and thread-safe. `DividerConfigurationBuilder` is not thread-safe, so synchronize externally if you share a builder across threads.

## See Also

- [Markup Reference](markup-reference.md) - Styling options for divider titles
- [Parser Documentation](parser.md) - How markup parsing works