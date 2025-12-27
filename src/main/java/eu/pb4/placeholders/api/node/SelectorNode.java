package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import java.util.Optional;
import net.minecraft.commands.arguments.selector.SelectorPattern;
import net.minecraft.network.chat.Component;

public record SelectorNode(SelectorPattern selector, Optional<TextNode> separator) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return Component.selector(selector, separator.map(x -> x.toComponent(context, removeBackslashes)));
    }

    @Override
    public boolean isDynamic() {
        return separator.isPresent() && separator.get().isDynamic();
    }
}
