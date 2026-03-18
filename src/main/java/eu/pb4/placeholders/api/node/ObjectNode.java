package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.ObjectInfo;

import java.util.Optional;

public record ObjectNode(ObjectInfo content, Optional<TextNode> fallback) implements TextNode {

    public ObjectNode(ObjectInfo content) {
        this(content, Optional.empty());
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        if (this.fallback.isEmpty()) {
            return Component.object(content);
        }

        return Component.object(content, fallback.orElseThrow().toComponent(context, removeBackslashes));
    }
}
