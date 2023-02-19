package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;

public record DirectTextNode(Text text) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return this.text;
    }
}
