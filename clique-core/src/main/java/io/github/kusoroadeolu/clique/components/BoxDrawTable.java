package io.github.kusoroadeolu.clique.components;

import io.github.kusoroadeolu.clique.configuration.TableConfiguration;
import io.github.kusoroadeolu.clique.internal.WidthAwareList;
import io.github.kusoroadeolu.clique.internal.documentation.Stable;
import io.github.kusoroadeolu.clique.internal.utils.Constants;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

import java.util.Objects;

import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.clearStringBuilder;
import static io.github.kusoroadeolu.clique.internal.utils.StringUtils.formatAndReset;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.align;
import static io.github.kusoroadeolu.clique.internal.utils.TableUtils.chooseColAlignment;


@Stable(since = "3.2.0")
public class BoxDrawTable extends AbstractTable {
    String topLeft;
    String topRight;
    String bottomLeft;
    String bottomRight;
    private String hLine;
    private String vLine;
    private String topJoin;
    private String bottomJoin;
    private String leftJoin;
    private String rightJoin;

    private String cross;

    public BoxDrawTable(TableConfiguration tableConfiguration) {
        super(tableConfiguration);

        this.hLine = "─";
        this.vLine = "│";
        this.topLeft = "┌";
        this.topRight = "┐";
        this.bottomLeft = "└";
        this.bottomRight = "┘";
        this.topJoin = "┬";
        this.bottomJoin = "┴";
        this.leftJoin = "├";
        this.rightJoin = "┤";
        this.cross = "┼";
        this.colorTableBorders();
    }

    public String get() {
        //Declarations
        var tableBuilder = new StringBuilder();
        final StringBuilder helperBuilder = new StringBuilder();
        final String header = this.appendHeader(helperBuilder);
        clearStringBuilder(helperBuilder);
        final String footer = this.appendFooter(helperBuilder);
        clearStringBuilder(helperBuilder);
        final String headerEnd = this.drawHeaderEnd(helperBuilder);
        clearStringBuilder(helperBuilder);
        final int padding = this.configuration.getPadding();

        //Build
        tableBuilder.append(header).append(Constants.NEWLINE);


        for (int i = 0; i < this.rows.size(); i++) {
            final WidthAwareList list = this.rows.get(i);
            tableBuilder.append(vLine);

            for (int j = 0; j < list.size(); j++) {
                var cellAlign = this.configuration.getAlignment();
                final String styledCell = list.getStyledText(j);
                final int displayWidth = list.get(j).width();
                final WidthAwareList cl = this.columns.get(j);
                final int longest = cl.longest(); //Longest str height in each column
                final int offset = (longest - displayWidth) + padding; //Add padding to avoid cramping
                cellAlign = chooseColAlignment(j, cellAlign, this.configuration.getColumnAlignment());
                tableBuilder.append(align(cellAlign, helperBuilder, offset, styledCell, vLine));

                clearStringBuilder(helperBuilder);
            }

            if (i == 0) tableBuilder.append(Constants.NEWLINE).append(headerEnd);


            tableBuilder.append(Constants.NEWLINE);
        }

        tableBuilder.append(footer);
        return tableBuilder.toString();
    }

    public String appendHeader(StringBuilder sb) {
        return drawEdges(sb, topLeft, topJoin, topRight);
    }

    public String appendFooter(StringBuilder sb) {
        return drawEdges(sb, bottomLeft, bottomJoin, bottomRight);
    }

    public String drawHeaderEnd(StringBuilder sb) {
        return drawEdges(sb, leftJoin, cross, rightJoin);
    }

    private String drawEdges(StringBuilder sb, String left, String join, String right) {
        sb.append(left);
        for (int i = 0; i < this.columns.size(); i++) {
            final WidthAwareList l = this.columns.get(i);
            sb.repeat(hLine, l.longest() + this.configuration.getPadding());

            if (i < this.columns.size() - 1) {
                sb.append(join);
            }
        }

        sb.append(right);
        return sb.toString();
    }


    protected void colorTableBorders() {
        final StringBuilder sb = new StringBuilder();
        final AnsiCode[] borderColor = configuration.getBorderColor();

        this.hLine = formatAndReset(sb, this.hLine, borderColor);
        this.topJoin =  formatAndReset(sb, this.topJoin, borderColor);
        this.bottomJoin =   formatAndReset(sb, this.bottomJoin, borderColor);
        this.cross = formatAndReset(sb, this.cross, borderColor);

        this.vLine = formatAndReset(sb, this.vLine, borderColor);
        this.leftJoin = formatAndReset(sb, this.leftJoin, borderColor);
        this.rightJoin = formatAndReset(sb, this.rightJoin, borderColor);

        this.topLeft = formatAndReset(sb, this.topLeft, borderColor);
        this.topRight = formatAndReset(sb, this.topRight, borderColor);
        this.bottomLeft = formatAndReset(sb, this.bottomLeft, borderColor);
        this.bottomRight = formatAndReset(sb, this.bottomRight, borderColor);

    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        BoxDrawTable that = (BoxDrawTable) object;
        return Objects.equals(topLeft, that.topLeft) && Objects.equals(topRight, that.topRight) && Objects.equals(bottomLeft, that.bottomLeft) && Objects.equals(bottomRight, that.bottomRight) && Objects.equals(hLine, that.hLine) && Objects.equals(vLine, that.vLine) && Objects.equals(topJoin, that.topJoin) && Objects.equals(bottomJoin, that.bottomJoin) && Objects.equals(leftJoin, that.leftJoin) && Objects.equals(rightJoin, that.rightJoin) && Objects.equals(cross, that.cross);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                topLeft,
                topRight,
                bottomLeft,
                bottomRight,
                hLine,
                vLine,
                topJoin,
                bottomJoin,
                leftJoin,
                rightJoin,
                cross
        );
    }


    @Override
    public String toString() {
        return "BoxDrawTable[" +
                "topLeft='" + topLeft + '\'' +
                ", topRight='" + topRight + '\'' +
                ", bottomLeft='" + bottomLeft + '\'' +
                ", bottomRight='" + bottomRight + '\'' +
                ", hLine='" + hLine + '\'' +
                ", vLine='" + vLine + '\'' +
                ", topJoin='" + topJoin + '\'' +
                ", bottomJoin='" + bottomJoin + '\'' +
                ", leftJoin='" + leftJoin + '\'' +
                ", rightJoin='" + rightJoin + '\'' +
                ", cross='" + cross + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                ", tableConfiguration=" + configuration +
                ']';
    }
}