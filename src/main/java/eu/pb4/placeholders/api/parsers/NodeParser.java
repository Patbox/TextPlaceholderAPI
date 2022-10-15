package eu.pb4.placeholders.api.parsers;

import com.mojang.serialization.Codec;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.textparser.MergedParser;

import java.util.List;

public interface NodeParser {
    NodeParser NOOP = i -> new TextNode[] { i };

    TextNode[] parseNodes(TextNode input);


    default Codec<WrappedText> codec() {
        return Codec.STRING.xmap(x -> WrappedText.from(this, x), w -> w.input());
    }

    static NodeParser merge(NodeParser... parsers) {
        return switch (parsers.length) {
            case 0 -> NOOP;
            case 1 -> parsers[0];
            default -> new MergedParser(parsers);
        };
    }

    static NodeParser merge(List<NodeParser> parsers) {
        return switch (parsers.size()) {
            case 0 -> NOOP;
            case 1 -> parsers.get(0);
            default -> new MergedParser(parsers.toArray(new NodeParser[0]));
        };
    }
}
