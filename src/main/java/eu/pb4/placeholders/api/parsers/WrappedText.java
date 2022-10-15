package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.Text;

public record WrappedText(String input, TextNode textNode, Text text) {
    public static WrappedText from(NodeParser parser, String input) {
        var node = TextNode.asSingle(parser.parseNodes(TextNode.of(input)));

        return new WrappedText(input, node, node.toText(ParserContext.of(), true));
    }
}
