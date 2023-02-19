package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;

public record KeybindNode(String value) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return Text.keybind(this.value());
    }
}
