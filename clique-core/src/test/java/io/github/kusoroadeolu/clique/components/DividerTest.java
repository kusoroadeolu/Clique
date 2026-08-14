package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.DividerConfiguration;
import io.github.kusoroadeolu.clique.configuration.TitleAlign;
import io.github.kusoroadeolu.clique.internal.exception.UnidentifiedStyleException;
import io.github.kusoroadeolu.clique.internal.utils.AnsiDetector;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.clique.style.ColorCode;
import io.github.kusoroadeolu.clique.style.StyleCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DividerTest {

	@BeforeEach
	void setup() {
		AnsiDetector.enableCliqueColors();
	}

	@Test
	void dividerWidth() {
		var divider = Clique.divider(100);
		var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
		assertEquals(100, output.length());
	}

	@Test
	void zeroWidth() {
		assertThrows(IllegalArgumentException.class, () -> Clique.divider(0));
	}

	@Test
	void negativeWidth() {
		assertThrows(IllegalArgumentException.class, () -> Clique.divider(-1));
	}

	@Test
	void nullConfiguration() {
		assertThrows(NullPointerException.class, () -> new Divider(10, null));
	}

	@Test
	void coloredByString() {
		var divider = Clique.divider(100, "red");
		var output = divider.get();
		assertEquals(109, output.length());
		assertTrue(output.startsWith(ColorCode.RED.ansiSequence()));
		assertTrue(output.endsWith(StyleCode.RESET.ansiSequence()));
	}

	@Test
	void unparsableColorCode() {
		assertThrows(UnidentifiedStyleException.class, () -> Clique.divider(100, "rad"));
	}

	@Test
	void coloredByAnsiCode() {
		var divider = Clique.divider(100, ColorCode.RED);
		var output = divider.get();
		assertEquals(109, output.length());
		assertTrue(output.startsWith(ColorCode.RED.ansiSequence()));
		assertTrue(output.endsWith(StyleCode.RESET.ansiSequence()));
	}

	@Test
	void centeredTitle() {
		var divider = Clique.divider(20).title("Clique rocks");
		var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
		assertEquals(20, output.length());
		assertEquals("─── Clique rocks ───", output);
	}

	@Test
	void titleLongerThanWidthAssertThrows() {
		var divider = Clique.divider(10);
        assertThrows(IllegalArgumentException.class, () -> divider.title("Clique rocks"));
	}

	@Test
	void titleWithMarkup() {
		var divider = Clique.divider(100).title("[red]Clique rocks[/]");
		var output = divider.get();
		assertEquals(100, MarkupParser.DEFAULT.getOriginalString(output).length());
		assertTrue(output.contains(ColorCode.RED + "Clique rocks" + StyleCode.RESET));
	}

	@Test
	void titleWithMultipleMarkups() {
		var divider = Clique.divider(100).title("[red][bold]Clique rocks[/][/]");
		var output = divider.get();
		assertEquals(100, MarkupParser.DEFAULT.getOriginalString(output).length());
		assertTrue(output.contains(ColorCode.RED.toString() + StyleCode.BOLD + "Clique rocks" + StyleCode.RESET + StyleCode.RESET));
	}

	@Nested
	class CustomConfiguration {
		@Test
		void dividerChar() {
			var config = DividerConfiguration.builder()
					.dividerChar('*')
					.build();
			var divider = Clique.divider(100, config);
			var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
			assertEquals(100, output.length());
			assertTrue(output.contains("**"));
		}

		@Test
		void dividerColorString() {
			var config = DividerConfiguration.builder()
					.dividerColor("red")
					.build();
			var divider = Clique.divider(100, config);
			var output = divider.get();
			assertTrue(output.startsWith(ColorCode.RED.ansiSequence()) && output.endsWith(StyleCode.RESET.ansiSequence()));
		}

		@Test
		void dividerColorAnsiCode() {
			var config = DividerConfiguration.builder()
					.dividerColor(ColorCode.RED)
					.build();
			var divider = Clique.divider(100, config);
			var output = divider.get();
			assertTrue(output.startsWith(ColorCode.RED.ansiSequence()) && output.endsWith(StyleCode.RESET.ansiSequence()));}

		@Test
		void dividerColorWithColoredTitle() {
			var config = DividerConfiguration.builder()
					.dividerColor(ColorCode.RED)
					.build();
			var divider = Clique.divider(100, config).title("[green]Clique rocks[/]");
			var output = divider.get();
			assertTrue(output.contains(ColorCode.GREEN.ansiSequence() + "Clique rocks" + StyleCode.RESET));
			assertTrue(output.startsWith(ColorCode.RED.ansiSequence()));
			assertTrue(output.endsWith(StyleCode.RESET.ansiSequence()));
		}

		@Test
		void leftAlignedTitle() {
			var divider = Clique.divider(20).title("Clique rocks", TitleAlign.LEFT);
			var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
			assertEquals(20, output.length());
			assertEquals("─ Clique rocks ─────", output);
		}

		@Test
		void centerAlignedTitle() {
			var divider = Clique.divider(20).title("Clique rocks", TitleAlign.CENTER);
			var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
			assertEquals(20, output.length());
			assertEquals("─── Clique rocks ───", output);
		}

		@Test
		void rightAlignedTitle() {
			var divider = Clique.divider(20).title("Clique rocks", TitleAlign.RIGHT);
			var output = MarkupParser.DEFAULT.getOriginalString(divider.get());
			assertEquals(20, output.length());
			assertEquals("───── Clique rocks ─", output);
		}

		@Test
		void nullTitleAlign() {
			var divider = Clique.divider(20);
			assertThrows(NullPointerException.class, () -> divider.title("Clique rocks", null));
		}
	}
}