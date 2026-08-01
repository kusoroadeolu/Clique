# Themes

Clique ships with seven handpicked terminal color schemes. Drop one in, and every color in that palette becomes available in your markup tags, no ANSI codes, no manual wiring.

---

## Setup

Themes live in a separate package from core. You need both:

```xml
<!-- Maven -->
<dependencies>
    <dependency>
        <groupId>io.github.kusoroadeolu</groupId>
        <artifactId>clique-core</artifactId>
        <version>4.0.2</version>
    </dependency>
    <dependency>
        <groupId>io.github.kusoroadeolu</groupId>
        <artifactId>clique-themes</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

```gradle
// Gradle
implementation 'io.github.kusoroadeolu:clique-core:4.0.3'
implementation 'io.github.kusoroadeolu:clique-themes:1.0.1'
```

---

## Available themes

| Name | Description                                     |
|------|-------------------------------------------------|
| `catppuccin-mocha` | Pastel dark, warm, soothing, easy on the eyes   |
| `catppuccin-latte` | Pastel light, the mocha palette in daylight     |
| `dracula` | High-contrast dark with punchy purples          |
| `gruvbox-dark` | Retro warm darks and earthy accents             |
| `gruvbox-light` | The gruvbox palette flipped light               |
| `nord` | Cool, arctic blues and muted greens             |
| `tokyo-night` | Deep purples and electric blues, very cyberpunk |

---

## Using a theme

Register by name, then use its colors anywhere markup is accepted:

```java
Clique.registerTheme("catppuccin-mocha");

Clique.parser().print("[ctp_mauve, bold]Welcome[/]");
Clique.parser().print("[ctp_red]Error:[/] [ctp_text]Something went wrong[/]");
Clique.parser().print("[bg_ctp_surface0, ctp_blue] INFO [/] Starting up...");
```

Need more than one? Register them all at once:

```java
Clique.registerAvailableThemes();

Clique.parser().print("[ctp_mauve]Catppuccin[/] meets [tokyo_cyan]Tokyo Night[/]");
Clique.parser().print("[nord_frost0]Nord[/] meets [drac_pink]Dracula[/]");
```

---

## Color reference

Every theme color has a matching background variant with the `bg_` prefix. So `ctp_blue` becomes `bg_ctp_blue`, `nord_frost2` becomes `bg_nord_frost2`, and so on.

### Catppuccin (Mocha & Latte)

Both variants share the same color names — just different RGB values underneath.

**Accents:** `ctp_rosewater`, `ctp_flamingo`, `ctp_pink`, `ctp_mauve`, `ctp_red`, `ctp_maroon`, `ctp_peach`, `ctp_yellow`, `ctp_green`, `ctp_teal`, `ctp_sky`, `ctp_sapphire`, `ctp_blue`, `ctp_lavender`

**Surface & text:** `ctp_text`, `ctp_subtext1`, `ctp_subtext0`, `ctp_overlay2`, `ctp_overlay1`, `ctp_overlay0`, `ctp_surface2`, `ctp_surface1`, `ctp_surface0`, `ctp_base`, `ctp_mantle`, `ctp_crust`

```java
Clique.parser().print("[bg_ctp_surface0, ctp_text] Package Manager [/]");
Clique.parser().print("[ctp_overlay0]├──[/] [ctp_blue]Installing dependencies...[/]");
Clique.parser().print("[ctp_overlay0]└──[/] [ctp_green]✓ Complete[/]");
```

### Dracula

**Standard:** `drac_black`, `drac_red`, `drac_green`, `drac_yellow`, `drac_blue`, `drac_magenta`, `drac_cyan`, `drac_white`

**Bright (prefix with `*`):** `*drac_red`, `*drac_green`, `*drac_cyan`, etc.

```java
Clique.parser().print("[drac_magenta]♦[/] [*drac_white, bold]Dracula[/]");
Clique.parser().print("[drac_red]✗[/] Failed  [drac_green]✓[/] Success");
```

### Gruvbox (Dark & Light)

**Primary:** `gb_red`, `gb_green`, `gb_yellow`, `gb_blue`, `gb_purple`, `gb_aqua`, `gb_gray`, `gb_orange`

**Bright (prefix with `*`):** `*gb_red`, `*gb_orange`, `*gb_aqua`, etc.

**Backgrounds:** `gb_bg`, `gb_bg0_h`, `gb_bg0_s`, `gb_bg1`, `gb_bg2`, `gb_bg3`, `gb_bg4`

**Foregrounds:** `gb_fg`, `gb_fg0`, `gb_fg1`, `gb_fg2`, `gb_fg3`, `gb_fg4`

```java
Clique.parser().print("[gb_orange, bold]Warning:[/] [gb_fg]Check configuration[/]");
Clique.parser().print("[bg_gb_bg1, gb_aqua] → [/] [gb_yellow]Processing...[/]");
```

### Nord

**Polar Night (dark backgrounds):** `nord_polar0`, `nord_polar1`, `nord_polar2`, `nord_polar3`

**Snow Storm (light foregrounds):** `nord_snow`

**Frost (blues & cyans):** `nord_frost0`, `nord_frost1`, `nord_frost2`, `nord_frost3`

**Aurora (accents):** `nord_red`, `nord_orange`, `nord_yellow`, `nord_green`, `nord_purple`

```java
Clique.parser().print("[nord_frost2]❄[/] [nord_snow]Nord[/]");
Clique.parser().print("[bg_nord_polar1, nord_frost0] INFO [/] [nord_snow]Cool and minimal[/]");
```

### Tokyo Night

**Standard:** `tokyo_black`, `tokyo_red`, `tokyo_green`, `tokyo_yellow`, `tokyo_blue`, `tokyo_magenta`, `tokyo_cyan`, `tokyo_white`

**Bright (prefix with `*`):** `*tokyo_red`, `*tokyo_cyan`, `*tokyo_white`, etc.

**Base:** `tokyo_bg`, `tokyo_fg`

```java
Clique.parser().print("[tokyo_magenta]◆[/] [*tokyo_white, bold]Tokyo Night[/]");
Clique.parser().print("[tokyo_cyan]›[/] [tokyo_fg]Modern and clean[/]");
```

---

## Combining with text styles

Theme colors compose with all of Clique's standard styles — bold, italic, underline, dim, all of it:

```java
Clique.parser().print("[ctp_mauve, bold, ul]Important Heading[/]");
Clique.parser().print("[gb_orange, bg_gb_bg1, bold] WARNING [/]");
Clique.parser().print("[nord_red, italic]Something subtle[/]");
```

---

## A few patterns worth knowing

**Semantic aliases.** Theme color names are expressive but verbose. For a real app, it's worth mapping them to names that mean something in your domain:

```java
Clique.registerTheme("nord");

Clique.registerStyle("error",   new RGBColor(191, 97,  106, false)); // nord_red
Clique.registerStyle("success", new RGBColor(163, 190, 140, false)); // nord_green
Clique.registerStyle("info",    new RGBColor(136, 192, 208, false)); // nord_frost2

Clique.parser().print("[error]Failed to connect[/]");
Clique.parser().print("[success]Connection established[/]");
```

**Pick a side on light vs dark.** Catppuccin Mocha and Latte share the same color names, so swapping between them is one line — but don't register both at once or you'll get whichever loaded last.

---

## Discovering themes at runtime

List everything available on the classpath:

```java
List<CliqueTheme> themes = Clique.findAvailableThemes();
themes.forEach(t -> System.out.println(t.themeName() + " by " + t.author()));
```

Check for a specific one before using it:

```java
Clique.findTheme("tokyo-night").ifPresentOrElse(
    theme -> Clique.registerTheme(theme.themeName()),
    ()    -> System.out.println("tokyo-night not found")
);
```

---

## Terminal requirements

Themes use 24-bit RGB color. Most modern terminals handle this without any setup. iTerm2, Alacritty, Kitty, Windows Terminal, and recent GNOME Terminal all support it out of the box. If colors look off, make sure `COLORTERM=truecolor` is set in your shell profile.

---

## See Also

- [Build Your Own Theme](build-your-own-theme.md) — create and distribute custom themes
- [Markup Reference](markup-reference.md) — all built-in colors and styles
- [Parser](parser.md) — how markup parsing works