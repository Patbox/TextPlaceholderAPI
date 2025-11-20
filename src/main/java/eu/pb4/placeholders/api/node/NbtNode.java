package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.data.DataSource;

public record NbtNode(String rawPath, boolean interpret, Optional<TextNode> separator, DataSource dataSource) implements TextNode {
    @Override
    public Component toText(ParserContext context, boolean removeBackslashes) {
        return Component.nbt(rawPath, interpret, separator.map(x -> x.toText(context, removeBackslashes)), dataSource);
    }

    @Override
    public boolean isDynamic() {
        return separator.isPresent() && separator.get().isDynamic();
    }
}
