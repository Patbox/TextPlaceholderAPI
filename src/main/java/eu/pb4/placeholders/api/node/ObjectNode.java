package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;
import net.minecraft.text.object.TextObjectContents;

public record ObjectNode(TextObjectContents content) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return Text.method_74062(content);
    }
}
