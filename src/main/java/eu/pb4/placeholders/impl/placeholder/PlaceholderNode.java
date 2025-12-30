package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.Placeholder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;


@ApiStatus.Internal
public record PlaceholderNode<Ctx>(ParserContext.Key<Ctx> contextKey, Placeholder.ArgumentedHandler<Ctx, ?> handler) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var ctx = context.get(contextKey);
        if (ctx != null) {
            try {
                return handler.onPlaceholderRequest(ctx).component();
            } catch (Throwable e) {
                GeneralUtils.LOGGER.error("Error occurred while parsing placeholder " + handler.placeholder().identifier() + " / " + contextKey.key() + "!", e);
                return Component.empty();
            }
        } else {
            if (GeneralUtils.IS_DEV) {
                GeneralUtils.LOGGER.error("Missing context for placeholders requiring them (" + handler.placeholder().identifier() + " / " + contextKey.key() + ")!", new NullPointerException());
            }
            return Component.empty();
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
