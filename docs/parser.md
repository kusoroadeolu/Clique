# Parser

Clique's parser allows you to use a simple markup format for styling text instead of verbose `StyleBuilder` calls or raw ANSI codes.

## Basic Usage
### Parse and Print
```java
// Parse and print directly
Clique.parser().print("[red, bold]Error:[/] Something went wrong");

// Parse and return styled string
String styled = Clique.parser().parse("[red, bold]Error:[/] Something went wrong");
System.out.println(styled);
```

### Getting the Original String
After parsing, you can retrieve a text with the markup stripped:
```java
MarkupParser parser = Clique.parser();
String original = parser.getOriginalString("[red, bold]Hello[/] World"); // Returns "Hello World"
```

## Parser Rules

The parser follows a simple, predictable set of rules:

- `[red]` — valid, will be parsed and styled
- `[notastyle]` — valid syntax, but unrecognized style; passed through as-is
- `[nested[nested]]` — nested tags are not supported; passed through as-is
- `[not closed [red, blue] hello` — if a `[` is encountered before the current tag closes, the whole thing is ignored
- `[not closed [red]text[/]` — an unmatched `[` stops every later tag in the same string from being parsed
- Tags are case-insensitive: `[RED]`, `[Red]` and `[red]` are equivalent. 
Registered custom style names are lowercased too, so `registerStyle("Brand", ...)` is used as `[brand]`.

### Unmatched brackets

The tokenizer counts open brackets, so one stray `[` disables all later tags in the same string. Escape literal brackets to avoid this:

```java
Clique.parser().print("arr\\[0] is [green]ok[/]");
```

Components parse each unit on its own, so a stray bracket only affects the unit it appears in: a `Tree` label, an `ItemList` symbol or content string, a `Box` line, or a `Table` cell.

### Strict parsing and dynamic content

With `enableStrictParsing()`, any bracketed text that isn't a style throws, so `"a[0]"` throws `UnidentifiedStyleException`. Escape brackets in user-supplied or data-driven strings.

## Parser Configuration

Customize how the parser behaves using `ParserConfiguration`:
```java
ParserConfiguration configuration = ParserConfiguration
        .builder()
        .enableAutoReset()  // Automatically reset styles between tags to prevent leaking
        .delimiter(' ')          // Use space instead of comma as delimiter
        .build();

MarkupParser configuredParser = Clique.parser(configuration);

// Now you can use space-separated styles
configuredParser.print("[red bold]Hello[blue] World");
```

### Configuration Options

- **`delimiter(char)`** - Set the delimiter between style attributes (default: `,`)
- **`enableAutoReset()`** - Automatically resets styles when a new tag is encountered, preventing styles from leaking into subsequent tags
- **`enableStrictParsing()`** - Throw exceptions for unrecognized styles on otherwise valid tags
- **`addStyle(String, AnsiCode)`** - Register a single custom style for this parser instance
- **`styleContext(StyleContext)`** - Attach a full `StyleContext` of custom styles to this parser instance

## Local Styles with StyleContext

`StyleContext` lets you register custom styles scoped to a specific parser instance, without touching the global style registry. This is useful when you want custom markup that only applies in a specific context.

```java
StyleContext ctx = StyleContext.builder()
        .add("highlight", ColorCode.YELLOW)
        .add("muted", StyleCode.DIM)
        .build();

ParserConfiguration config = ParserConfiguration.builder()
        .styleContext(ctx)
        .build();

MarkupParser parser = Clique.parser(config);
parser.print("[highlight]This is highlighted[/] and [muted]this is muted[/]");
```

You can also register a single style directly on the builder without constructing a full `StyleContext`:

```java
ParserConfiguration config = ParserConfiguration.builder()
        .addStyle("highlight", ColorCode.YELLOW)
        .build();
```

### Style Resolution Order

When the parser encounters a markup tag, it resolves styles in the following order:

1. **Local styles** — defined via `styleContext()` or `addStyle()` on this parser's configuration
2. **Global custom styles** — registered via `Clique.registerStyle()`
3. **Predefined styles** — built-in colors, backgrounds, and text styles

This means local styles always win. If you define `highlight` locally and something with the same name exists globally, the local one takes precedence.

### Combining with Other Configuration

`StyleContext` composes naturally with the rest of `ParserConfiguration`:

```java
StyleContext ctx = StyleContext.builder()
        .add("tag", ColorCode.CYAN)
        .add("warn", ColorCode.YELLOW)
        .build();

ParserConfiguration config = ParserConfiguration.builder()
        .styleContext(ctx)
        .enableAutoReset()
        .enableStrictParsing()
        .build();

Clique.parser(config).print("[tag]INFO[/] [warn]Something looks off[/]");
```

## Parser Exceptions
When strict parsing is enabled, the parser throws exceptions for unrecognized styles on valid tags:

### UnidentifiedStyleException

Thrown when a valid tag contains a style that doesn't exist:
```java
ParserConfiguration config = ParserConfiguration.builder()
        .enableStrictParsing()
        .build();

MarkupParser parser = Clique.parser(config);

// Throws UnidentifiedStyleException because "bol" is not a recognized style
parser.parse("[red, bol]Text[/]");
```

**Note:** Without strict parsing enabled, unrecognized styles are ignored and the text is passed through as-is. Malformed or structurally invalid tags are always passed through regardless of strict mode.

## Escaping Special Characters

Since `[]` brackets are used for markup tags, you can escape them with a backslash to display literal brackets.

### Escaping Brackets
```java
// Display literal [red]
Clique.parser().print("\\[red]");
```

**Examples:**
```java
"\\[red]"               // Displays: [red]
        "\\[red, bold]"         // Displays: [red, bold]
        "Coords: \\[10, 20]"    // Displays: Coords: [10, 20]
```

## Markup Syntax Reference

See [markup-reference.md](markup-reference.md) for a complete list of default supported colors, background colors, and text styles.

## Using Parser with Other Features

The parser is integrated into every component. By default, markup parsing is enabled for all of those features:
```java
// Tables with markup
Clique.table(TableType.ASCII)
    .headers("[green, bold]Name[/]", "[green, bold]Age[/]")
    .row("[red]John[/]", "25")
    .render();

// Boxes with markup
Clique.box()
    .content("[bold, blue]This is a configured box[/]")
    .render();
```

You can also pass a custom configured parser to these features through their configuration objects.