package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;

public record DirectComponentNode(Component component) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return this.component;
    }
}
