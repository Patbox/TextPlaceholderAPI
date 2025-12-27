package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;

public record DirectTextNode(Component text) implements TextNode {
    @Override
    public Component toText(ParserContext context, boolean removeBackslashes) {
        return this.text;
    }
}
