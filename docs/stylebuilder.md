# StyleBuilder

StyleBuilder provides a simpler API for chaining styled strings together . It's perfect when you prefer a programmatic imperative and type-safe pattern over markup syntax.

## Basic Usage

### appendAndReset

Applies styles to a segment of text and resets the terminal after. Subsequent segments start with a clean style state:

```java
Clique.styleBuilder()
    .appendAndReset("Hello", ColorCode.BLUE, StyleCode.BOLD, StyleCode.UNDERLINE)
    .appendAndReset("World", ColorCode.RED, StyleCode.DIM, StyleCode.REVERSE_VIDEO)
    .print();
```

### append

Applies styles without resetting. Use this when you want styles to carry over into the next segment:

```java
Clique.styleBuilder()
    .append("Hello", ColorCode.BLUE, StyleCode.BOLD, StyleCode.UNDERLINE)
    .append("World", ColorCode.RED, StyleCode.DIM, StyleCode.REVERSE_VIDEO)
    .print();
```

### Combining Both

```java
Clique.styleBuilder()
    .append("WARN", ColorCode.YELLOW, StyleCode.BOLD)   // styles carry over
    .append(": ",   ColorCode.YELLOW)
    .appendAndReset("disk usage at 90%", ColorCode.WHITE) // clean slate after
    .appendAndReset("action required",   ColorCode.RED, StyleCode.BOLD)
    .print();
```

## Getting the Result

```java
String output = Clique.styleBuilder()
    .appendAndReset("Title: ", ColorCode.CYAN, StyleCode.BOLD)
    .appendAndReset("My Document", ColorCode.WHITE)
    .toString();    

System.out.println(output);
```

## Available Codes

### ColorCode

All standard and bright ANSI colors are available:
- Standard: `RED`, `GREEN`, `YELLOW`, `BLUE`, `MAGENTA`, `CYAN`, `WHITE`, `BLACK`
- Bright: `BRIGHT_RED`, `BRIGHT_GREEN`, `BRIGHT_YELLOW`, etc.

### StyleCode

- `BOLD` - Bold/emphasized text
- `DIM` - Dimmed text
- `ITALIC` - Italic text
- `UNDERLINE` - Underlined text
- `DOUBLE_UNDERLINE` - Double underlined text
- `REVERSE_VIDEO` - Swap foreground/background
- `INVISIBLE_TEXT` - Hidden text
- `STRIKETHROUGH` - Strikethrough text

### Background Colors

Background colors live in the BackgroundCode enum: `BackgroundCode.RED`, `BackgroundCode.BRIGHT_RED`, and so on

## Examples

### Success/Error Messages
```java
// Success
Clique.styleBuilder()
    .appendAndReset("✓ ", ColorCode.GREEN, StyleCode.BOLD)
    .appendAndReset("Build successful", ColorCode.WHITE)
    .print();

// Error
Clique.styleBuilder()
    .appendAndReset("✗ ", ColorCode.RED, StyleCode.BOLD)
    .appendAndReset("Build failed: ", ColorCode.RED)
    .appendAndReset("Missing dependency", ColorCode.WHITE, StyleCode.DIM)
    .print();
```

### Highlighted Output
```java
Clique.styleBuilder()
    .appendAndReset("Warning: ", ColorCode.YELLOW, StyleCode.BOLD)
    .appendAndReset("This action cannot be undone", ColorCode.WHITE)
    .print();
```

## See Also

- [Parser Documentation](parser.md) - Markup-based alternative to StyleBuilder
- [Markup Reference](markup-reference.md) - All available colors and styles