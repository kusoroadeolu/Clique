# Changelog

## Clique [3.1.0] - 2026-03-21

### Added
- `Frame` component for layout composition — bordered container that vertically stacks nested Clique components. Supports `BoxType` border styles, titled borders, per-node alignment, and markup in string nodes and titles
- `Tree` component for hierarchical data with `├─, └─, │` connectors, arbitrary nesting, markup in labels, and guide styling via `TreeConfiguration`
- RGB ANSI code support via new interface
- Emoji support in `Box`, `Table`, and `Frame`
- `Box` now allows for fluent customization chaining
- `Clique#rgb(int, int, int)` and `Clique#rgb(int, int, int, boolean)` factory methods for creating RGB ANSI codes directly — returns `RGBAnsiCode` compatible with `Clique#registerStyle()`


### Fixed
- `AnsiStringParser#getOriginalString` now correctly strips ANSI codes, not just Clique parser tags
- `Table#removeColumn(row, col)` does not throw an exception if col idx = 0

### Changed
- `noDimensions()` now throws `IllegalStateException` if `autoSize` is not enabled in `BoxConfiguration`
- `dimensions()` now throws `IllegalArgumentException` for zero or negative values
  For both **Box** and **CustomizableBox**
- All box types (`DEFAULT`, `CLASSIC`, `ROUNDED`, `DOUBLE_LINE`) consolidated into a single `DefaultBox` implementation driven by `BorderChars`, eliminates the previous per-type subclass hierarchy
- All box types now support border customization — previously restricted to `DEFAULT` only

- `BorderStyle` string overloads added for `horizontalBorderStyles()`, `verticalBorderStyles()`, and `edgeBorderStyles()` — accepts markup style strings using the default delimiter, without the markup borders i.e. `[]`
- `BorderStyle.BorderStyleBuilder` now has `uniformStyle(AnsiCode...)` and `uniformStyle(String)` for applying a single style across all border axes
- Default `BoxType` changed from `BoxType.DEFAULT` to `BoxType.ROUNDED`, more visually appealing out of the box(for both frame and box)
- Default `TableType` changed from `TableType.DEFAULT` to `TableType.BOX_DRAW`, same reason


### Deprecated
- `addHeaders()` in favor of `Table#headers()` for cleaner and more concise chaining
- `addRows()` in favor of `Table#row()` for cleaner and more concise chaining
- `Clique#customizableBox()` overloads in favor of using `BorderStyle#horizontalChar()`, `BorderStyle#verticalChar()`, `BorderStyle#cornerChar()` config methods
- `Clique#customizableTable()` overloads in favor of using `BorderStyle#horizontalChar()`, `BorderStyle#verticalChar()`, `BorderStyle#cornerChar()` config methods
- `BoxConfiguration#centerPadding` due to unclear and incorrect semantics
- `BorderStyle#getHorizontalBorderStyles()` in favor of `getHorizontalStyle()`
- `BorderStyle#getEdgeBorderStyles()` in favor of `getCornerStyle()`
- `BorderStyle#getVerticalBorderStyles()` in favor of `getVerticalStyle()`
- `BorderStyleBuilder#horizontalBorderStyles()` in favor of `horizontalStyle()`
- `BorderStyleBuilder#edgeBorderStyles()` in favor of `cornerStyle()`
- `BorderStyleBuilder#verticalBorderStyles()` in favor of `verticalStyle()`

## clique-spi [1.0.2] - 2026-03-21
Changes to the SPI module in this release.
### Changed
- Introduction of `RGBAnsiCode` interface

---

# Changelog

## Clique [3.1.1] - 2026-03-24

### Added
- `BorderStyle` overloads for `Clique#frame()`, `Clique#box()`, and `Clique#table()`
- `EasingConfiguration` overload for `Clique#progressBar()`
- `ProgressBarConfiguration#fromPreset()` — returns a configuration builder for further customization of progress bar presets

### Changed
- `EasingConfiguration.DEFAULT` now uses sensible animation defaults (`EASE_OUT_QUAD`, 500ms, 20 frames, threshold 5)
- Added `EasingConfiguration.DISABLED` as an explicit no-op constant
- `AnsiDetector#autoDetect()` now checks `CLICOLOR_FORCE` and `COLORTERM` environment variables
- `NO_COLOR` detection now correctly requires a non-empty value per spec
- Added `WT_SESSION` check for Windows Terminal ANSI support
- `AnsiDetector#ansiEnabled()` now returns a cached value instead of re-running detection on every call
- `AnsiDetector#enableCliqueColors()` and `#disableCliqueColors()` now update the cache directly


### Fixed
- Fixed an issue with `Indenter` where, if an empty(not blank) flag was passed as the current flag, it'd take precedence over the default configuration flag

## clique-spi [1.0.3] - 2026-03-24
_(no changes noted)_
---
...

# Changelog

## Clique [3.1.2] - 2026-03-26
**NOTE:** Most of these changes are internal changes and don't affect the public facing API

### Changed
- `Frame` padding is now applied symmetrically on both sides for all alignment values, matching `Box` padding behaviour
- `BorderStyle#styleBuilder()` now returns a fresh `StyleBuilder` instance per call instead of a cached shared instance, eliminates mutable shared state on an otherwise immutable config object

### Removed
- Word wrap pipeline from `AbstractBox` -> `wrapWord()`, `adjustBox()` removed from `AbstractBox`; `wrapLongString()`, `getActiveAnsiCodes()`, `getStyledEndIndex()` removed from `StringUtils`; `splitAndPreserveAnsi()` removed from `BoxUtils`

### Added
- `resolveLines()` -> splits box content on `\n`, returns `List<Cell>`
- `resolveDimensions()` -> validates explicit dimensions or computes them for `autoSize`
- `Box#content(String, TextAlign)` and `Box#content(Object, TextAlign)` -> convenience overloads for setting content and alignment inline

## clique-spi [1.0.4] - 2026-03-24
- Updated `author()` and `url()` metadata in CliqueTheme interface to be optional in the interface (i.e it is no longer mandatory for those metadata to be filled). Instead they default to an EMPTY string


# Changelog

## Clique [3.2.0] - 2026-03-30

### Added
- `BorderSpec` interface — common abstraction over border styling types
- `BorderColor` class implementing `BorderSpec` — applies a uniform color across all border axes
- Helper method in `BorderStyle` to convert a `BorderSpec` to a full `BorderStyle`
- `@InternalApi` annotation — marks types, constructors, and methods internal to Clique not intended for API consumers
- `@Stable` annotation — marks public APIs considered frozen and safe to depend on across minor releases
- `@Unstable` annotation — marks public APIs that are available but may change shape between minor versions
- `@Experimental` annotation — stronger signal than `@Unstable`; marks APIs that exist for early feedback and may be removed entirely
- `MarkupPreProcessor` — preprocessing step in the parse pipeline handling backslash escape sequences before tokenization

### Changed
- `Box` auto-sizing is now implicit when no dimensions are provided, matching `Frame`'s behaviour
- `BoxConfigurationBuilder#padding()` now returns the builder reference for fluent chaining
- `BoxConfiguration` `equals()`, `hashCode()`, and `toString()` now include `padding`
- `BorderStyle` now holds a `BorderColor` internally instead of raw `AnsiCode` arrays
- `Clique` facade overloads now accept `BorderSpec` instead of a `BorderStyle`
- `builder()` is now the standard entry point for all configuration classes
- Parser escape syntax replaced — `[content[/]]` is removed in favor of `\[` (e.g. `\[red]` renders as `[red]`)
- `enableAutoReset` now correctly described as style leak prevention — resets styles when a new tag is encountered rather than forgiving malformed tags
- `enableStrictParsing` no longer throws on unrecognized or structurally unusual brackets — only throws `UnidentifiedStyleException` when a valid tag contains an unrecognized style

### Fixed
- Strict parsing throwing `ParseProblemException` aggressively on plain brackets and the old escape syntax even when content was not a valid markup tag

### Deprecated
- `BoxConfiguration#autoSize()` — auto-sizing is now the default; configure dimensions directly on the builder
- `Box#noDimensions()` in favor of `Box#autoSize()` - no longer throws, delegates to `autoSize()` internally
- `immutableBuilder()` across all configuration classes in favor of `builder()` - marked for removal in a future major version

## clique-spi [1.0.5] - 2026-03-30
_(no changes noted)_

# Changelog

## Clique [3.2.1] - 2026-04-02

### Added
- **`IterableProgressBar<T>`** — wraps a `Collection<T>` and implements `Iterable<T>`, ticking and rendering automatically on each iteration. Single-use; throws `IllegalStateException` if iterated more than once
- New `Clique#progressBar(Collection<T>)` factory overloads:
    - `progressBar(Collection<T>)` — default configuration
    - `progressBar(Collection<T>, ProgressBarConfiguration)` — custom configuration
    - `progressBar(Collection<T>, ProgressBarPreset)` — predefined preset

### Changed
- **`ProgressBar#tick()`** now calls `render` on each tick
- **`ProgressBarConfiguration#styleRange(min, max)`** — `max` is now inclusive
- **`StyleResolver`** previously `StyleApplicator` now uses `StyleBuilder` directly for result accumulation, removing the redundant parallel `StringBuilder`
- **`DefaultStyleBuilder#style()`** now accepts a `StringBuilder` parameter, allowing `stack()` to write in-place and `format()` to use an isolated builder — eliminates unnecessary object allocation and intermediate `toString()` calls
- **`Tree`** — `buildTree` now uses `StyleBuilder` instead of a raw `StringBuilder`, with guide style resolved eagerly to `AnsiCode[]` at construction time via `ParserUtils.getAnsiCodes()` rather than inlining markup tags into the output string
- **`TreeConfigurationBuilder#guideStyle()`** no longer wraps the value in `[%s]` markup — raw style strings are now passed through as-is, with ANSI resolution handled downstream by `Tree`


### Fixed
- **`MarkupPreProcessor`** — pre-parsed ANSI escape sequences in concatenated strings no longer interfere with markup tag detection; ANSI sequences are now tracked and sentinel during pre-processing to prevent `[` in escape codes from corrupting bracket depth tracking in the tokenizer
- **`AnsiStringParserImpl#getOriginalString()`** — escaped brackets (`\[`) no longer cause width miscalculations; `postProcess` is now called on the pre-processed string instead of the original input, ensuring `\[` is correctly collapsed to a single character before width is measured
- **`ProgressBar#complete()`** no longer throws when called on an already-completed bar
- Passing a null parser into `ProgressBarConfiguration` no longer causes a `NullPointerException` during style resolution

## clique-spi [1.0.6] - 2026-04-02
_(no changes noted)_


# Changelog

## Clique [3.2.2] - 2026-04-03


### Removed
- **`MarkupPreProcessor`** — class deleted entirely; previously handled escape sequences and ANSI codes via a two-pass state machine using Unicode sentinel/placeholder characters (`\uFFFF`, `\uE000`) to mark positions for later cleanup

### Added
- **`MarkupPostProcessor`** — trimmed to a single static utility method; now only handles one concern: replacing escaped brackets (`\[`) with literal `[` in the final output. ANSI sentinel cleanup removed along with the pre-processor

### Changed
- **`Tokenizer`** — all methods are now static; class is no longer meant to be instantiated (private constructor added). Escape sequence handling (`\[`) is now done inline — when a `[` is encountered, the preceding character is checked, and if it's a backslash, the bracket is skipped. A `charNotEquals` helper was added to support this
- **`StyleResolver`** — all methods made static, private constructor added; no behavioral changes

### Fixed
- **`Frame`** - Each line in each node in `Frame` now takes into account its line width, to fill it's remaining space across all 3 `FrameAlign` options, preventing broken lines 
- **`Box`** - Does not throw an NPE, during `resolveLines` if no content is set 

## clique-spi [1.0.7] - 2026-04-03
_(no changes noted)_


## Clique [4.0.0] - 2026-04-10
### Added
- `AnsiCode` varargs and `String` overloads in `Clique` facade in place of `BorderSpec` types. These overloads provide uniform styling across each component's borders, removing the use of per edge control.
- `connectorColor()` method to `TreeConfiguration` with `AnsiCode...` and `String` overloads
- `StyleContext` support in `ParserConfiguration` via `styleContext(StyleContext)` and `addStyle(String, AnsiCode)` builder methods, allowing custom styles scoped to a specific parser instance
- `Ink` — a lightweight, functional, chainable ANSI string builder accessible via `Clique.ink()` and `Clique.ink(StyleContext)`. Supports all predefined colors, bright variants, background colors, text styles, 24-bit RGB via `rgb()` and `bgRgb()`, and named style lookup via `of()`.
- `ItemList` — a new declarative, composable list component accessible via `Clique.list()` and `Clique.list(ItemListConfiguration)`. Replaces `Indenter` with a structured `item(symbol, content)` API where nesting is expressed through composition rather than manual level tracking.
- `ItemListConfiguration` — configuration for `ItemList`, supporting `indentSize`, `symbolSpacing`, and `parser`. Config cascades automatically into nested sublists.
- `Clique#registerTheme(CliqueTheme)` as a separate overload for more programmatic control of the string based theme registeration
- `Clique#disableCliqueColors()`

### Fixed
- An off by one error in `Frame` when a title wider than the frame's content was aligned left or right.

### Removed
- Deprecated customizable variants of `Table` and `Box` and the `Customizable` interface
- `AbstractBox`, instead folding all pre-existing logic into `DefaultBox`
- `BoxDimensionBuilder`, taking along `autosize()` and `withDimensions()` build steps. Boxes now autosize if `dimensions()` is not called
- Deprecated `autosize()`, `centerPadding()`, `getAutosize()` and `getCenterPadding()` methods from `BoxConfiguration`
- `BorderStyle` and `BorderSpec` types entirely, along with their usages across `BoxConfiguration`, `FrameConfiguration`, and `TableConfiguration`
- `fromBorderStyle(BorderSpec)` factory methods from `BoxConfiguration`, `FrameConfiguration`, and `TableConfiguration`
- `getBorderStyle()` getters and `borderStyle(BorderSpec)` builder methods from all three configuration classes
- Deprecated `immutableBuilder()` methods in configuration classes
- `flush()` methods from `Tree` and `StyleBuilder`
- `Indenter` and `IndenterConfiguration` — fully removed in favour of `ItemList` and `ItemListConfiguration`
- `Clique.indenter()` factory method — replaced by `Clique.list()` and `Clique.list(ItemListConfiguration)`
- `Clique#enableCliqueColors(boolean)`

### Updated
- `Box` and `BoxConfiguration` `equals()` and `hashcode()` contracts
- `BoxConfiguration`, `FrameConfiguration`, and `TableConfiguration` now accept border color via `AnsiCode[]` instead of `BorderStyle`, exposed through a `getBorderColor()` getter and `borderColor(AnsiCode...)` / `borderColor(String)` builder methods
- `Clique#table` overloads to return `PendingTable` interface, in place of `TableDimensionBuilder`
- `Tree#parent` returns an `Optional<Tree>` type instead of a `Tree` type.
- `AnsiStringParser` to `MarkupParser`
- `enableAutoCloseTags()` renamed to `enableAutoReset()` in `ParserConfiguration` to better reflect its behavior of resetting ANSI codes after each styled segment
- `Tree#print` to `Tree#render()`
- `Clique#discoverThemes()` to `Clique#findAvailableThemes()`
- `Clique#registerAllThemes` to `Clique#registerAvailableThemes()`

## clique-spi [2.0.0] - 2026-04-10
- `AnsiCode#toString()` contract renamed to `AnsiCode#ansiSequence()`

## clique-themes [1.0.1] - 2026-04-10
- Registered theme ansi codes now align with the updated interface contract. No breaking changes

## Clique [4.0.1] - 2026-04-12
### Added
- `Ink#hyperlink(String url)` — wraps rendered text in OSC 8 escape sequences for clickable links in supported terminals
- `Ink#gradient(RGBAnsiCode from, RGBAnsiCode to)` — applies a linear RGB gradient across rendered text, interpolating per visible character while preserving existing ANSI sequences
- Coverage for **colored circles and squares** (`0x1F7E0..0x1F7F0`, E12.0–E14.0): 🟠🟡🟢🟣🟤⬛⬜🟥🟧🟨🟩🟦🟫🟰
- Missing emoji-presentation BMP ranges 

## clique-spi [2.0.1] - 2026-04-12
_(no changes noted)_


## Clique [4.0.2] - 2026-04-29
### Added
- `ProgressBar#tick(boolean render)` overloads to all tick methods to control if a tick should print to `System.out`
- `ProgressBarConfiguration#ticksPerUnit(int)` with `:units` and `:total-units` format tokens, derived automatically from tick count
- `IterableProgressBar#printStream(PrintStream stream)` method for more control on iterable progress bar `PrintStream`
- Added the new `Divider` component — a horizontal divider line with optional centered title and markup support
- `DividerConfiguration` for customizing divider character, color, and parser
- `Ink#hex(String hexCode)` for applying a 24-bit RGB foreground color via a `#RRGGBB` hex string
- `Ink#bgHex(String hexCode)` for applying a 24-bit RGB background color via a `#RRGGBB` hex string
- `Clique#hex(String hexCode)` and `Clique#hex(String hexCode, boolean background)` facade methods for hex color creation
- `Clique#compose(AnsiCode...)` and `Clique#compose(Collection<AnsiCode>)` for combining multiple ANSI codes into one
- `ItemList#item(String content)` overload that reuses the last set symbol

## Updated
- Updated `CharWidth` utility to better support Unicode 17.0, specifically focusing on accurate column measurement for complex emojis.

## Fixed
- Fixed ZWJ Sequence Over-counting. Resolved an issue where multi-person ZWJ sequences (e.g., families or modern 17.0 clusters) were incorrectly measured as multiple characters.
- Fixed ItemList to recursively set configuration now propagates to all descendants, not just immediate children
- Fixed `Table#remove` to now be index-based to avoid removing wrong cell when duplicates exist



## clique-spi [2.0.2] - 2026-04-29
_(no changes noted)_


## Clique [4.0.3] - 2026-07-31
### Added
- `PendingTable#fromColumns(SequencedMap<String, ? extends SequencedCollection<String>> columns)` to build the table in one call instead of chaining
- `PendingTable#fromRows(SequencedCollection<? extends SequencedCollection<String>> rows)` to build the table in one call instead of chaining
- `StyleContext#fromTheme(String theme)` to create a style context from a theme

### Deprecated
- `PendingTable#headers(Collection<String> headers)` in favor of `PendingTable#headers(SequencedCollection<String> headers)`
- `Table#row(Collection<String> row)` in favor of `Table#row(SequencedCollection<String> row)`

## clique-spi [2.0.3] - 2026-07-31
_(no changes noted)_