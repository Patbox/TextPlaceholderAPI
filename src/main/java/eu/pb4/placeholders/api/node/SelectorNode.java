package eu.pb4.placeholders.api.node;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.ParserContext;
import java.util.Optional;

import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CompilableString;

public record SelectorNode(TextNode selector, Optional<TextNode> separator) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var ctx = context.get(ParserContext.Key.HOLDER_LOOKUP);

        var selectorBacking = selector.toComponent(context, removeBackslashes).getString();

        var pattern = EntitySelector.COMPILABLE_CODEC.decode(ctx != null ? ctx.createSerializationContext(StringArgOps.INSTANCE) : StringArgOps.INSTANCE,
                Either.left(selectorBacking));


        if (pattern.isError()) {
            return Component.literal(selectorBacking);
        }

        return Component.selector(pattern.getOrThrow().getFirst(), separator.map(x -> x.toComponent(context, removeBackslashes)));
    }

    @Override
    public boolean isDynamic() {
        return separator.isPresent() && separator.get().isDynamic();
    }
}
