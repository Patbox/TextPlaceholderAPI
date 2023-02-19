package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;

public record EmptyNode() implements TextNode {
    public static final EmptyNode INSTANCE = new EmptyNode();
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return Text.empty();
    }
}
