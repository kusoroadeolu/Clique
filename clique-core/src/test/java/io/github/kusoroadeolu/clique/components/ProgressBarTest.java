package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.ParserConfiguration;
import io.github.kusoroadeolu.clique.configuration.ProgressBarConfiguration;
import io.github.kusoroadeolu.clique.configuration.ProgressBarPreset;
import io.github.kusoroadeolu.clique.parser.MarkupParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ProgressBarTest {

    @Test
    void testInitialState() {
        ProgressBar bar = new ProgressBar(100);
        assertEquals(0, bar.currentTick);
        assertEquals(100, bar.total);
        assertFalse(bar.isDone());
    }

    @Test
    void testTickIncrementsProgress() {
        ProgressBar bar = new ProgressBar(100);
        bar.tick(false);
        assertEquals(1, bar.currentTick);

        bar.tick(5, false);
        assertEquals(6, bar.currentTick);
    }

    @Test
    void testTickCannotExceedTotal() {
        ProgressBar bar = new ProgressBar(10);
        bar.tick(15, false);
        assertEquals(10, bar.currentTick);  // Should cap at total
    }

    @Test
    void testTickShouldThrowOnNegativeTick() {
        ProgressBar bar = new ProgressBar(100);
        assertThrows(IllegalArgumentException.class, () -> bar.tick(-50, false));
    }

    @Test
    void testComplete() {
        ProgressBar bar = new ProgressBar(100);
        bar.tick(50, false);
        bar.complete(false);

        assertEquals(100, bar.currentTick);
        assertTrue(bar.isDone);
    }


    @Test
    void testComplete_whenComplete_completeCallShouldNotThrow(){
        ProgressBar bar = new ProgressBar(100);
        bar.tick(100, false);
        assertDoesNotThrow(() -> bar.complete(false));
    }

    @Test
    void testPercentCalculation() {
        ProgressBar bar = new ProgressBar(100);
        bar.tick(25, false);

        String output = bar.get();
        assertTrue(output.contains("25"));  // Should show 25%
    }

    @Test
    void testPercentWithZeroTotal() {
        ProgressBar bar = new ProgressBar(0);
        String output = bar.get();
        assertTrue(output.contains("  0"));  // Should handle gracefully
    }

    @Test
    void testFormatTokenReplacement() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .format(":progress/:total (:percent%)")
                .build();

        ProgressBar bar = new ProgressBar(100, config);
        bar.tick(50, false);

        String output = bar.get();
        assertTrue(output.contains("50/100"));
        assertTrue(output.contains("50"));
    }

    @Test
    void testCustomCompleteAndIncomplete() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .complete('=')
                .incomplete('-')
                .length(10)
                .format(":bar")
                .build();

        ProgressBar bar = new ProgressBar(10, config);
        bar.tick(5, false);

        String output = bar.get();
        assertTrue(output.contains("====="));
        assertTrue(output.contains("-----"));
    }

    @Test
    void testStyleRanges() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .styleRange(0, 30, "LOW :percent%")
                .styleRange(30, 70, "MID :percent%")
                .styleRange(70, 100, "HIGH :percent%")
                .build();

        ProgressBar bar = new ProgressBar(100, config);

        bar.tick(20, false);
        assertTrue(bar.get().contains("LOW"));

        bar.tick(30, false);  // Now at 50%
        assertTrue(bar.get().contains("MID"));

        bar.tick(30, false);  // Now at 80%
        assertTrue(bar.get().contains("HIGH"));
    }

    @Test
    void testStylePredicates() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .styleWhen(p -> p == 100, "COMPLETE!")
                .styleWhen(p -> p < 100, "LOADING :percent%")
                .build();

        ProgressBar bar = new ProgressBar(100, config);

        bar.tick(50, false);
        assertTrue(bar.get().contains("LOADING"));

        bar.complete(false);
        assertTrue(bar.get().contains("COMPLETE!"));
    }

    @Test
    void testStylePredicateOrderMatters() {
        // First matching predicate wins
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .styleWhen(p -> p >= 50, "FIRST")
                .styleWhen(p -> p >= 50, "SECOND")  // This won't match even at 50%
                .build();

        ProgressBar bar = new ProgressBar(100, config);
        bar.tick(75, false);

        assertTrue(bar.get().contains("FIRST") && !bar.get().contains("SECOND"));
    }

    @Test
    void testFallbackToDefaultFormat() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .format("DEFAULT :percent%")
                .styleWhen(p -> p > 200, "IMPOSSIBLE")  // Will never match
                .build();

        ProgressBar bar = new ProgressBar(100, config);
        bar.tick(50, false);

        assertTrue(bar.get().contains("DEFAULT"));
    }

    @Test
    void testElapsedTimeExists() {
        ProgressBar bar = new ProgressBar(100);
        bar.tick(false);
        String output = bar.get();
        assertTrue(output.contains("00:"));
    }

    @Test
    void testRemainingTimeWithNoProgress() {
        ProgressBar bar = new ProgressBar(100);
        String output = bar.get();
        assertTrue(output.contains("--:--"));  // No progress = can't estimate
    }

    @Test
    void testMarkupParsingInFormat() {
        ProgressBarConfiguration config = ProgressBarConfiguration.builder()
                .format("[red]:percent%[/]")
                .build();

        ProgressBar bar = new ProgressBar(100, config);
        bar.tick(50, false);

        String output = bar.get();
        assertFalse(output.contains("[red]")); //Should contain the actual ansi code instead
    }

    @Test
    void testTotalUnitsComesBeforeTotal() {
        ProgressBarConfiguration configuration = ProgressBarConfiguration.builder()
                .format("[green]:bar :percent% :total :units/:total-units[/]")
                .ticksPerUnit(2)
                .build();
        ProgressBar bar = new ProgressBar(100, configuration);
        var str = bar.get();
        assertTrue(str.contains("50") && str.contains("100"));
    }

    @Test
    void testUnitsIncrementsByTicksPerUnit() {
        ProgressBarConfiguration configuration = ProgressBarConfiguration.builder()
                .format("[green]:bar :percent% :total :units/:total-units[/]")
                .ticksPerUnit(2)
                .build();
        ProgressBar bar = new ProgressBar(100, configuration);
        bar.tick(4, false);
        assertTrue(bar.get().contains("2"));
    }

    @Test
    void assertThrowsOnZeroTickPerUnit() {
        var builder = ProgressBarConfiguration.builder()
                .format("[green]:bar :percent% :total :units/:total-units[/]");
        assertThrows(IllegalArgumentException.class, () -> builder.ticksPerUnit(0));
    }

    @Test
    void assertThrowsOnNegativeTickPerUnit() {
        var builder = ProgressBarConfiguration.builder()
                .format("[green]:bar :percent% :total :units/:total-units[/]");
        assertThrows(IllegalArgumentException.class, () -> builder.ticksPerUnit(-1));
    }

    @ParameterizedTest
    @EnumSource(ProgressBarPreset.class)
    void testAllPresetsRenderUnderStrictParsing(ProgressBarPreset preset) {
        MarkupParser strict = new MarkupParser(
                ParserConfiguration.builder().enableStrictParsing().build());

        ProgressBarConfiguration config = ProgressBarConfiguration.fromPreset(preset)
                .parser(strict)
                .build();

        ProgressBar bar = new ProgressBar(100, config);
        bar.tick(50, false);

        assertDoesNotThrow(bar::get);
    }

    @Test
    void testBlocksPresetRendersLiteralBrackets() {
        ProgressBar bar = new ProgressBar(100, ProgressBarPreset.BLOCKS.getConfiguration());
        bar.tick(50, false);
        assertTrue(bar.get().contains("[00:"));
    }
}