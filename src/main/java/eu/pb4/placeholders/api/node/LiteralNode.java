package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;

public record LiteralNode(String value) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeSingleSlash) {
        return Text.literal(removeSingleSlash
                ? this.value.replace("\\\\", "\001")
                            .replace("\\", "")
                            .replace("\001", "\\")
                : this.value());
    }
}
