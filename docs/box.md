# Boxes

Boxes are single-cell containers that display text with borders. They're perfect for displaying standalone messages, warnings, or long-form text.

## Box Types

Clique provides 4 built-in box styles:

1. **ASCII** - Standard box with ASCII borders
2. **CLASSIC** - Classic box style
3. **ROUNDED** - Box with rounded corners (default)
4. **DOUBLE_LINE** - Box with double-line borders


## Basic Usage

### Creating a Simple Box
```java
Box box = Clique.box()
        .content("This is my first box");

box.render(); // Print the box to terminal
```

### Box Dimensions

- **Width** - The horizontal size of the box (in characters)
- **Height** - The vertical size of the box (in lines)
```java
Clique.box()
    .dimensions(50, 5)  // Width, height
    .content("A wider, shorter box")
    .render();
```


### Using Markup in Boxes

Boxes automatically parse markup tags in content:
```java
Clique.box()
    .dimensions(40, 5)
    .content("[yellow, bold]Warning:[/] This is an important message that needs attention")
    .render();
```

### Multi-line Content

Boxes handle newlines properly and will autosize to the text if no dimensions are set accordingly:
```java
Clique.box()
    .content(
        """
        [green, bold]Success![/]
        Your operation completed successfully.
        You can now proceed to the next step.
        """
    )
    .render();
```

### Text Alignment

Boxes support a range of text alignments, with the default being centered:
```java
Clique.box()
    .content(
        """
        [green, bold]Success![/]
        Your operation completed successfully.
        You can now proceed to the next step.
        """, TextAlign.CENTER
    )
    .render();
```

Available alignments:
- `TextAlign.TOP_LEFT`, `TextAlign.TOP_CENTER`, `TextAlign.TOP_RIGHT`
- `TextAlign.CENTER_LEFT`, `TextAlign.CENTER`, `TextAlign.CENTER_RIGHT`
- `TextAlign.BOTTOM_LEFT`, `TextAlign.BOTTOM_CENTER`, `TextAlign.BOTTOM_RIGHT`

Default `TextAlign` is `CENTER`

## Box Configuration

Use `BoxConfiguration` to customize box appearance and behavior. You can access the builder via `BoxConfiguration.builder()`, which returns a `BoxConfigurationBuilder`.

### Default Values

| Option | Default |
|---|---|
| `textAlign` | `TextAlign.CENTER` |
| `parser` | `MarkupParser.DEFAULT` |
| `borderColor` | `{}` (no color) |
| `padding` | `2` |

### Full Configuration Example
```java
BoxConfiguration config = BoxConfiguration.builder()
    .borderColor("blue")
    .textAlign(TextAlign.CENTER)
    .parser(Clique.parser())
    .build();

Clique.box(BoxType.DOUBLE_LINE, config)
    .content("[bold, blue]This is a configured box[/]")
    .render();
```

## Examples

### Alert Box
```java
Clique.box(BoxType.DOUBLE_LINE)
    .content("[red, bold]⚠ ALERT ⚠[/]\n\nSystem maintenance in progress")
    .render();
```

### Info Box
```java
Clique.box()
    .dimensions(60, 10)
    .content(
        "[blue, bold]ℹ Information[/]\n\n" +
        "This feature is currently in beta. " +
        "Please report any issues you encounter."
    )
    .render();
```

## Things to Watch Out For
- `Clique.box()` defaults to `BoxType.ROUNDED`
- `null` values for `textAlign`, `parser`, or `borderColor` in the builder will throw a `NullPointerException`
- Padding is deducted from the box's given width, not added on top of it

## See Also

- [Markup Reference](markup-reference.md) - Styling options for box content
- [Parser Documentation](parser.md) - How markup parsing works