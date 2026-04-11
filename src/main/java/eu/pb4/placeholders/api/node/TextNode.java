package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface TextNode {
    Component toComponent(ParserContext context, boolean removeBackslashes);

    default Component toComponent(ParserContext context) {
        return toComponent(context, true);
    }

    default Component toComponent(PlaceholderContext context) {
        return toComponent(context.asParserContext(), true);
    }

    default Component toComponent() {
        return toComponent(ParserContext.of(), true);
    }

    default boolean isDynamic() {
        return false;
    }

    static TextNode convert(Component input) {
        return GeneralUtils.convertToNodes(input);
    }

    static TextNode of(String input) {
        return new LiteralNode(input);
    }

    static TextNode wrap(TextNode... nodes) {
        return new ParentNode(nodes);
    }

    static TextNode wrap(List<TextNode> nodes) {
        return new ParentNode(nodes.toArray(GeneralUtils.CASTER));
    }

    static TextNode asSingle(TextNode... nodes) {
        return switch (nodes.length) {
            case 0 -> EmptyNode.INSTANCE;
            case 1 -> nodes[0];
            default -> wrap(nodes);
        };
    }

    static TextNode asSingle(List<TextNode> nodes) {
        return switch (nodes.size()) {
            case 0 -> EmptyNode.INSTANCE;
            case 1 -> nodes.get(0);
            default -> wrap(nodes);
        };
    }

    static TextNode[] array(TextNode... nodes) {
        if (nodes.length == 1 && nodes[0].getClass() == ParentNode.class) {
            return ((ParentNode) nodes[0]).getChildren();
        }

        return nodes;
    }

    static TextNode empty() {
        return EmptyNode.INSTANCE;
    }
}
