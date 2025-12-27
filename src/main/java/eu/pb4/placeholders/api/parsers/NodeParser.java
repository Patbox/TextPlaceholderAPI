package eu.pb4.placeholders.api.parsers;

import com.mojang.serialization.Codec;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.textparser.MergedParser;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface NodeParser {
    NodeParser NOOP = i -> new TextNode[] { i };

    TextNode[] parseNodes(TextNode input);

    default TextNode parseNode(TextNode input) {
        return TextNode.asSingle(this.parseNodes(input));
    }

    default TextNode parseNode(String input) {
        return this.parseNode(TextNode.of(input));
    }

    default Component parseText(TextNode input, ParserContext context) {
        return TextNode.asSingle(this.parseNodes(input)).toText(context, true);
    }

    default Component parseText(String input, ParserContext context) {
        return parseText(TextNode.of(input), context);
    }

    default Codec<WrappedText> codec() {
        return Codec.STRING.xmap(x -> WrappedText.from(this, x), WrappedText::input);
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

    static ParserBuilder builder() {
        return new ParserBuilder();
    }
}
