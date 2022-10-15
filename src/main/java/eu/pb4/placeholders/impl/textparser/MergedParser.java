package eu.pb4.placeholders.impl.textparser;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;

import java.util.Arrays;

public record MergedParser(NodeParser[] parsers) implements NodeParser {
    public MergedParser(NodeParser[] parsers) {
        this.parsers = Arrays.copyOf(parsers, parsers.length);
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        var out = new TextNode[]{input};
        for (int i = 0; i < this.parsers.length; i++) {
            out = parsers[i].parseNodes(TextNode.asSingle(out));
        }

        return out;
    }
}
