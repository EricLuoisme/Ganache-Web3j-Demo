package com.own.third.api.md;

import net.steppschuh.markdowngenerator.table.Table;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MarkdownTest {

    @Test
    public void md2Img() {

        // Markdown table
        final Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
                .withRowLimit(2)
                .addRow("Date", "START", "END", "BTC", "USDT");
        tableBuilder.addRow("2024-01-11", "05:55:47", "06:55:47", "-0.00321", "+124.46713429");
        String text = tableBuilder.build().toString();

        text = "\uD83D\uDCB0*Money Flow Checking*\n" +
                "```BTCUSDT \n" +
                text +
                "\n```";

        // convert to HTML
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(text);

        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String htmlContent = renderer.render(document);
        System.out.println("HTML Content:\n" + htmlContent);
    }
}
