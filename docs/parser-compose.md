# Parser Composability

The parser knows about built-in colors and styles out of the box, but it's also fully open to extension. If you've ever wanted a `[brand-primary]` tag or a sunset gradient in your terminal, this is where that happens.

> **Note:** The examples below use `Clique.registerStyle()` and `Clique.registerStyles()`, which write into a global, shared registry visible to every parser in your application. This is fine for application-wide constants, but for styles scoped to a specific feature or module, consider building a [`StyleContext`](style-context.md) instead and attaching it to just the parsers that need it. Everything on this page, `AnsiCode` implementations, composite styles, generated gradients, config-loaded colors, works the same way whether you register it globally or scope it into a `StyleContext`.

## The AnsiCode Interface

Everything the parser understands is an `AnsiCode`. Colors, styles, themes, they all implement the same interface from `clique-spi`:

```java
public interface AnsiCode {
    String ansiSequence(); // return your ANSI escape sequence here
}
```

That's it. If your class returns an ANSI escape string from `ansiSequence()`, the parser will use it. This means you can plug in anything: RGB colors, 256-color codes, composite styles, values loaded from a config file, as long as it speaks ANSI.

> **Tip:** Compute your escape sequence once in the constructor and just return the stored value in `ansiSequence()`. No need to recompute it on every call.

## Custom Colors

Clique ships with `RGBAnsiColor` for 24-bit color support, and you can create it directly via `Clique.rgb()`. But if you need something lower-level, implementing `AnsiCode` yourself is straightforward:

```java
// 256-color support for older terminals
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

If `orange` is only meant to be used by one part of your app, register it into a `StyleContext` instead so other parsers don't pick it up:
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

Multiple `AnsiCode` instances can be chained together into a single reusable style. This is great for semantic aliases that carry real meaning in your app:

```java
public class CompositeStyle implements AnsiCode {
    private final String code;

    public CompositeStyle(AnsiCode... codes) {
        var sb = new StringBuilder();
        for (AnsiCode c : codes) sb.append(c);
        this.code = sb.ansiSequence();
    }

    @Override
    public String ansiSequence() { return code; }
}

// Semantic styles that actually mean something
Clique.registerStyle("error", new CompositeStyle(ColorCode.BRIGHT_RED, StyleCode.BOLD));
Clique.registerStyle("warn",  new CompositeStyle(ColorCode.YELLOW, StyleCode.ITALIC));
Clique.registerStyle("ok",    new CompositeStyle(ColorCode.BRIGHT_GREEN, StyleCode.BOLD));

Clique.parser().print("[error]Build failed[/]");
Clique.parser().print("[ok]All tests passed[/]");
```

`StyleContext`'s builder actually has this composition built in, so you often don't need a custom `CompositeStyle` class at all. Passing multiple `AnsiCode` values to `add()` combines them for you:
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

Since styles are just `AnsiCode` instances, you can generate them programmatically. Here's a gradient registered as a set of indexed tags:

```java
public static void registerGradient(String name, int[] from, int[] to, int steps) {
    Map<String, AnsiCode> gradient = new HashMap<>();

    for (int i = 0; i < steps; i++) {
        double t = (double) i / (steps - 1);
        int r = (int) (from[0] + t * (to[0] - from[0]));
        int g = (int) (from[1] + t * (to[1] - from[1]));
        int b = (int) (from[2] + t * (to[2] - from[2]));
        gradient.put(name + "-" + i, Clique.rgb(r, g, b));
    }

    Clique.registerStyles(gradient);
}

// Coral to purple, 5 steps
registerGradient("sunset", new int[]{255, 94, 77}, new int[]{138, 35, 135}, 5);

Clique.parser().print("[sunset-0]█[/][sunset-1]█[/][sunset-2]█[/][sunset-3]█[/][sunset-4]█[/]");
```

A generated gradient is a good candidate for scoping, since the indexed names (`sunset-0`, `sunset-1`, ...) are easy to accidentally collide with something else registered globally. Building the map and handing it to a `StyleContext` instead of `Clique.registerStyles()` keeps it contained:
```java
public static StyleContext gradientContext(String name, int[] from, int[] to, int steps) {
    Map<String, AnsiCode> gradient = new HashMap<>();

    for (int i = 0; i < steps; i++) {
        double t = (double) i / (steps - 1);
        int r = (int) (from[0] + t * (to[0] - from[0]));
        int g = (int) (from[1] + t * (to[1] - from[1]));
        int b = (int) (from[2] + t * (to[2] - from[2]));
        gradient.put(name + "-" + i, Clique.rgb(r, g, b));
    }

    return StyleContext.from(gradient);
}

StyleContext ctx = gradientContext("sunset", new int[]{255, 94, 77}, new int[]{138, 35, 135}, 5);

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[sunset-0]█[/][sunset-1]█[/][sunset-2]█[/][sunset-3]█[/][sunset-4]█[/]");
```

## Loading Styles from Config

If you want your styles to live outside the codebase, a `colors.json` or a `.theme` file, you can load and register them at startup:

```java
// colors.json
// {
//   "brand":   { "r": 66,  "g": 135, "b": 245 },
//   "accent":  { "r": 156, "g": 39,  "b": 176 }
// }

JsonObject styles = JsonParser.parseString(Files.readString(Path.of("colors.json"))).getAsJsonObject();
Map<String, AnsiCode> loaded = new HashMap<>();

for (var entry : styles.entrySet()) {
    JsonObject c = entry.getValue().getAsJsonObject();
    loaded.put(entry.getKey(), Clique.rgb(
        c.get("r").getAsInt(),
        c.get("g").getAsInt(),
        c.get("b").getAsInt()
    ));
}

Clique.registerStyles(loaded);
Clique.parser().print("[brand]Styled from config[/]");
```

If `brand` and `accent` really are constants shared across your whole application, this is a reasonable use of the global registry. If instead they belong to one specific part of the app, build a `StyleContext` from the same map instead of registering globally:
```java
StyleContext ctx = StyleContext.from(loaded);

ParserConfiguration config = ParserConfiguration.builder()
    .styleContext(ctx)
    .build();

Clique.parser(config).print("[brand]Styled from config[/]");
```

## Keeping Registration Clean

If your app registers a lot of custom styles globally, it's worth centralizing that into a single class with a guard so it only runs once:

```java
public class AppStyles {
    private static boolean registered = false;

    public static synchronized void register() {
        if (registered) return;

        Clique.registerStyles(Map.of(
            "error", new CompositeStyle(ColorCode.BRIGHT_RED, StyleCode.BOLD),
            "warn",  new CompositeStyle(ColorCode.YELLOW, StyleCode.ITALIC),
            "ok",    new CompositeStyle(ColorCode.BRIGHT_GREEN, StyleCode.BOLD)
        ));

        registered = true;
    }
}

// Somewhere at startup
AppStyles.register();
```

The guard exists because `Clique.registerStyles()` writes into shared global state, and calling it more than once, or from more than one place, risks re-registering or overwriting styles unexpectedly. This is a reasonable pattern if you genuinely need application-wide styles available everywhere.

If your styles don't need to be global, building a `StyleContext` sidesteps the problem entirely, since it's just a value you construct and pass around like any other object, with no shared state and no guard needed:
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
- [Markup Reference](markup-reference.md) - Built-in colors and styles
- [Themes](themes.md) - Pre-built palettes you can register and use