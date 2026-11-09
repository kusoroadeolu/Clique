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
public class AsciiTable extends AbstractTable {
    private String corner;
    private String hLine;
    private String vLine;

    public AsciiTable(TableConfiguration tableConfiguration) {
        super(tableConfiguration);
        this.corner = "+";
        this.hLine = "-";
        this.vLine = "|";
        this.colorTableBorders();

    }

    public String get() {
        //Declarations
        final var tableBuilder = new StringBuilder();
        final var sb = new StringBuilder();
        final var headerAndFooter = this.appendHeader(sb);
        final int padding = this.configuration.getPadding();
        clearStringBuilder(sb);

        //Build
        tableBuilder.append(headerAndFooter).append(Constants.NEWLINE);

        for (final WidthAwareList list : this.rows) {
            tableBuilder.append(vLine);

            for (int j = 0; j < list.size(); j++) {
                var cellAlign = this.configuration.getAlignment();
                final String styledCell = list.getStyledText(j);
                final int displayWidth = list.get(j).width();
                final WidthAwareList cl = this.columns.get(j);
                final int longest = cl.longest(); //Longest str height in each column

                final int offset = (longest - displayWidth) + padding;

                cellAlign = chooseColAlignment(j, cellAlign, this.configuration.getColumnAlignment());
                tableBuilder.append(align(cellAlign, sb, offset, styledCell, vLine));
                clearStringBuilder(sb);
            }

            tableBuilder.append(Constants.NEWLINE);
            tableBuilder.append(headerAndFooter).append(Constants.NEWLINE);
        }

        return tableBuilder.toString();
    }

    //Dynamically calculate the header and footer for the table
    private String appendHeader(StringBuilder sb) {
        for (final WidthAwareList l : this.columns) {
            sb.append(corner);
            sb.repeat(hLine, l.longest() + this.configuration.getPadding());
        }
        sb.append(corner);
        return sb.toString();
    }


    protected void colorTableBorders() {
        var color = configuration.getBorderColor();
        if (color != null && color.length != 0){
            final var sb = new StringBuilder();
            this.hLine = formatAndReset(sb, this.hLine, color);
            this.vLine = formatAndReset(sb, this.vLine, color);
            this.corner = formatAndReset(sb, this.corner, color);
        }

    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        AsciiTable that = (AsciiTable) object;
        return Objects.equals(corner, that.corner) && Objects.equals(hLine, that.hLine) && Objects.equals(vLine, that.vLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), corner, hLine, vLine);
    }

    @Override
    public String toString() {
        return "AsciiTable[" +
                "tableConfiguration=" + configuration +
                ", vLine='" + vLine + '\'' +
                ", hLine='" + hLine + '\'' +
                ", edge='" + corner + '\'' +
                ", rows=" + rows +
                ", columns=" + columns +
                ']';
    }
}
