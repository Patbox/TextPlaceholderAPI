package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;

import java.util.Optional;
import java.util.UUID;

import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.world.item.component.ResolvableProfile;

public record DynamicPlayerHeadNode(TextNode name, boolean hat, Type type, Optional<TextNode> fallback) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var val = this.name.toComponent(context).getString();
        var fallback = this.fallback.map(x -> x.toComponent(context, removeBackslashes));

        if (type == Type.UUID || type == Type.EITHER) {
            try {
                return GeneralUtils.objectComponent(new PlayerSprite(ResolvableProfile.createUnresolved(UUID.fromString(val)), hat), fallback);
            } catch (Throwable e) {
                // ignore
            }
        }

        if (type == Type.NAME || type == Type.EITHER) {
            try {
                return GeneralUtils.objectComponent(new PlayerSprite(ResolvableProfile.createUnresolved(val), hat), fallback);
            } catch (Throwable e) {
                // ignore
            }
        }



        return GeneralUtils.objectComponent(new PlayerSprite(ResolvableProfile.createUnresolved(""), hat), fallback);
    }

    public enum Type {
        UUID,
        NAME,
        EITHER
    }
}
