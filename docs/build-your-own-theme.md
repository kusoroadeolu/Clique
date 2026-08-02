# Build Your Own Theme

Clique's color system is open. If you've ever looked at a built-in theme and thought *I want that, but mine* — this is how you do it.

A theme is just a class that maps names to ANSI codes. That's it. Everything else — auto-discovery, `registerTheme()` by name, mixing with markup — comes for free once you wire it up.

---

## What you need

Just the SPI module:

```xml
<!-- Maven -->
<dependency>
    <groupId>io.github.kusoroadeolu</groupId>
    <artifactId>clique-spi</artifactId>
    <version>2.0.3</version>
</dependency>
```

```gradle
// Gradle
implementation 'io.github.kusoroadeolu:clique-spi:2.0.3'
```

If you want to reference the built-in themes while building yours, add `clique-themes` too — but it's optional.

---

## A theme in its simplest form

```java
public class MyTheme implements CliqueTheme {

    @Override public String themeName() { return "my-theme"; }

    @Override public String author() { return "your-name"; }

    @Override public String url() { return "https://github.com/you/my-theme"; }

    @Override
    public Map<String, AnsiCode> styles() {
        return Map.of(
            "mt_blue",    ansi(66, 135, 245, false),
            "mt_purple",  ansi(156, 39, 176, false),
            "bg_mt_blue", ansi(66, 135, 245, true)
        );
    }

    private AnsiCode ansi(int r, int g, int b, boolean bg) {
        int type = bg ? 48 : 38;
        String code = "\u001B[%d;2;%d;%d;%dm".formatted(type, r, g, b);
        return new Rgb(code);
    }

    private record Rgb(String code) implements AnsiCode {
        @Override public String ansiSequence() { return code; }
    }
}
```

`author()` and `url()` are metadata, they don't affect how colors render, but they're part of the interface and useful for discovery, attribution, and anyone inspecting themes at runtime. Return whatever makes sense for your project.

Register it, use it:

```java
Clique.registerTheme(new MyTheme());
Clique.parser().print("[mt_blue, bold]Hello from my theme[/]");
```

---

## Building something real

Solarized Dark, showing what a complete palette might look like in practice:

```java
public class SolarizedDarkTheme implements CliqueTheme {

    @Override public String themeName() { return "solarized-dark"; }

    @Override public String author() { return "ethan-schoonover"; }

    @Override public String url() { return "https://ethanschoonover.com/solarized"; }

    @Override
    public Map<String, AnsiCode> styles() {
        var colors = new HashMap<String, AnsiCode>();

        // Base tones
        put(colors, "sol_base03",  0,   43,  54);
        put(colors, "sol_base02",  7,   54,  66);
        put(colors, "sol_base01",  88,  110, 117);
        put(colors, "sol_base0",   131, 148, 150);
        put(colors, "sol_base1",   147, 161, 161);
        put(colors, "sol_base3",   253, 246, 227);

        // Accent colors
        put(colors, "sol_yellow",  181, 137, 0);
        put(colors, "sol_orange",  203, 75,  22);
        put(colors, "sol_red",     220, 50,  47);
        put(colors, "sol_magenta", 211, 54,  130);
        put(colors, "sol_violet",  108, 113, 196);
        put(colors, "sol_blue",    38,  139, 210);
        put(colors, "sol_cyan",    42,  161, 152);
        put(colors, "sol_green",   133, 153, 0);

        return colors;
    }

    // Registers both foreground and background in one call
    private void put(Map<String, AnsiCode> map, String name, int r, int g, int b) {
        map.put(name,        rgb(r, g, b, false));
        map.put("bg_" + name, rgb(r, g, b, true));
    }

    private AnsiCode rgb(int r, int g, int b, boolean bg) {
        String code = "\u001B[%d;2;%d;%d;%dm".formatted(bg ? 48 : 38, r, g, b);
        return new Rgb(code);
    }

    private record Rgb(String code) implements AnsiCode {
        @Override public String ansiSequence() { return code; }
    }
}
```
---

## Working from hex

If you're pulling colors from a design tool or a palette website, hex is usually what you have. Here's a helper that converts it directly:

```java
private void addHex(Map<String, AnsiCode> map, String name, String hex) {
    map.put(name,         hexToAnsi(hex, false));
    map.put("bg_" + name, hexToAnsi(hex, true));
}

private AnsiCode hexToAnsi(String hex, boolean bg) {
    hex = hex.startsWith("#") ? hex.substring(1) : hex;
    int r = Integer.parseInt(hex.substring(0, 2), 16);
    int g = Integer.parseInt(hex.substring(2, 4), 16);
    int b = Integer.parseInt(hex.substring(4, 6), 16);
    String code = "\u001B[%d;2;%d;%d;%dm".formatted(bg ? 48 : 38, r, g, b);
    return new Rgb(code);
}
```

Then your palette becomes just a itemList of names and hex values, readable, easy to update:

```java
addHex(colors, "corp_navy",    "#003366");
addHex(colors, "corp_gold",    "#FFB81C");
addHex(colors, "corp_success", "#2E7D32");
addHex(colors, "corp_error",   "#C62828");
```

---

## Auto-discovery

If you want your theme to work with `Clique.registerTheme("my-theme")` or `Clique.registerAvailableThemes()`, you need to tell Java's ServiceLoader where to find it.

Create this file:

**`src/main/resources/META-INF/services/io.github.kusoroadeolu.clique.spi.CliqueTheme`**

With one fully-qualified class name per line:

```
com.example.themes.SolarizedDarkTheme
com.example.themes.SolarizedLightTheme
```

That's all. After this, your theme is discoverable like any built-in one.

### Using JPMS?

Add a `provides` declaration to your `module-info.java`:

```java
module my.themes {
    requires clique.spi;
    provides io.github.kusoroadeolu.clique.spi.CliqueTheme
        with com.example.themes.SolarizedDarkTheme,
             com.example.themes.SolarizedLightTheme;
}
```

Keep the `META-INF/services` file too — it handles non-modular classpath scenarios.

---

## Naming things well

A few conventions worth following:

**Prefix everything with your theme's identifier.** Colors like `red` or `primary` will eventually collide with something.

```java
// Good
"sol_red", "sol_cyan", "sol_base03"

// Will cause problems eventually
"red", "cyan", "background"
```

**Background colors get the `bg_` prefix.** Always. It's what users expect after working with any other Clique theme.

```java
"sol_blue"     // foreground
"bg_sol_blue"  // background
```

**Theme names use lowercase with hyphens.**

```java
return "solarized-dark";   // ✓
return "SolarizedDark";    // ✗
return "solarized_dark";   // works but inconsistent with convention
```

---

## Testing it

Before shipping, run through every color to make sure nothing's invisible or broken:

```java
var theme = new MyTheme();
Clique.registerTheme(theme);

theme.styles().forEach((name, code) -> {
    if (name.startsWith("bg_")) {
        Clique.parser().print("[" + name + ", white] " + name + " [/]");
    } else {
        Clique.parser().print("[" + name + "] " + name + " [/]");
    }
});
```

It's rough, but it catches the common mistakes, missing `ansiSequence()`, forgotten colors, names that clash.

---

## Distributing it

### As a library

Package it as a standalone JAR. The structure is straightforward:

```
my-clique-themes/
├── src/main/java/com/example/themes/
│   ├── MyTheme.java
│   └── MyOtherTheme.java
└── src/main/resources/META-INF/services/
    └── io.github.kusoroadeolu.clique.spi.CliqueTheme
```

Users add it as a dependency, and `Clique.registerAvailableThemes()` picks it up automatically. No extra setup on their end.

### Just for your own project

Skip the ServiceLoader entirely and register directly by name after wiring up the service file, or just call `Clique.registerTheme("my-theme")` once the class is on the classpath.

---

## A note on terminal support

Themes use 24-bit RGB color. Most modern terminals handle this without any configuration — iTerm2, Alacritty, Kitty, Windows Terminal, recent GNOME Terminal all support it out of the box.

If colors look off, check that `COLORTERM=truecolor` is set in your shell profile. On Windows PowerShell, you may also need:

```powershell
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

---

## See Also

- [Themes](themes.md) — using the built-in themes
- [Markup Reference](markup-reference.md) — how to use your colors in markup
- [Parser](parser.md) — the parser that brings it all together