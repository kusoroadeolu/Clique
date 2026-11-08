# Markup Reference

This guide covers all supported markup options for styling text in Clique. Use these inside `[...]` tags when working with the parser.

## Text Colors

### Standard Colors

Use standard color names for basic ANSI text colors:

| Color | Tag | Example |
|-------|-----|---------|
| Red | `red` | `[red]Text[/]` |
| Green | `green` | `[green]Text[/]` |
| Yellow | `yellow` | `[yellow]Text[/]` |
| Blue | `blue` | `[blue]Text[/]` |
| Magenta | `magenta` | `[magenta]Text[/]` |
| Cyan | `cyan` | `[cyan]Text[/]` |
| White | `white` | `[white]Text[/]` |
| Black | `black` | `[black]Text[/]` |

### Bright Colors

Prefix color names with `*` for brighter versions:

| Color | Tag | Example |
|-------|-----|---------|
| Bright Red | `*red` | `[*red]Text[/]` |
| Bright Green | `*green` | `[*green]Text[/]` |
| Bright Yellow | `*yellow` | `[*yellow]Text[/]` |
| Bright Blue | `*blue` | `[*blue]Text[/]` |
| Bright Magenta | `*magenta` | `[*magenta]Text[/]` |
| Bright Cyan | `*cyan` | `[*cyan]Text[/]` |
| Bright White | `*white` | `[*white]Text[/]` |
| Bright Black | `*black` | `[*black]Text[/]` |

**Example:**
```java
Clique.parser().print("[red]Standard red[/] vs [*red]Bright red[/]");
```

## Background Colors

### Standard Backgrounds

Prefix color names with `bg_` for background colors:

| Color | Tag | Example |
|-------|-----|---------|
| Red Background | `bg_red` | `[bg_red]Text[/]` |
| Green Background | `bg_green` | `[bg_green]Text[/]` |
| Yellow Background | `bg_yellow` | `[bg_yellow]Text[/]` |
| Blue Background | `bg_blue` | `[bg_blue]Text[/]` |
| Magenta Background | `bg_magenta` | `[bg_magenta]Text[/]` |
| Cyan Background | `bg_cyan` | `[bg_cyan]Text[/]` |
| White Background | `bg_white` | `[bg_white]Text[/]` |
| Black Background | `bg_black` | `[bg_black]Text[/]` |

### Bright Backgrounds

Use `*bg_` prefix for bright background colors:

| Color | Tag | Example |
|-------|-----|---------|
| Bright Red BG | `*bg_red` | `[*bg_red]Text[/]` |
| Bright Green BG | `*bg_green` | `[*bg_green]Text[/]` |
| Bright Yellow BG | `*bg_yellow` | `[*bg_yellow]Text[/]` |
| Bright Blue BG | `*bg_blue` | `[*bg_blue]Text[/]` |
| Bright Magenta BG | `*bg_magenta` | `[*bg_magenta]Text[/]` |
| Bright Cyan BG | `*bg_cyan` | `[*bg_cyan]Text[/]` |
| Bright White BG | `*bg_white` | `[*bg_white]Text[/]` |
| Bright Black BG | `*bg_black` | `[*bg_black]Text[/]` |

**Example:**
```java
Clique.parser().print("[bg_red, white]Alert![/]");
Clique.parser().print("[*bg_blue, *white]Bright background[/]");
```

## Text Styles

Apply various text effects using these style tags:

| Style            | Tag      | Description                                      |
|------------------|----------|--------------------------------------------------|
| Bold             | `bold`   | Makes text bold/emphasized                       |
| Dim              | `dim`    | Reduces text brightness                          |
| Italic           | `italic` | Slants the text (not all terminals support this) |
| Underline        | `ul`     | Underlines text                                  |
| Reverse Video    | `rv`     | Swaps foreground and background colors           |
| Invisible        | `inv`    | Hides text (useful for debugging)                |
| Reset            | `/`      | Resets all styles                                |
| Double Underline | `dbl_ul` | Underlines a text twice                          |
| Strike           | `strike` | Strikes through a text                           |


**Example:**
```java
Clique.parser().print("[bold, ul]Important[/] [dim]subtle note[/]");
Clique.parser().print("[rv]Inverted colors![/]");
```

## Combining Styles

You can combine multiple styles, colors, and backgrounds in a single tag:

```java
// Combine color and style
Clique.parser().print("[red, bold]Bold red text[/]");

// Combine foreground, background, and style
Clique.parser().print("[white, bg_blue, bold]White on blue, bold[/]");

// Multiple styles
Clique.parser().print("[red, bold, ul, italic]Everything at once[/]");
```

## Quick Reference Table

| Category | Example Syntax | Result |
|----------|----------------|--------|
| Text Color | `[red]Text[/]` | Red text |
| Bright Color | `[*blue]Text[/]` | Bright blue text |
| Background | `[bg_yellow, black]Text[/]` | Black text on yellow background |
| Bright Background | `[*bg_green, white]Text[/]` | White text on bright green background |
| Style | `[bold, ul, red]Text[/]` | Red, bold, and underlined |
| Reset | `[red]Text[/]` | Resets style after closing tag |

Clique automatically detects ANSI support and can be manually controlled:

```java
Clique.enableCliqueColors();  // Force enable colors
Clique.enableCliqueColors(false); // Force disable colors
```
## Terminal Compatibility
For all colors to display correctly, your terminal must support truecolor (24-bit color). Most modern terminals support this by default, but you may need to enable it:

- Check support: Run `echo $COLORTERM` - it should output truecolor or 24bit
- Enable truecolor: Set the environment variable `COLORTERM=truecolor` in your shell profile
- Terminal compatibility: Ensure your terminal emulator supports 24-bit color (most modern terminals like iTerm2, Alacritty, Kitty, Windows Terminal, and recent versions of GNOME Terminal do)
  Without truecolor support, themes may appear with reduced color accuracy or fall back to the nearest 256-color approximation.

  
## See Also

- [Parser Documentation](parser.md) - Learn how to use the parser
- [StyleBuilder Documentation](stylebuilder.md) - Alternative to the markup parser