package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;

public record KeybindNode(String value) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return Component.keybind(this.value());
    }
}
