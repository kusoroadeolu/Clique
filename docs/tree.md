# Tree

Tree displays hierarchical data as a structured, readable tree with connector lines. It's ideal for file structures, dependency graphs, nested configurations, or any parent-child relationships.

## Basic Usage

### Simple Tree
```java
Tree tree = Clique.tree("project/")
    .add("src/")
    .add("README.md")
    .add("build.gradle");

tree.render();
```

### Nested Tree
```java
Tree tree = Clique.tree("project/");

Tree src = tree.add("src/");
src.add("Main.java");
src.add("Utils.java");

Tree test = tree.add("test/");
test.add("MainTest.java");

tree.add("README.md");

tree.render();
```

## Adding Nodes

### Add a Child
```java
Tree parent = Clique.tree("Parent");
Tree child = parent.add("Child");          // child is the new node
Tree grandchild = child.add("Grandchild");

Tree existing = Clique.tree("Existing");
Tree attached = parent.add(existing);      // attached is existing
```

`add()`returns the newly created child node, so you can nest further calls off it.


### Nesting Arbitrarily Deep
```java
Tree a = tree.add("level 1");
Tree b = a.add("level 2");
b.add("level 3");
```

## Using Markup in Labels

Markup tags work in any label, root or child:
```java
Tree tree = Clique.tree("[*magenta, bold]my_project/");

Tree src = tree.add("[cyan]src/");
src.add("[green]Main.java        [dim]✓ ok");
src.add("[yellow]Utils.java       [dim]⚠ needs review");
src.add("[red]Broken.java      [dim]✗ error");

tree.add("[dim].gitignore");

tree.render();
```

Each label is parsed independently. A stray `[` in one label won't affect markup in any other label. To show a literal bracket, escape it: `tree.add("arr\\[0]")`.

## Tree Configuration

Use `TreeConfiguration` to customize the guide connector style.

### Basic Configuration
```java
TreeConfiguration config = TreeConfiguration.builder()
    .connectorColor("cyan, bold")
    .build();

Tree tree = Clique.tree("project/", config);
```

### Configuration Options

#### Connector Color

Controls the color and style of the connector characters (`├─`, `└─`, `│`). Accepts any valid markup style string:

```java
TreeConfiguration config = TreeConfiguration.builder()
    .connectorColor("*blue, bold")
    .build();
```

#### Custom Parser

Provide a custom configured parser for markup processing in labels:

```java
ParserConfiguration parserConfig = ParserConfiguration
        .builder()
        .delimiter(' ')
        .build();

TreeConfiguration config = TreeConfiguration.builder()
        .parser(Clique.parser(parserConfig))
        .build();
```

### Full Configuration Example

```java
TreeConfiguration config = TreeConfiguration.builder()
        .connectorColor("*cyan, bold") //Do not add the markup tag borders i.e [*cyan, bold]
        .build();

Tree tree = Clique.tree("[*magenta, bold]clique-lib/", config);

Tree src = tree.add("[*cyan, bold]src/");
Tree core = src.add("[cyan]core/");
core.add("[green]Parser.java         [dim]✓ 312 lines");
core.add("[green]StyleResolver.java  [dim]✓ 198 lines");
core.add("[yellow]Renderer.java       [dim]⚠ needs review");

Tree tests = tree.add("[*cyan, bold]tests/");
tests.add("[green, bold]ParserTest.java     [dim]✓ 14/14 pass");
tests.add("[red, bold]RendererTest.java   [dim]✗  9/14 pass");
tests.add("[dim, strike]TreeTest.java       skipped");

tree.add("[white]README.md");
tree.add("[dim].gitignore");

tree.render();
```

## Getting the Tree as a String

Use `get()` to retrieve the rendered tree as a string without printing:

```java
String result = tree.get();
System.out.println(result);
```


## Parent
```java
Tree src = tree.add("src/");
Tree main = src.add("Main.java");

main.parent(); // returns Optional.of(src)
tree.parent();  // returns Optional.empty() — root has no parent
```

## Reparenting and Cycles
If the child already has a parent, it’s removed from that parent first. Adding a node to itself throws UnsupportedOperationException. 
Adding an ancestor is not checked, which creates a cycle, and get() will overflow the stack

## Factory Methods

```java
// Default configuration
Clique.tree("label");

// Custom configuration
Clique.tree("label", config);
```



## See Also

- [Markup Reference](markup-reference.md) - Available colors and styles for labels
- [Parser Documentation](parser.md) - How markup parsing works
- [Item List Documentation](item-list.md) - Lower level functional hierarchical building