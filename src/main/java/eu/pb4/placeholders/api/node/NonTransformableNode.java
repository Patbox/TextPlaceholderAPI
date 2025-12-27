package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;

/**
 * It works as long as no parser implements support for it™
 */
public record NonTransformableNode(TextNode node) implements TextNode {
    @Override
    public Component toText(ParserContext context, boolean removeBackslashes) {
        return node.toText(context, removeBackslashes);
    }

    @Override
    public boolean isDynamic() {
        return node.isDynamic();
    }
}
