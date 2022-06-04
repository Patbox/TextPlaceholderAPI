package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.text.Text;

public interface TextNode {
    Text toText(ParserContext context, boolean removeSingleSlash);


    static TextNode convert(Text input) {
        return GeneralUtils.convertToNodes(input);
    }
}
