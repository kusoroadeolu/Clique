package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.DividerConfiguration;
import io.github.kusoroadeolu.clique.configuration.TitleAlign;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.StringUtils;
import io.github.kusoroadeolu.clique.style.StyleBuilder;

import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.Constants.BLANK;

/**
 * A horizontal divider line that can optionally display a title.
 * <p>
 * Divider renders a horizontal line of configurable width. When a title is
 * set via {@link #title(String)}, the title is centered within the divider line.
 *
 * @since 4.0.2
 */
@Stable(since = "4.0.2")
public class Divider implements Component {

	private final DividerConfiguration configuration;

	private final int width;

	private String title;

	private TitleAlign titleAlign = TitleAlign.CENTER;

	/**
	 * Creates a new Divider with the given width and configuration.
	 *
	 * @param width         the width of the divider; must be greater than 0
	 * @param configuration the divider configuration to use; must not be {@code null}
	 * @throws IllegalArgumentException if {@code width} is not greater than 0
	 * @throws NullPointerException     if {@code configuration} is {@code null}
	 */
	public Divider(int width, DividerConfiguration configuration) {
		validateWidth(width);
		this.width = width;
		this.configuration = Objects.requireNonNull(configuration, "Divider configuration cannot be null");
	}

	private static void validateWidth(int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("Width must be greater than 0.");
		}
	}

	@Override
	public String get() {
		String dividerChar = String.valueOf(configuration.getDividerChar());

		if (title == null || title.isEmpty()) {
			return new StyleBuilder()
					.appendAndReset(dividerChar.repeat(width), configuration.getDividerColor())
					.toString();
		}

		int contentLength = configuration.getParser().getOriginalString(title).length() + 2; // add 2 from padding
		String content = BLANK + StringUtils.parse(title, configuration.getParser()) + BLANK;


		int remaining = width - contentLength;

		int left = switch (titleAlign) {
			case LEFT -> 1;
			case RIGHT -> remaining - 1;
			case CENTER -> remaining / 2;
		};

		int right = remaining - left;

        var dividerColor = configuration.getDividerColor();
		return new StyleBuilder()
                .appendAndReset(dividerChar.repeat(left), dividerColor)
				.appendAndReset(content)
				.appendAndReset(dividerChar.repeat(right), dividerColor)
				.toString();
	}

	/**
	 * Sets the title to display centered within the divider line.
	 * <p>
	 *
     * @param title the title to display; must not be {@code null}
     *
     * @throws NullPointerException if {@code title} is {@code null}
     * @throws IllegalArgumentException if the visible title length exceeds or is equal to the divider width
	 * @return this divider instance
	 */
	public Divider title(String title) {
       return title(title, TitleAlign.CENTER);
	}

	public Divider title(String title, TitleAlign titleAlign) {
		Objects.requireNonNull(title, "Divider title cannot be null");
		Objects.requireNonNull(titleAlign, "Title align cannot be null");

		String visible = configuration.getParser().getOriginalString(title);

		if (visible.length() + 2 >= width) {
			throw new IllegalArgumentException(
					"Title's visible length must be less than divider width."
			);
		}

		this.title = title;
		this.titleAlign = titleAlign;
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) return false;

		Divider that = (Divider) object;
		return width == that.width && Objects.equals(configuration, that.configuration) && Objects.equals(title, that.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, configuration, title);
	}

	@Override
	public String toString() {
		return "Divider[" +
			   "configuration=" + configuration +
			   ", title='" + title + '\'' +
			   ", width=" + width +
			   ']';
	}
}
