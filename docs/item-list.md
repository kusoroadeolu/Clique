# ItemList

`ItemList` builds nested, symbol-driven lists with full markup support. Each item gets a symbol, content, and optionally a sublist. Levels are tracked automatically — you just compose.

> **`@Stable` since `4.0.1`.**

> **Looking for file trees?** If you need `├─` / `└─` connectors, check out [`Tree`](tree.md) instead. `ItemList` is for when *you* own the structure — task lists, build output, menus, etc.

---

## Basic Usage

```java
Clique.list()
    .item("•", "Buy groceries")
    .item("•", "Write tests")
    .item("•", "Ship it")
    .render();
```

---

## Nesting

Pass a sublist as the third argument to `item()`. The parent's config cascades down automatically — no need to configure each level separately.

```java
Clique.list()
    .item("→", "Backend",
        Clique.list()
            .item("✓", "Auth service")
            .item("✓", "User service")
            .item("✗", "[red]Payment gateway[/]")
    )
    .item("→", "Frontend",
        Clique.list()
            .item("✓", "Landing page")
            .item("✗", "[red]Dashboard[/]")
    )
    .render();
```

A few things worth knowing:
- Passing `null` as a sublist throws `NullPointerException`
- Nesting a list within itself throws `IllegalArgumentException`
- Sublists render depth-first — each sublist fully resolves before the next sibling item

---

## Configuration

```java
ItemListConfiguration config = ItemListConfiguration.builder()
    .indentSize(4)            // spaces per level, default: 2
    .symbolSpacing(2)         // spaces between symbol and content, default: 1
    .parser(Clique.parser())  // default: MarkupParser.DEFAULT
    .build();

Clique.list(config)
    .item("▸", "[bold]Section[/]",
        Clique.list()
            .item("–", "Subsection item")
    )
    .render();
```

Config set on a parent list cascades into all nested sublists — you set it once at the top.

### Options

| Option          | Default              | Constraint            |
|-----------------|----------------------|-----------------------|
| `indentSize`    | `2`                  | Cannot be less than 1 |
| `symbolSpacing` | `1`                  | Cannot be negative    |
| `parser`        | `MarkupParser.DEFAULT` | —                   |

---

## Markup Support

Markup works in symbols, content, or both:

```java
Clique.list()
    .item("[cyan]▸[/]", "[bold]Section Title[/]",
          Clique.list()
            .item("[green]✓[/]", "Thing that worked")
            .item("[yellow]~[/]", "Thing that kind of worked")
            .item("[red]✗[/]", "Thing that did not work")
    )
            .render();
```

The symbol and the content of each item are parsed separately, and so is every item in the list. A stray `[` in one only affects that string. Escape literal brackets with `\\[`.

---

## Examples

### Sprint Board

```java
Clique.list()
    .item("→", "[bold]Sprint 12[/]",
          Clique.list()
            .item("[green]✓[/]", "[dim, strike]Auth service[/]")
            .item("[green]✓[/]", "[dim, strike]User profile page[/]")
            .item("[yellow]~[/]", "Notification system - [yellow]in review[/]",
                  Clique.list()
                    .item("!", "Waiting on design sign-off")
            )
                    .item("[red]✗[/]", "[red]Payment integration[/]",
                          Clique.list()
                    .item("!", "Stripe keys not in env")
                    .item("!", "Webhook endpoint missing")
            )
                    )
                    .render();
```

### Build Report

```java
Clique.list()
    .item("[bold]>[/]", "[bold, cyan]Build Report - v2.4.1[/]",
          Clique.list()
            .item("[green]✓[/]", "Compiled 42 files in 1.3s")
            .item("[green]✓[/]", "108 tests passed")
            .item("[yellow]⚠[/]", "2 deprecation warnings",
                  Clique.list()
                    .item("–", "StringUtils.format() - use formatAndReset()")
                    .item("–", "Flag enum - superseded by markup symbols")
            )
                    .item("[red]✗[/]", "Coverage at 74% - threshold is 80%")
    )
            .render();
```

### Nested CLI Menu

```java
ItemListConfiguration config = ItemListConfiguration.builder()
        .indentSize(3)
        .build();

Clique.list(config)
    .item("▸", "[cyan, bold]Main Menu[/]",
          Clique.list()
            .item("▸", "File",
                  Clique.list()
                    .item("–", "New")
                    .item("–", "Open")
                    .item("–", "Save")
            )
                    .item("▸", "Edit",
                          Clique.list()
                    .item("–", "Cut")
                    .item("–", "Copy")
                    .item("–", "Paste")
            )
                    .item("▸", "View")
    )
            .render();
```