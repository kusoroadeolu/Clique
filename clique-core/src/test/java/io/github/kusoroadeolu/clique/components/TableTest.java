package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.configuration.CellAlign;
import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.configuration.TableType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    @Test
    void testTableStructure() {
        Table table = Clique.table()
                .headers("A", "B")
                .row("1", "2");
        String output = table.get();
        List<String> lines = output.lines().toList();

        assertNotNull(output);
        assertTrue(lines.get(1).contains("A"));
        assertTrue(lines.get(1).contains("B"));
        assertTrue(lines.get(3).contains("1"));
        assertTrue(lines.get(3).contains("2"));
    }

    @Test
    void testTableCellUpdate() {
        Table table = Clique.table()
                .headers("A", "B")
                .row("1", "2");
        table.updateCell(1, 0, "99");
        String output = table.get();
        assertTrue(output.contains("99"));
    }

    @Test
    void testTableCellRemove() {
        var config = TableConfiguration.builder().nullReplacement("N/A").build();
        Table table = Clique.table(config)
                .headers("A", "B")
                .row("1", "2");
        table.removeCell(1, 0);

        String output = table.get();
        List<String> lines = output.lines().toList();
        assertNotNull(output);
        assertFalse(lines.get(3).contains("1"));
        assertTrue(lines.get(3).contains("N/A"));
    }

    @Test
    void assertThrows_onHeaderRemoval() {
        var config = TableConfiguration.builder().nullReplacement("N/A").build();
        Table table = Clique.table(config)
                .headers("A", "B")
                .row("1", "2");
        assertThrows(IllegalArgumentException.class, () -> table.removeCell(0, 1));
    }

    @Test
    void assertThrows_onInvalidRowIndex(){
        Table table = Clique.table()
                .headers("A", "B")
                .row("1", "2");
        assertThrows(IndexOutOfBoundsException.class, () -> table.removeCell(3, 1));
    }

    @Test
    void assertThrows_onInvalidColIndex(){
        Table table = Clique.table()
                .headers("A", "B")
                .row("1", "2");

        assertThrows(IndexOutOfBoundsException.class, () -> table.removeCell(1, 3));
    }

    @Test
    void assertPadding_shouldHaveThreeSpaces_whenLeftAligned(){
        var config = TableConfiguration.builder().padding(3).build();
        Table table = Clique.table(TableType.ASCII, config)
                .headers("A", "B")
                .row("1", "2");
        var lines = table.get().lines().toList();
        var firstCol = lines.get(1);
        String[] arr = firstCol.split("\\|");
        String threeSpaces = " ".repeat(3);
        assertEquals(threeSpaces + "A" + threeSpaces, arr[1]);
        assertEquals(threeSpaces + "B" + threeSpaces, arr[2]);
    }

    @Test
    void assertPadding_shouldHaveThreeSpaces_whenRightAligned(){
        var config = TableConfiguration.builder()
                .columnAlignment(Map.of(0, CellAlign.RIGHT, 1, CellAlign.RIGHT))
                .padding(3)
                .build();
        Table table = Clique.table(TableType.ASCII, config)
                .headers("A")
                .row("1");
        var lines = table.get().lines().toList();
        var firstCol = lines.get(1);

        String[] arr = firstCol.split("\\|");
        String threeSpaces = " ".repeat(3);

        assertEquals(threeSpaces + "A" + threeSpaces, arr[1]);
    }


    @Test
    void assertNotSame_onSubsequentGetCalls_onRowCall() {
        Table table = Clique.table(TableType.ASCII)
                .headers("A", "B")
                .row("1", "2");
        var str1 = table.get();
        table.row("1");

        assertNotSame(str1, table.get());
    }

    @Test
    void row_withMoreCellsThanHeaders_throws() {
        Table table = Clique.table(TableType.ASCII).headers("Name", "Age", "Class");

        var ex = assertThrows(IllegalArgumentException.class,
                () -> table.row("John", "25", "Class A", "extra"));

        assertEquals("Row has 4 cells but the table only has 3 columns", ex.getMessage());
    }

    @Test
    void row_withMoreCellsThanHeaders_doesNotAddRow() {
        Table table = Clique.table(TableType.ASCII).headers("Name", "Age");
        String before = table.get();

        assertThrows(IllegalArgumentException.class, () -> table.row("John", "25", "extra"));

        assertEquals(before, table.get()); // failed call left the table untouched
    }

    @Test
    void row_withExactCellCount_succeeds() {
        Table table = Clique.table(TableType.ASCII).headers("Name", "Age");

        assertDoesNotThrow(() -> table.row("John", "25"));
    }

    @Test
    void row_withFewerCells_padsWithNullReplacement() {
        var config = TableConfiguration.builder().nullReplacement("N/A").build();
        Table table = Clique.table(TableType.ASCII, config).headers("Name", "Age", "Class");

        table.row("John");

        assertTrue(table.get().contains("N/A"));
    }

    @Test
    void fromRows_withRowLongerThanHeaders_throws() {
        var rows = List.of(
                List.of("Name", "Age"),
                List.of("John", "25", "extra")
        );

        assertThrows(IllegalArgumentException.class,
                () -> Clique.table(TableType.ASCII).fromRows(rows));
    }
}