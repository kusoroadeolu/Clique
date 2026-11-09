package io.github.kusoroadeolu.clique.components;


import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.Constants;

import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.clearStringBuilder;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.formatAndReset;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.align;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.chooseColAlignment;

@Stable(since = "3.2.0")
public class MarkdownTable extends AbstractTable {
    private String hLine;
    private String vLine;

    public MarkdownTable(TableConfiguration tableConfiguration) {
        super(tableConfiguration);
        this.vLine = "|";
        this.hLine = "-";
        this.colorTableBorders();

    }

    public String get() {
        final var tableBuilder = new StringBuilder();
        final var sb = new StringBuilder();

        for (int i = 0; i < this.rows.size(); i++) {
            final WidthAwareList list = this.rows.get(i);
            sb.append(this.vLine);
            for (int j = 0; j < list.size(); j++) {
                var cellAlign = this.configuration.getAlignment();
                final String styledCell = list.getStyledText(j);
                final int displayWidth = list.get(j).width();
                final WidthAwareList cl = this.columns.get(j);
                final int longest = cl.longest(); //Longest str height in each column

                final int offset = (longest - displayWidth) + this.configuration.getPadding();

                cellAlign = chooseColAlignment(j, cellAlign, this.configuration.getColumnAlignment());
                tableBuilder.append(align(cellAlign, sb, offset, styledCell, this.vLine));
                clearStringBuilder(sb);
            }

            if (i == 0) {
                tableBuilder.append(Constants.NEWLINE).append(this.appendHeader(sb));
                clearStringBuilder(sb);
            }

            tableBuilder.append(Constants.NEWLINE);
        }

        return tableBuilder.toString();
    }

    //Dynamically calculate the header for the table
    private String appendHeader(StringBuilder sb) {
        sb.append(this.vLine);
        for (final WidthAwareList col : this.columns) {
            sb.repeat(this.hLine, col.longest() + this.configuration.getPadding());
            sb.append(this.vLine);
        }

        return sb.toString();
    }


    protected void colorTableBorders() {
        var color = configuration.getBorderColor();
        if (color != null && color.length != 0){
            final var sb = new StringBuilder();
            this.hLine = formatAndReset(sb, this.hLine, color);
            this.vLine = formatAndReset(sb, this.vLine, color);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        MarkdownTable that = (MarkdownTable) object;
        return Objects.equals(hLine, that.hLine) && Objects.equals(vLine, that.vLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hLine, vLine);
    }

    @Override
    public String toString() {
        return "MarkdownTable[" +
                "hLine='" + hLine + '\'' +
                ", vLine='" + vLine + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                ", tableConfiguration=" + configuration +
                ']';
    }
}
