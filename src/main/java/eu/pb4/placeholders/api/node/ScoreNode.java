package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;

public record ScoreNode(String name, String objective) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeSingleSlash) {
        return Text.score(name, objective);
    }
}
