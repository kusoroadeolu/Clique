package io.github.kusoroadeolu.clique.configuration;

import io.github.kusoroadeolu.clique.internal.documentation.InternalApi;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;

import java.util.Objects;

/**
  * <p>Easing interpolates a tick across multiple rendered frames over a fixed duration,
 * producing a smooth animation rather than an instant jump. Whether easing is applied
 * to a given tick is determined by {@link #shouldEase(int)}, which compares the tick
 * amount against the configured {@link #getThreshold() threshold}. The default values are:
 *
 * <p>Default values:
 * <ul>
 *   <li>function: {@link EasingFunction#EASE_OUT_QUAD}</li>
 *   <li>durationMs: {@code 500}</li>
 *   <li>frames: {@code 20}</li>
 *   <li>threshold: {@code 5}</li>
 * </ul>
 *
 * <p>The per-frame delay is derived from {@code durationMs / frames} and is available
 * via {@link #getFrameDelayMs()}.
 *
 * <p>This class is immutable and thread-safe. {@link EasingConfigurationBuilder} is
 * <b>not</b> thread-safe.
 *
 * <p>Example:
 * <pre>{@code
 * EasingConfiguration easing = EasingConfiguration.builder()
 *     .function(EasingFunction.EASE_IN_OUT_CUBIC)
 *     .duration(300)
 *     .frames(15)
 *     .threshold(10)
 *     .build();
 * }</pre>
 *
 * @since 3.0.0
 */
@Stable(since = "3.2.0")
public final class EasingConfiguration {

    /**
     * A default {@code EasingConfiguration} with {@code EASE_OUT_QUAD}, 500 ms duration,
     * 20 frames, and a threshold of {@code 5}.
     */
    public static final EasingConfiguration DEFAULT = new EasingConfiguration();

    /**
     * A disabled {@code EasingConfiguration}. {@link #shouldEase(int)} always returns
     * {@code false}, causing all ticks to be applied instantly without animation.
     */
    public static final EasingConfiguration DISABLED = disabledConfig();

    private final EasingFunction function;
    private final int durationMs;
    private final int frames;
    private final int threshold;

    private EasingConfiguration() {
        this(new EasingConfigurationBuilder());
    }

    private static final int DISABLED_THRESHOLD = 0;

    private static EasingConfiguration disabledConfig() {
        EasingConfigurationBuilder b = new EasingConfigurationBuilder();
        b.function = EasingFunction.LINEAR;
        b.durationMs = 0;
        b.frames = 1;
        b.threshold = DISABLED_THRESHOLD;
        return new EasingConfiguration(b);
    }

    private EasingConfiguration(EasingConfigurationBuilder easingConfigurationBuilder) {
        this.function = easingConfigurationBuilder.function;
        this.durationMs = easingConfigurationBuilder.durationMs;
        this.frames = easingConfigurationBuilder.frames;
        this.threshold = easingConfigurationBuilder.threshold;
    }

    /**
     * Returns a new builder for constructing an {@code EasingConfiguration}.
     *
     * @return a new {@link EasingConfigurationBuilder}
     */
    public static EasingConfigurationBuilder builder() {
        return new EasingConfigurationBuilder();
    }

    /**
     * Returns the easing function applied to interpolate progress across frames.
     *
     * @return the easing function; never {@code null}
     */
    public EasingFunction getFunction() {
        return function;
    }

    /**
     * Returns the total duration of the easing animation in milliseconds.
     *
     * <p>The per-frame delay is {@code durationMs / frames}, available via
     * {@link #getFrameDelayMs()}.
     *
     * @return the animation duration in milliseconds; {@code >= 0}
     */
    public int getDurationMs() {
        return durationMs;
    }

    /**
     * Returns the number of intermediate frames rendered during the animation.
     *
     * <p>Higher values produce smoother animations at the cost of increased
     * CPU usage. The per-frame delay is {@code durationMs / frames}.
     *
     * @return the frame count; always {@code >= 1}
     */
    public int getFrames() {
        return frames;
    }

    /**
     * Returns the minimum tick amount required to trigger easing.
     *
     * <p>Ticks below this value are applied instantly. A threshold of {@code 0}
     * means easing is applied to all ticks. See {@link #shouldEase(int)}.
     *
     * @return the threshold; {@code >= 0}
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Returns the delay between each animation frame, derived as {@code durationMs / frames}.
     *
     * <p>Uses integer division, so the result is truncated. The total animation
     * time may be slightly shorter than {@link #getDurationMs()}.
     *
     * @return the per-frame delay in milliseconds
     */
    public long getFrameDelayMs() {
        return durationMs / frames;
    }

    /**
     * Returns whether easing should be applied for the given tick amount.
     *
     * <p>Returns {@code true} if {@code tickBy >= threshold}. A negative threshold
     * (used internally by {@link #DISABLED}) always yields {@code false}. Thresholds
     * set through the builder are always {@code >= 0}, so a builder-created
     * configuration never disables easing this way.
     */
    @InternalApi(since = "4.0.2")
    public boolean shouldEase(int tickBy) {
        if (threshold < 0) return false;
        return tickBy >= threshold;
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        EasingConfiguration that = (EasingConfiguration) object;
        return durationMs == that.durationMs && frames == that.frames && threshold == that.threshold && function == that.function;
    }

    public int hashCode() {
        return Objects.hash(durationMs, frames, threshold, function);
    }

    @Override
    public String toString() {
        return "EasingConfiguration[" +
                "function=" + function +
                ", durationMs=" + durationMs +
                ", frames=" + frames +
                ", threshold=" + threshold +
                ']';
    }

    /**
     * Builder for {@link EasingConfiguration}.
     *
     *
     * <p>This builder is <b>not</b> thread-safe.
     */
    public static class EasingConfigurationBuilder {

        private EasingFunction function = EasingFunction.EASE_OUT_QUAD;
        private int durationMs = 500;
        private int frames = 20;
        private int threshold = 5;

        private EasingConfigurationBuilder(){}


        /**
         * Sets the easing function used to interpolate progress across frames.
         *
         * @param function the easing function to apply; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code function} is {@code null}
         */
        public EasingConfigurationBuilder function(EasingFunction function) {
            Objects.requireNonNull(function, "Easing function cannot be null");
            this.function = function;
            return this;
        }

        /**
         * Sets the total duration of the easing animation in milliseconds.
         *
         * <p>The per-frame delay is computed as {@code durationMs / frames}.
         * A duration of {@code 0} causes all frames to render without delay.
         *
         * @param durationMs the animation duration; must be {@code >= 0}
         * @return this builder
         * @throws IllegalArgumentException if {@code durationMs} is negative
         */
        public EasingConfigurationBuilder duration(int durationMs) {
            if (durationMs < 0) throw new IllegalArgumentException("Duration must be non-negative");
            this.durationMs = durationMs;
            return this;
        }

        /**
         * Sets the number of intermediate frames rendered during the animation.
         *
         * <p>Higher values produce smoother animations at the cost of increased CPU usage.
         * The per-frame delay is computed as {@code durationMs / frames}.
         *
         * @param frames the number of frames; must be {@code >= 1}
         * @return this builder
         * @throws IllegalArgumentException if {@code frames} is {@code <= 0}
         */
        public EasingConfigurationBuilder frames(int frames) {
            if (frames <= 0) throw new IllegalArgumentException("Frames must be greater than zero");
            this.frames = frames;
            return this;
        }

        /**
         * Sets the minimum tick amount required to trigger easing.
         *
         * <p>Ticks smaller than this value are applied instantly without animation.
         * Set to {@code 0} to ease all ticks regardless of size.
         *
         * @param threshold the minimum tick amount; must be {@code >= 0}
         * @return this builder
         * @throws IllegalArgumentException if {@code threshold} is negative
         */
        public EasingConfigurationBuilder threshold(int threshold) {
            if (threshold < 0) throw new IllegalArgumentException("Threshold must be non-negative");
            this.threshold = threshold;
            return this;
        }

        /**
         * Constructs a new {@link EasingConfiguration} from the current builder state.
         *
         * @return a new, immutable {@code EasingConfiguration}
         */
        public EasingConfiguration build() {
            return new EasingConfiguration(this);
        }
    }
}