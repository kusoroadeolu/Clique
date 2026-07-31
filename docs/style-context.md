# Custom Styles with StyleContext

Clique lets you register your own named styles so you can write `[highlight]` or `[brand-primary]` instead of raw ANSI codes or repeated `StyleBuilder` calls. There are two ways to do this: a global registry (`Clique.registerStyle()`), and `StyleContext`, which is scoped to a single parser.

This doc covers `StyleContext`, and why it's the better default for most applications.

## Why scoping matters

`Clique.registerStyle()` writes into a single, shared, global table of styles. Every parser in your application reads from that same table. This is convenient for quick scripts, but it has a real cost as an application grows:

- Any code, anywhere, can register a style with the same name and silently overwrite what another part of your app registered.
- A style registered by one module is visible to every other module, whether that module wants it or not.
- Tests that register styles can leak state into other tests unless someone remembers to clean up.
- It's hard to look at a piece of code and know which styles are actually available to it, since the answer depends on global state that could have been mutated by something else entirely.

`StyleContext` avoids all of this. It's an immutable, self-contained bundle of styles that you build once and attach to a specific parser. Nothing outside that parser can see or change it, and it can't be mutated after it's built.

**Recommendation:** use `StyleContext` for styles that belong to a specific feature, module, or component. Reserve the global registry (`Clique.registerStyle()`) for things that are genuinely constant across your whole application, like a fixed brand color palette that every part of the app should agree on.

## Basic Usage

Build a context, then attach it to a `ParserConfiguration`:

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

The styles in `ctx` are only usable through parsers configured with it. A parser built from a different configuration, or the default `Clique.parser()`, has no idea `highlight` or `muted` exist.

If you only need to register one style and don't want to build a full context, `ParserConfiguration` also has a shortcut:

```java
ParserConfiguration config = ParserConfiguration.builder()
        .addStyle("highlight", ColorCode.YELLOW)
        .build();
```

## Building a StyleContext

### From individual styles

The builder's `add()` method has a few overloads depending on what you're registering.

**A single `AnsiCode`:**
```java
StyleContext ctx = StyleContext.builder()
        .add("highlight", ColorCode.YELLOW)
        .build();
```

**Multiple `AnsiCode` values combined into one style**, using varargs:
```java
StyleContext ctx = StyleContext.builder()
        .add("error", ColorCode.BRIGHT_RED, StyleCode.BOLD)
        .build();
```

This combines the given codes into a single composite style stored under one name. `[error]` will apply both the color and the bold styling together.

**A collection of `AnsiCode` values**, if you already have them in a `List` or similar:
```java
List<AnsiCode> codes = List.of(ColorCode.BRIGHT_RED, StyleCode.BOLD);

StyleContext ctx = StyleContext.builder()
        .add("error", codes)
        .build();
```

All three forms end up storing exactly one `AnsiCode` per name. The multi-argument forms just combine their inputs first.

### From a map

If you already have styles as a `Map<String, AnsiCode>`, you can bulk-register them:
```java
Map<String, AnsiCode> styles = Map.of(
        "highlight", ColorCode.YELLOW,
        "muted", StyleCode.DIM
);

StyleContext ctx = StyleContext.builder()
        .add(styles)
        .build();
```

If a name in the map matches one you already registered on the same builder, the map's value wins.

There's also a static shortcut that skips the builder entirely:
```java
StyleContext ctx = StyleContext.from(styles);
```

### From another StyleContext

You can merge one context's styles into another while building:
```java
StyleContext base = StyleContext.builder()
        .add("highlight", ColorCode.YELLOW)
        .build();

StyleContext extended = StyleContext.builder()
        .add(base)
        .add("muted", StyleCode.DIM)
        .build();
```

If you just want an independent copy of an existing context, use the static factory instead:
```java
StyleContext copy = StyleContext.from(base);
```

`copy` holds its own internal map. Nothing you do to `base` afterward (not that you can mutate it anyway, see below) will affect `copy`, or vice versa.

### From a theme

If you have a registered theme and want its colors available as a scoped context instead of globally, look it up by name:
```java
StyleContext ctx = StyleContext.fromTheme("catppuccin-mocha");
```

This throws `NoSuchThemeException` if no theme with that name can be found.

### An empty context

`StyleContext.NONE` is a ready-made empty context. Passing it to `styleContext()` is a no-op, it simply means no local styles are added to that parser's resolution chain:
```java
ParserConfiguration config = ParserConfiguration.builder()
        .styleContext(StyleContext.NONE)
        .build();
```

This is mostly useful as an explicit default value, for example if you're writing a method that accepts an optional `StyleContext` parameter.

## Immutability

Once `build()` is called, the resulting `StyleContext` cannot be changed. There is no `remove()` or `set()` method on `StyleContext` itself, only on the builder, before `build()` is called. If you need a modified version of an existing context, build a new one:
```java
StyleContext updated = StyleContext.builder()
        .add(existing)
        .add("new-style", ColorCode.CYAN)
        .build();
```

This is deliberate. Since a `StyleContext` might be attached to a `ParserConfiguration` that's shared or reused, letting it change out from under callers would defeat the point of scoping styles in the first place. The class is documented as immutable and thread-safe. `StyleContextBuilder` is not thread-safe on its own, if you're sharing a single builder instance across threads, you need to synchronize access to it yourself.

## Style Resolution Order

When a parser encounters a markup tag, it checks for a matching style in this order:

1. **Local styles** from the parser's attached `StyleContext` (or via `addStyle()`)
2. **Global custom styles** registered with `Clique.registerStyle()`
3. **Predefined styles** (Clique's built-in colors and text styles)

Local styles always win. If you register `highlight` in a `StyleContext` and something with the same name also exists in the global registry, the parser uses your local one.

This means a `StyleContext` also works as a safe way to override or shadow a global style for a specific parser, without touching the global registry or affecting any other parser in your application.

## Looking Up a Style Directly

If you need to check what a `StyleContext` has registered under a given name, without going through the parser, use `get()`:
```java
AnsiCode code = ctx.get("highlight");
```

This returns `null` if no style is registered under that name in this context. Note that this only checks the context itself, it does not fall back to the global registry or predefined styles the way the parser's resolution order does.

## A Note on the Global Registry

The global registry (`Clique.registerStyle()`, `Clique.registerStyles()`) still exists and is fine to use for styles that really are constant across your whole application, an official brand color, for instance. The tradeoff is that anything registered there is visible everywhere, and can be overwritten by anything else that registers the same name.

For anything scoped to a specific feature, component, or part of your codebase, prefer building a `StyleContext` and attaching it to just the parsers that need it.

## Things to Watch Out For

- All `add()` overloads on the builder throw `NullPointerException` if any argument is `null`.
- `StyleContext.from(StyleContext)` throws `NullPointerException` if the given context is `null`.
- `StyleContext.from(String)` throws `NullPointerException` if the theme name is `null`, and `NoSuchThemeException` if no theme with that name is registered or discoverable.
- `get()` returns `null`, not an exception, when a name isn't found.
- `StyleContextBuilder` is not thread-safe, synchronize externally if a single builder is shared across threads.

## See Also

- [Parser Documentation](parser.md) - How markup parsing and style resolution work
- [Markup Reference](markup-reference.md) - Built-in colors and styles
- [Themes](themes.md) - Pre-built palettes you can register and use, or load into a StyleContext