package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.world.item.component.ResolvableProfile;

public record DynamicPlayerHeadNode(TextNode name, boolean hat, Type type) implements TextNode {
    @Override
    public Component toText(ParserContext context, boolean removeBackslashes) {
        var val = this.name.toText(context).getString();

        if (type == Type.UUID || type == Type.EITHER) {
            try {
                return Component.object(new PlayerSprite(ResolvableProfile.createUnresolved(UUID.fromString(val)), hat));
            } catch (Throwable e) {
                // ignore
            }
        }

        if (type == Type.NAME || type == Type.EITHER) {
            return Component.object(new PlayerSprite(ResolvableProfile.createUnresolved(val), hat));
        }

        return Component.object(new PlayerSprite(ResolvableProfile.createUnresolved(""), hat));
    }

    public enum Type {
        UUID,
        NAME,
        EITHER
    }
}
