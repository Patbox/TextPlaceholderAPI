package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

@ApiStatus.Internal
public record PlaceholderNode(PlaceholderHandler handler, boolean optionalContext, @Nullable String argument) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeSingleSlash) {
        var ctx = context.get(PlaceholderContext.KEY);
        return ctx != null || this.optionalContext ? handler.onPlaceholderRequest(ctx, argument).text() : Text.empty();
    }
}
