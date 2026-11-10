package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.EasingConfiguration;
import io.github.kusoroadeolu.clique.configuration.ProgressBarConfiguration;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;
import static io.github.kusoroadeolu.clique.internal.utils.Constants.ZERO;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.parse;

/**
 * A terminal progress bar component that tracks incremental progress toward a total.
 *
 * <p>Progress is advanced via {@link #tick()} or {@link #tick(int)}, each of which
 * re-renders the bar in place. The bar is considered complete when {@code currentTick >= total},
 * at which point {@link #isDone()} returns {@code true} and a newline is emitted on the
 * next {@link #render(PrintStream)} call.
 *
 * <p>Rendering uses a format string from {@link ProgressBarConfiguration} that supports
 * the following tokens:
 * <ul>
 *   <li>{@code :bar} — the filled/unfilled bar segment</li>
 *   <li>{@code :progress} — current tick count, right-aligned to total width</li>
 *   <li>{@code :total} — the total tick count</li>
 *   <li>{@code :percent} — completion percentage, right-aligned to 3 characters</li>
 *   <li>{@code :elapsed} — elapsed time in {@code mm:ss}</li>
 *   <li>{@code :remaining} — estimated remaining time in {@code mm:ss}, or {@code --:--} if unavailable</li>
 * </ul>
 *
 * <p>This class is <b>not</b> thread-safe. Concurrent calls to {@link #tick(int)} or
 * {@link #tickAnimated(int)} from multiple threads require external synchronization.
 *
 * @since 3.0.0
 */
@Stable(since = "3.0.0")
public class ProgressBar implements Component {
    final int total;
    final long creationTime;
    final ProgressBarConfiguration configuration;
    int currentTick;
    boolean isDone;

    private ProgressBar(
            int currentTick,
            int total,
            boolean isDone,
            long creationTime,
            ProgressBarConfiguration configuration
    ) {
        if (total < 0) throw new IllegalArgumentException("Progress bar total cannot be negative");
        this.currentTick = currentTick;
        this.total = total;
        this.isDone = isDone;
        this.creationTime = creationTime;
        this.configuration = configuration;
    }

    public ProgressBar(int total, ProgressBarConfiguration configuration) {
        this(ZERO, total, false, System.currentTimeMillis(), configuration);
    }

    public ProgressBar(int total) {
        this(ZERO, total, false, System.currentTimeMillis(), ProgressBarConfiguration.DEFAULT);
    }

    /**
     * Advances the progress bar by {@code 1} tick and re-renders it to {@link System#out}.
     *
     * <p>Equivalent to {@link #tick(int) tick(1)}.
     *
     * @return this instance
     */
    public ProgressBar tick() {
        return this.tick(1);
    }

    /**
     * Advances the progress bar by the given number of ticks and re-renders it to {@link System#out}.
     *
     * <p>The current tick is clamped to {@code [0, total]}. If this call causes
     * {@code currentTick >= total}, {@link #isDone()} will return {@code true} and
     * a newline is emitted on the next render.
     *
     * @param amount the number of ticks to advance; must be {@code >= 1}
     * @return this instance
     * @throws IllegalArgumentException if {@code amount} is less than {@code 1}
     */
    public ProgressBar tick(int amount) {
        return tick(amount, true);
    }

    /**
     * Advances the progress bar by {@code 1} tick.
     *
     * @param render if the progress bar should be re-rendered to {@link System#out}
     *
     * <p>Equivalent to {@link #tick(int) tick(1)}.
     *
     * @return this instance
     */
    public ProgressBar tick(boolean render) {
        return tick(1, render);
    }

    /**
     * Advances the progress bar by the given number of ticks.
     *
     * <p>The current tick is clamped to {@code [0, total]}. If this call causes
     * {@code currentTick >= total}, {@link #isDone()} will return {@code true} and
     * a newline is emitted on the next render.
     *
     * @param amount the number of ticks to advance; must be {@code >= 1}
     * @param render if the progress bar should be re-rendered to {@link System#out}
     * @return this instance
     * @throws IllegalArgumentException if {@code amount} is less than {@code 1}
     */
    public ProgressBar tick(int amount, boolean render){
        if (isDone) return this;
        if (amount < 1) throw new IllegalArgumentException("Tick amount cannot be less than 1");
        currentTick = Math.clamp(currentTick + (long) amount, ZERO, total);
        if (currentTick >= total && !isDone) isDone = true;
        if (render) render();
        return this;
    }

    /**
     * Advances the progress bar by the given amount, applying easing if configured.
     *
     * <p>If the active {@link ProgressBarConfiguration} has an {@link EasingConfiguration}
     * whose {@code shouldEase(amount)} returns {@code true}, the tick is interpolated
     * across multiple frames using the configured easing function and frame delay.
     * Otherwise, delegates directly to {@link #tick(int)}.
     *
     * <p>This method blocks the calling thread for the duration of the animation when
     * easing is applied.
     *
     * @param amount the number of ticks to advance; must be {@code >= 1}
     * @return this instance
     */
    public ProgressBar tickAnimated(int amount) {
        if (isDone) return this;
        if (amount < 1) throw new IllegalArgumentException("Tick amount cannot be less than 1");
        var config = this.configuration;
        if (config != null && config.getEasing().shouldEase(amount)) {
            this.easeTick(amount, config.getEasing());
            return this;
        } else {
            return this.tick(amount);
        }
    }

    private void easeTick(int amount, EasingConfiguration easingConfig) {
        int startValue = this.currentTick;
        int targetValue = Math.clamp(currentTick + (long) amount, ZERO, total);
        int diff = targetValue - startValue;

        int frames = easingConfig.getFrames();
        long frameDelay = easingConfig.getFrameDelayMs();

        for (int i = 1; i <= frames; i++) {
            double t = i / (double) frames;
            double eased = easingConfig.getFunction().apply(t);  // Apply easing

            this.currentTick = Math.clamp(startValue + (long) (diff * eased), ZERO, total);

            this.render();

            try {
                TimeUnit.MILLISECONDS.sleep(frameDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Ensure we end exactly at target
        this.currentTick = targetValue;
        if (currentTick >= total && !isDone) {
            isDone = true;
            this.render();
        }
    }


    /**
     * Returns whether the progress bar has reached its total.
     *
     * @return {@code true} if {@code currentTick >= total}; {@code false} otherwise
     */
    public boolean isDone() {
        return isDone;
    }


    //Modified this to first check if we can tick by an actual valid value
    /**
     * Advances the progress bar to completion and re-renders it to {@link System#out}.
     *
     * <p>Equivalent to ticking by {@code total - currentTick}. If the bar is already
     * complete, this method does not throw
     *
     * @return this instance
     */
    public ProgressBar complete() {
        return complete(true);
    }

    /**
     * Advances the progress bar to completion.
     *
     * <p>Equivalent to ticking by {@code total - currentTick}. If the bar is already
     * complete, this method does not throw
     *
     * @param render if the progress bar should be re-rendered to {@link System#out}
     *
     * @return this instance
     */
    public ProgressBar complete(boolean render) {
        return tick(Math.max(1, total - currentTick), render);
    }

    private int percent() {
        if (total > ZERO) return (int) (currentTick * 100L / total);
        else return ZERO;
    }

    private long elapsedTime() {
        return System.currentTimeMillis() - creationTime;
    }

    private String interval(Long milliseconds) {
        if (milliseconds == null) return "--:--";
        else {
            var seconds = (milliseconds / 1000) % 60;
            var minutes = milliseconds / 60000;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private String barText() {
        var length = configuration.getLength();
        var complete = configuration.getComplete();
        var incomplete = configuration.getIncomplete();

        int completedLength = total > ZERO
                ? (int) ((long) currentTick * length / total)
                : ZERO;

        return String.valueOf(complete).repeat(completedLength)
                + String.valueOf(incomplete).repeat(Math.max(length - completedLength, ZERO));
    }

    private String alignRight(String text, int size) {
        return BLANK.repeat(Math.max(ZERO, size - text.length())) + text;
    }

    private Long remainingTime() {
        if (currentTick <= ZERO || total <= ZERO) return null;
        var elapsed = elapsedTime();
        var totalTime = elapsed / (currentTick / (double) total);
        return (long) (totalTime - elapsed);
    }

    /**
     * Returns the current rendered string representation of the progress bar,
     * with all format tokens resolved.
     *
     * <p>The format string is sourced from {@link ProgressBarConfiguration#getFormatForPercent(int)}
     * based on the current completion percentage. Markup tags in the resolved string
     * are parsed if a parser is configured.
     *
     * @return the rendered progress bar string; never {@code null}
     */
    public String get() {
        var currentPercent = this.percent();
        var format = configuration.getFormatForPercent(currentPercent);
        var bar = barText();
        format = format.replace(":bar", bar);

        var progress = alignRight(
                String.valueOf(this.currentTick),
                String.valueOf(this.total).length()
        );

        format = format.replace(":progress", progress);

        String totalUnits = String.valueOf( this.total / configuration.getTicksPerUnit());
        format = format.replace(":total-units", totalUnits);

        var t = String.valueOf(this.total);
        format = format.replace(":total", t);

        var percent = alignRight(String.valueOf(this.percent()), 3);
        format = format.replace(":percent", percent);

        String tickUnit = String.valueOf(currentTick / configuration.getTicksPerUnit());
        format = format.replace(":units", tickUnit);

        var elapsed = interval(this.elapsedTime());
        format = format.replace(":elapsed", elapsed);

        var remaining = interval(this.remainingTime());
        format = format.replace(":remaining", remaining);

        return parse(format, configuration.getParser());
    }

    /**
     * Renders the progress bar to the given {@link PrintStream} in place, using a
     * carriage return ({@code \r}) to overwrite the current line.
     *
     * <p>When {@link #isDone()} is {@code true}, a newline is appended after the final
     * render to advance the cursor past the completed bar.
     *
     * @param printStream the stream to render to; must not be {@code null}
     */
    @Override
    public void render(PrintStream printStream) {
        printStream.print("\r" + get());
        if (isDone) printStream.println();
        printStream.flush();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ProgressBar bar = (ProgressBar) o;
        return total == bar.total && creationTime == bar.creationTime && currentTick == bar.currentTick && isDone == bar.isDone && Objects.equals(configuration, bar.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuration, creationTime, total, currentTick, isDone);
    }

    @Override
    public String toString() {
        return "ProgressBar[" +
                "total=" + total +
                ", creationTime=" + creationTime +
                ", configuration=" + configuration +
                ", currentTick=" + currentTick +
                ", isDone=" + isDone +
                ']';
    }
}