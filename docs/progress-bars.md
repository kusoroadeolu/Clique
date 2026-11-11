# Progress Bars

Progress bars provide visual feedback for long-running operations. Clique includes predefined styles and extensive customization options.

## Quick Start

### Basic Progress Bar
```java
ProgressBar bar = Clique.progressBar(100);

while (!bar.isDone()) {
    bar.tick();
    Thread.sleep(50);
}
```

### Using Predefined Presets

Clique provides several built-in presets:
```java
// Blocks style (default)
ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.BLOCKS);

// Lines style
ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.LINES);

// Bold style
ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.BOLD);

// Classic style
ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.CLASSIC);

// Dots style
ProgressBar bar = Clique.progressBar(100, ProgressBarPreset.DOTS);
```

### Starting from a Preset
`fromPreset()` lets you use a preset as a starting point and customize from there. Useful when a preset is close to what you want but needs a tweak — different format, different length, extra styling:
```java
ProgressBarConfiguration config = ProgressBarConfiguration
.fromPreset(ProgressBarPreset.BLOCKS)
.format("[green]:bar[/] :percent% | :progress/:total")
.length(60)
.build();

ProgressBar bar = Clique.progressBar(100, config);
```

All preset values (characters, length, format) are copied into the builder, and anything you chain after overrides them.

### Iterating Over a Collection

When processing a collection, use `progressBar(collection)` to skip the boilerplate entirely. The total is inferred from the collection size, and the bar ticks and renders automatically on each iteration:
```java
for (var file : Clique.progressBar(files)) {
    process(file);
}
```

Presets and custom configuration are supported too:
```java
// With a preset
for (var file : Clique.progressBar(files, ProgressBarPreset.DOTS)) {
    process(file);
}

// With custom configuration
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
    .format("[blue]:bar[/] :progress/:total files [:elapsed/:remaining]")
    .build();

for (var file : Clique.progressBar(files, config)) {
    process(file);
}
```

> **Note:** `IterableProgressBar` is single-use. Iterating over the same instance twice will throw an `IllegalStateException`.

## Predefined Styles

### BLOCKS (Default)
```
████████████████████████████████░░░░░░░░  80% [00:12/00:03]
``` 
- Complete: `█`
- Incomplete: `░`
- Length: 40
- Format: `:bar :percent% [:elapsed/:remaining]`

### LINES
```
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▁▁▁▁▁▁▁▁▁▁ 80%
```
- Complete: `▂`
- Incomplete: `▁`
- Length: 50
- Format: `:bar :percent%`

### BOLD
```
▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▱▱▱▱▱▱▱▱ 80% | 80/100
```
- Complete: `▰`
- Incomplete: `▱`
- Length: 40
- Format: `:bar :percent% | :progress/:total`

### CLASSIC
```
[##############################====================] 60% [00:45]
```
- Complete: `#`
- Incomplete: `=`
- Length: 50
- Format: `[:bar] :percent% [:elapsed]`

### DOTS
```
●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●○○○○○○○○○○○○○○○○○○○○○ 58%
```
- Complete: `●`
- Incomplete: `○`
- Length: 50
- Format: `:bar :percent%`

## Custom Configuration

Build your own progress bar style using `ProgressBarConfiguration`:
```java
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
    .length(60)
    .complete('▓')
    .incomplete('░')
    .format("[cyan]:bar[/] :percent% | :progress/:total")
    .build();

ProgressBar bar = Clique.progressBar(100, config);
```

### Format String
Template for the progress bar output with placeholders:
```java
.format(":bar :percent% [:elapsed/:remaining]")
```

**Available placeholders:**
- `:bar` - The actual progress bar visualization
- `:percent` - Current percentage (0-100)
- `:progress` - Current tick count
- `:total` - Total tick count
- `:elapsed` - Elapsed time (MM:SS)
- `:remaining` - Estimated remaining time (MM:SS)
- `:units` - Current number of completed units (derived from tick count ÷ ticksPerUnit)
- `:total-units` - Total number of units (derived from total ÷ ticksPerUnit)

> **Literal brackets:** The format string is parsed as markup after tokens like `:bar` and `:elapsed` are substituted, so `[:bar]` becomes a tag candidate such as `[####====]`. With strict parsing enabled that throws. Escape literal brackets: `"\\[:bar] :percent%"`. The built-in `BLOCKS` and `CLASSIC` presets do this already.

#### Custom Parser
Use a custom parser for markup processing:
```java
.parser(Clique.parser())
```

## Dynamic Styling

Change the format based on progress percentage:
```java
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
        .styleRange(0, 30, "[red]:bar[/] :percent% [red]Starting...[/]")
        .styleRange(30, 70, "[yellow]:bar[/] :percent% [yellow]In Progress...[/]")
        .styleRange(70, 100, "[green]:bar[/] :percent% [green]Almost Done![/]")
        .build();

ProgressBar bar = Clique.progressBar(100, config);
```

### Custom Conditions

Use `styleWhen()` for custom conditions:
```java
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
        .styleWhen(p -> p <= 50, "[red]:bar[/] :percent%")
        .styleWhen(p -> p > 50 && p < 90, "[yellow]:bar[/] :percent%")
        .styleWhen(p -> p >= 90, "[green]:bar[/] :percent%")
        .build();
```

## Quick Examples

### Downloading a File
```java
ProgressBar bar = Clique.progressBar(totalMB, config);
int lastDownloaded = 0;

while (downloading) {
    int downloaded = getDownloadedMB();
    int delta = downloaded - lastDownloaded;
    if (delta > 0) {
        bar.tick(delta);
        lastDownloaded = downloaded;
    }
    Thread.sleep(100);
}
```

### Processing a File List
```java
for (var file : Clique.progressBar(files)) {
    process(file);
}
```

Simple as that — the bar ticks and renders automatically on each iteration.

### Processing with Custom Styling
```java
ProgressBarConfiguration config = ProgressBarConfiguration
        .fromPreset(ProgressBarPreset.DOTS)
        .styleRange(0, 50, "[red]:bar[/] :percent% [dim]Processing...[/]")
        .styleRange(50, 90, "[yellow]:bar[/] :percent% [dim]Finalizing...[/]")
        .styleRange(90, 100, "[green]:bar[/] :percent% [bold]Complete![/]")
        .build();

for (var file : Clique.progressBar(files, config)) {
    process(file);
}
```
### Processing Files with Steps
```java
// 10 files, 5 steps each = 50 ticks, displayed as units
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
        .ticksPerUnit(5)
        .format(":bar :percent% | :units/:total-units files [:elapsed/:remaining]")
        .build();

ProgressBar bar = Clique.progressBar(50, config);

for (File file : files) {
    for (int step = 0; step < 5; step++) {
        process(file, step);
        bar.tick();
    }
}
```


## See Also

- [Progress Bar Easing](progress-bars-easing.md) - Smooth animations for progress updates
- [Markup Reference](markup-reference.md) - Styling options for progress bars