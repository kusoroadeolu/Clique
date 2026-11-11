# Progress Bar Easing

Progress bars usually jump. A batch finishes, the bar lurches forward 30%, done. That works, but if you want the bar to actually *feel* like something is happening, easing gives you smooth, animated transitions between values instead.

It's opt-in, lightweight, and takes about ten lines to set up.

---

## Constants

`EasingConfiguration.DEFAULT` gives you a ready-to-use animation with sensible values — `EASE_OUT_QUAD`, 500ms duration, 20 frames, and a threshold of 5. Good for most cases without any configuration:

```java
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
    .easing(EasingConfiguration.DEFAULT)
    .build();

ProgressBar bar = Clique.progressBar(100, config);
```

`EasingConfiguration.DISABLED` is an explicit no-op. Pass it anywhere an `EasingConfiguration` is accepted to opt out of animation entirely:

```java
ProgressBarConfiguration config = ProgressBarConfiguration.builder()
    .easing(EasingConfiguration.DISABLED)
    .build();
```

---

## How it works

Easing is configured separately from the progress bar itself, then attached via `ProgressBarConfiguration`. Once wired up, you use `tickAnimated()` instead of `tick()` for the updates you want animated.

```java
EasingConfiguration easing = EasingConfiguration.builder()
    .function(EasingFunction.EASE_OUT_CUBIC)
    .duration(500)
    .frames(30)
    .threshold(10)
    .build();

ProgressBarConfiguration config = ProgressBarConfiguration.builder()
    .easing(easing)
    .build();

ProgressBar bar = Clique.progressBar(100, config);

bar.tickAnimated(40);  // smooth 500ms animation
bar.tickAnimated(3);   // below threshold — jumps instantly
```

---

## Configuring easing

### function

The curve that controls how the animation accelerates and decelerates. See the [full list below](#easing-functions).

```java
.function(EasingFunction.EASE_OUT_CUBIC)
```

### duration

Total animation time in milliseconds. Must be non-negative.

```java
.duration(500)  // half a second
```

### frames

How many steps the animation is broken into. Frame delay is calculated automatically as `duration / frames` — integer division, so round numbers play nicer together.

```java
.frames(30)  // ~16ms per frame at 500ms duration
```

More frames means smoother motion but more CPU cycles. For CLI use, 20–30 frames is usually plenty.

### threshold

The minimum tick amount needed to trigger animation. Ticks below this jump instantly. This is what makes it practical to mix `tickAnimated()` with tight loops — small increments skip the animation overhead entirely.

```java
.threshold(10)  // animate ticks >= 10, instant otherwise
```

If you need to explicitly disable easing, use `EasingConfiguration.DISABLED` instead of building a configuration manually.

---

## Easing functions

All functions are based on [easings.net](https://easings.net). The naming follows a simple pattern: `EASE_IN` accelerates into the animation, `EASE_OUT` decelerates out of it, `EASE_IN_OUT` does both.

| Function | Feel |
|----------|------|
| `LINEAR` | Constant speed, no curve |
| `EASE_IN_SINE` | Gentle acceleration |
| `EASE_OUT_SINE` | Gentle deceleration |
| `EASE_IN_OUT_SINE` | Soft on both ends |
| `EASE_IN_QUAD` | Moderate acceleration |
| `EASE_OUT_QUAD` | Moderate deceleration |
| `EASE_IN_OUT_QUAD` | Balanced curve |
| `EASE_IN_CUBIC` | Strong acceleration |
| `EASE_OUT_CUBIC` | Strong deceleration |
| `EASE_IN_OUT_CUBIC` | Dramatic on both ends |

For progress bars, `EASE_OUT_QUAD` and `EASE_OUT_CUBIC` tend to feel the most natural — they move fast at first and settle into place, which matches how progress intuitively feels.

---

## Mixing animated and instant ticks

This is where easing actually shines in practice. You don’t have to pick one or the other: animate the big jumps, and process the small ones in a batch. 
Note that tick() renders on every call, so for a loop of many small updates, pass false to skip the render and call render() once at the end.

```java
ProgressBar bar = Clique.progressBar(1000, config);

// Large batch arrives, animate it
bar.tickAnimated(150);

// Process items individually. tick(false) skips rendering, so call render() once after the loop
for (int i = 0; i < 50; i++) {
    processItem();
    bar.tick(false);
}

bar.render();

// Another large batch
bar.tickAnimated(200);
```

---

## A few things to know

**`tickAnimated()` blocks.** The animation runs on the calling thread and sleeps between frames. For CLI tools where the bar is the main event, this is fine. If that's a problem, you'll need to run it on a separate thread yourself.

**Frames snap to target.** After the animation loop finishes, the bar is set exactly to the target value. The easing math is floating point, so without the snap you'd accumulate small rounding errors over many ticks.

---

## Tuning for performance

```java
// Smooth, higher CPU
.duration(1000).frames(60)

// Good general balance
.duration(500).frames(30)

// Snappy, minimal overhead
.duration(300).frames(15)
```

If updates are happening dozens of times per second, skip easing entirely and use `tick()`. Easing is for the moments that deserve a bit of ceremony — large batch completions, stage transitions, the final stretch to 100%.

---

## See Also

- [Progress Bars](progress-bars.md) — the full progress bar API
- [Markup Reference](markup-reference.md) — styling your progress bar output