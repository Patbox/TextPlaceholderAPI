package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.ObjectInfo;

public record ObjectNode(ObjectInfo content) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return Component.object(content);
    }
}
