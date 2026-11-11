# Parser Composability

The parser knows about built-in colors and styles out of the box, but it's also fully open to extension. If you've ever wanted a `[brand-primary]` tag or a sunset gradient in your terminal, this is where that happens.

> **Note:** The examples below use `Clique.registerStyle()` and `Clique.registerStyles()`, which write into a global, shared registry visible to every parser in your application. This is fine for application-wide constants, but for styles scoped to a specific feature or module, consider building a [`StyleContext`](style-context.md) instead and attaching it to just the parsers that need it. Everything on this page works the same way whether you register it globally or scope it into a `StyleContext`.

## The AnsiCode Interface

Everything the parser understands is an `AnsiCode`. Colors, styles, themes, they all implement the same interface from `clique-spi`:

```java
public interface AnsiCode {
    String ansiSequence(); // return your ANSI escape sequence here
}
```

That's it. If your class returns an ANSI escape string from `ansiSequence()`, the parser will use it. Clique already gives you helpers for the common cases (RGB, hex, composition), covered below. Implementing the interface yourself is only needed for things Clique doesn't build in.

## Naming Your Styles

A few rules apply to every style you register, whether globally or through a `StyleContext`.

**Names are case-insensitive.** Names are lowercased when registered, and tags are lowercased when parsed. So a style registered as `"Brand"` is used as `[brand]`, and `[BRAND]` works too.

**A name can't contain the delimiter or brackets.** With the default delimiter (`,`), a name like `"a,b"` would be split into two styles before lookup. With `delimiter(' ')`, names can't contain spaces. `[` and `]` can never appear in a name.

**Avoid built-in names.** Resolution order is local styles, then global custom styles, then predefined styles. That means registering `"red"` or `"bold"` globally **overrides the built-in everywhere in your application**. Shadowing a built-in inside a `StyleContext` is safe, since only that parser is affected. Globally, pick your own names.

**Prefix backgrounds with `bg_`.** The built-in backgrounds and every theme follow this convention, so custom styles should too (`brand` and `bg_brand`).

**Strict parsing catches typos.** If a parser has `enableStrictParsing()` on, an unregistered tag like `[brnad]` throws `UnidentifiedStyleException` instead of being passed through silently. This is a useful safety net once you have a set of custom names.

## Custom Colors

### RGB and hex

For 24-bit color you don't need to implement anything. `Clique.rgb()` and `Clique.hex()` return a ready-made `AnsiCode`:

```java
Clique.registerStyle("brand", Clique.hex("#4287F5"));
Clique.registerStyle("accent", Clique.rgb(156, 39, 176));

Clique.parser().print("[brand]Primary[/] and [accent]secondary[/]");
```

Both have an overload that targets the background instead:

```java
Clique.registerStyle("bg_brand", Clique.hex("#4287F5", true));
Clique.registerStyle("bg_accent", Clique.rgb(156, 39, 176, true));

Clique.parser().print("[white, bg_brand] INFO [/]");
```

Hex strings must be exactly `#RRGGBB`. Anything else throws `IllegalArgumentException`. RGB components must be 0 to 255.

### 256-color and other custom codes

Clique doesn't have built-in 256-color support, but it's a good example of implementing `AnsiCode` yourself:

```java
public class Color256 implements AnsiCode {
    private final String code;

    public Color256(int index, boolean background) {
        if (index < 0 || index > 255) throw new IllegalArgumentException("Color index must be 0-255");
        this.code = "\u001B[%d;5;%dm".formatted(background ? 48 : 38, index);
    }

    @Override
    public String ansiSequence() { return code; }
}

Clique.registerStyle("orange", new Color256(214, false));
Clique.parser().print("[orange]Look ma, 256 colors[/]");
```

> **Tip:** Compute your escape sequence once in the constructor and just return the stored value in `ansiSequence()`. No need to recompute it on every call.

If `orange` is only meant to be used by one part of your app, put it in a `StyleContext` instead so other parsers don't pick it up:

```java
StyleContext ctx = StyleContext.builder()
    .add("orange", new Color256(214, false))
    .build();

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[orange]Look ma, 256 colors[/]");
```

## Composing Styles

`Clique.compose()` chains several `AnsiCode` values into one reusable style, applied left to right. This is great for semantic aliases that carry real meaning in your app:

```java
Clique.registerStyle("error", Clique.compose(ColorCode.BRIGHT_RED, StyleCode.BOLD));
Clique.registerStyle("warn",  Clique.compose(ColorCode.YELLOW, StyleCode.ITALIC));
Clique.registerStyle("ok",    Clique.compose(ColorCode.BRIGHT_GREEN, StyleCode.BOLD));

Clique.parser().print("[error]Build failed[/]");
Clique.parser().print("[ok]All tests passed[/]");
```

There's also an overload that takes a `Collection<AnsiCode>`. Since codes are applied in order, a later color overrides an earlier one.

`StyleContext`'s builder has composition built in, so you don't need `compose()` there. Passing multiple `AnsiCode` values to `add()` combines them for you:

```java
StyleContext ctx = StyleContext.builder()
    .add("error", ColorCode.BRIGHT_RED, StyleCode.BOLD)
    .add("warn",  ColorCode.YELLOW, StyleCode.ITALIC)
    .add("ok",    ColorCode.BRIGHT_GREEN, StyleCode.BOLD)
    .build();

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[error]Build failed[/]");
Clique.parser(config).print("[ok]All tests passed[/]");
```

See [StyleContext](style-context.md) for the full set of `add()` overloads.

## Dynamic Style Generation

Since styles are just `AnsiCode` instances, you can generate them programmatically. Here's a gradient built as a set of indexed tags, with a matching `bg_` variant for each step:

```java
public static Map<String, AnsiCode> gradient(String name, int[] from, int[] to, int steps) {
    if (steps < 2) throw new IllegalArgumentException("A gradient needs at least 2 steps");

    Map<String, AnsiCode> styles = new HashMap<>();
    for (int i = 0; i < steps; i++) {
        double t = (double) i / (steps - 1);
        int r = (int) (from[0] + t * (to[0] - from[0]));
        int g = (int) (from[1] + t * (to[1] - from[1]));
        int b = (int) (from[2] + t * (to[2] - from[2]));
        styles.put(name + "-" + i, Clique.rgb(r, g, b));
        styles.put("bg_" + name + "-" + i, Clique.rgb(r, g, b, true));
    }
    return styles;
}

// Coral to purple, 5 steps
Clique.registerStyles(gradient("sunset", new int[]{255, 94, 77}, new int[]{138, 35, 135}, 5));

Clique.parser().print("[sunset-0]█[/][sunset-1]█[/][sunset-2]█[/][sunset-3]█[/][sunset-4]█[/]");
```

The `steps < 2` check matters: with a single step, `steps - 1` is zero and the interpolation breaks down.

A generated gradient is a good candidate for scoping, since the indexed names (`sunset-0`, `sunset-1`, ...) are easy to collide with something else registered globally. Hand the same map to `StyleContext.from()` instead of `Clique.registerStyles()` to keep it contained:

```java
StyleContext ctx = StyleContext.from(
    gradient("sunset", new int[]{255, 94, 77}, new int[]{138, 35, 135}, 5)
);

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[sunset-0]█[/][sunset-1]█[/][sunset-2]█[/][sunset-3]█[/][sunset-4]█[/]");
```

> **Gradient text:** If you just want a gradient across a string, you don't need indexed tags. `Ink` interpolates per character for you: `Clique.ink().gradient("#FF5E4D", "#8A2387").on("Gradient text")`. See [Ink](ink.md). Indexed tags are for when you need a gradient *inside markup*, mixed with other tags.

## Loading Styles from Config

If you want your styles to live outside the codebase, a `colors.json` or a `.theme` file, you can load and register them at startup. Storing hex strings keeps the file short and easy to edit:

```java
// colors.json
// {
//   "brand":  "#4287F5",
//   "accent": "#9C27B0"
// }

JsonObject styles = JsonParser.parseString(Files.readString(Path.of("colors.json"))).getAsJsonObject();
Map<String, AnsiCode> loaded = new HashMap<>();

for (var entry : styles.entrySet()) {
    String hex = entry.getValue().getAsString();
    loaded.put(entry.getKey(), Clique.hex(hex));
    loaded.put("bg_" + entry.getKey(), Clique.hex(hex, true));
}

Clique.registerStyles(loaded);
Clique.parser().print("[brand]Styled from config[/]");
```

An invalid hex string throws `IllegalArgumentException` at load time, so a typo in the file fails at startup instead of rendering wrong colors later.

If `brand` and `accent` really are constants shared across your whole application, this is a reasonable use of the global registry. If instead they belong to one specific part of the app, build a `StyleContext` from the same map:

```java
StyleContext ctx = StyleContext.from(loaded);

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[brand]Styled from config[/]");
```

## Using Custom Styles with Ink

Custom styles aren't limited to the parser. [`Ink`](ink.md) resolves names from the same places: its own `StyleContext`, then the global registry, then the built-ins.

```java
// Globally registered styles work with the default Ink
Clique.registerStyle("error", Clique.compose(ColorCode.BRIGHT_RED, StyleCode.BOLD));
Clique.ink().of("error").on("Build failed");

// Scoped styles need an Ink built with that context
StyleContext ctx = StyleContext.builder()
    .add("highlight", ColorCode.YELLOW)
    .build();

Clique.ink(ctx).of("highlight").on("Look here");
```

If you already hold an `AnsiCode`, skip the name lookup entirely with `Ink.of(AnsiCode)`:

```java
Clique.ink().of(Clique.hex("#4287F5")).bold().on("No registration needed");
```

An unknown name passed to `of(String)` throws `UnidentifiedStyleException`.

## Keeping Registration Clean

If your app registers a lot of custom styles globally, it's worth centralizing that into a single class with a guard so it only runs once:

```java
public class AppStyles {
    private static boolean registered = false;

    public static synchronized void register() {
        if (registered) return;

        Clique.registerStyles(Map.of(
            "error", Clique.compose(ColorCode.BRIGHT_RED, StyleCode.BOLD),
            "warn",  Clique.compose(ColorCode.YELLOW, StyleCode.ITALIC),
            "ok",    Clique.compose(ColorCode.BRIGHT_GREEN, StyleCode.BOLD)
        ));

        registered = true;
    }
}

// Somewhere at startup
AppStyles.register();
```

Registering the same styles twice is harmless, since each name is simply put into the registry again. The real risk is two different places registering *different* values under the same name, where whichever runs last wins. Keeping registration in one class makes that easy to spot.

If your styles don't need to be global, a `StyleContext` sidesteps the problem entirely, since it's just a value you construct and pass around, with no shared state and no guard needed:

```java
public class AppStyles {
    public static final StyleContext CONTEXT = StyleContext.builder()
        .add("error", ColorCode.BRIGHT_RED, StyleCode.BOLD)
        .add("warn",  ColorCode.YELLOW, StyleCode.ITALIC)
        .add("ok",    ColorCode.BRIGHT_GREEN, StyleCode.BOLD)
        .build();
}

// Wherever a parser needs these styles
ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(AppStyles.CONTEXT)
    .build();

Clique.parser(config).print("[error]Build failed[/]");
```

`AppStyles.CONTEXT` is immutable once built, so there's nothing to guard. It's built exactly once as a static field, and every parser that uses it sees the same fixed set of styles.

## See Also

- [Parser](parser.md) - Basic usage, configuration, and syntax
- [StyleContext](style-context.md) - Scoped, immutable custom styles, and why to prefer them over global registration
- [Ink](ink.md) - Chainable styling without markup, including gradients and named styles
- [Markup Reference](markup-reference.md) - Built-in colors and styles
- [Themes](themes.md) - Pre-built palettes you can register and use
- [Build Your Own Theme](build-your-own-theme.md) - If your styles form a full palette you want to distribute, package them as a theme instead