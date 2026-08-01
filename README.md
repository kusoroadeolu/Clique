![Supported JVM Versions](https://img.shields.io/badge/JVM-21+-brightgreen.svg?&logo=openjdk)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/kusoroadeolu/Clique/blob/main/LICENSE)
[![No Color](https://img.shields.io/badge/no--color.org-compliant-blue)](https://no-color.org)

# CLIQUE
A dependency-free and extensible CLI library for beautifying Java terminal applications.

![Clique Hero](images/clique-hero.png)

## Why Clique?
![Ansi Comparison](images/comparison.png)

---

## Highlights

- **RGB & Gradients** - Full 24-bit color support with per-character gradient rendering. [Ink docs](docs/ink.md)
- **no-color.org compliant** - Respects the `NO_COLOR` environment variable out of the box. [no-color.org](https://no-color.org)
- **GraalVM compatible** - Works with GraalVM native image, no reflection surprises.
- **Better unicode emoji support** - Emoji handling compliant with most Unicode emojis

---

## Quick Start

### Maven

```xml
<dependency>
    <groupId>io.github.kusoroadeolu</groupId>
    <artifactId>clique-core</artifactId>
    <version>4.0.3</version>
</dependency>
```

### Gradle

```gradle
dependencies {
    implementation 'io.github.kusoroadeolu:clique-core:4.0.3'
}
```

---

## Features

### Markup Parser
Simple, readable syntax for styled text:
```java
Clique.parser().print("[red, bold]Error:[/] Something went wrong");
```

### Ink
A lightweight, chainable ANSI string builder - similar in spirit to [Chalk](https://github.com/chalk/chalk). Each method returns a new `Ink` instance, so you can safely reuse base styles:
```java
Clique.ink().red().bold().on("Error");

// Reusable base style
Ink bold = Clique.ink().bold();
bold.red().on("error");    // bold + red
bold.yellow().on("warn");  // bold + yellow. The original stays untouched

// Gradients
Clique.ink().bold().gradient("#FF69B4", "#6495ED").on("Gradient text");
```
![Sample Ink](images/ink.png)

### Themes
Drop in popular color schemes with one line:
```java
Clique.registerTheme("catppuccin-mocha");
Clique.parser().print("[ctp_mauve]Styled with Catppuccin![/]");
```
**Built-in themes:** Catppuccin, Dracula, Gruvbox, Nord, Tokyo Night.
- [Clique Themes Repository](https://github.com/kusoroadeolu/clique-themes)
- [Themes docs](docs/themes.md)
- [Create your own theme](docs/build-your-own-theme.md)

### Tables
Build tables with multiple styles:
```java
Clique.table()
    .headers("Name", "Age", "Status")
    .row("Alice", "25", "Active")
    .row("Bob", "30", "Inactive")
    .render();
```
![Sample Table](images/sample-table.png)

### Boxes
Single-cell boxes with text wrapping:
```java
Clique.box()
    .dimensions(40, 10) // Width, Height
    .content("Your message here")
    .render();
```
![Sample Box](images/sample-box.png)

### Tree
Display hierarchical data with clean connector lines:
```java
Tree tree = Clique.tree("project/");

Tree src = tree.add("src/");
src.add("Main.java");
src.add("Utils.java");

tree.add("README.md");
tree.render();
```
![Sample Tree](images/sample-tree.png)

### ItemList
Symbol-driven lists with nesting and full markup support. Config cascades from parent to sublists automatically:
```java
Clique.list()
    .item("[green]✓[/]", "Auth service")
    .item("[yellow]~[/]", "Notification system - in review",
        Clique.list()
            .item("!", "Waiting on design sign-off")
    )
    .render();
```
![Sample Item List](images/list.png)

### Frames
Layout container that vertically stacks Clique components inside a border:
```java
Clique.frame()
    .title("[bold]My App[/]")
    .nest(table)
    .nest(progressBar)
    .render();
```
![Sample Frame](images/sample-frame.png)

### Progress Bars
Visual feedback for long-running operations - wrap a collection and the Clique handles the rest:
```java
for (var file : Clique.progressBar(files)) {
    process(file);
}
```

![Sample Progress Bar](images/sample-pg-bar.png)

> **Thread safety:** Style registration/lookup and config objects (once built) are thread-safe. All other components are not - avoid sharing instances across threads.

---

## Built with Clique

- [Veneer](https://github.com/kusoroadeolu/veneer): Syntax highlighting for Java, Python, Go, Lua, and JavaScript in the terminal

---

## Documentation

- **[Full Documentation](docs)**: Complete guides for all features
- **[Markup Reference](docs/markup-reference.md)**: Colors, styles, and syntax
- **[Examples & Demos](https://github.com/kusoroadeolu/clique-demos)**: Interactive examples

---

## Try the Demos

```bash
git clone https://github.com/kusoroadeolu/clique-demos.git
cd clique-demos
javac src/demo/QuizGame.java
java -cp src demo.QuizGame
```

See [clique-demos](https://github.com/kusoroadeolu/clique-demos) for all available demos.

---

## License

Apache 2.0

## Contributing

Contributions are welcome! 