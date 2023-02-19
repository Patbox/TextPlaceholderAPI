package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.text.Text;

public interface TextNode {
    Text toText(ParserContext context, boolean removeBackslashes);

    default Text toText(ParserContext context) {
        return toText(context, true);
    }

    default Text toText(PlaceholderContext context) {
        return toText(context.asParserContext(), true);
    }

    default Text toText() {
        return toText(ParserContext.of(), true);
    }

    static TextNode convert(Text input) {
        return GeneralUtils.convertToNodes(input);
    }

    static TextNode of(String input) {
        return new LiteralNode(input);
    }

    static TextNode wrap(TextNode... nodes) {
        return new ParentNode(nodes);
    }

    static TextNode asSingle(TextNode... nodes) {
        return switch (nodes.length) {
            case 0 -> EmptyNode.INSTANCE;
            case 1 -> nodes[0];
            default -> wrap(nodes);
        };
    }

    static TextNode[] array(TextNode... nodes) {
        return nodes;
    }

    static TextNode empty() {
        return EmptyNode.INSTANCE;
    }
}
