package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.class_10104;
import net.minecraft.text.Text;

import java.util.Optional;

public record SelectorNode(class_10104 selector, Optional<TextNode> separator) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return Text.selector(selector, separator.map(x -> x.toText(context, removeBackslashes)));
    }

    @Override
    public boolean isDynamic() {
        return separator.isPresent() && separator.get().isDynamic();
    }
}
