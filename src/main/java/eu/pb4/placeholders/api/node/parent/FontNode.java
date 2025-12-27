package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import java.util.Arrays;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class FontNode extends SimpleStylingNode {
    private final Identifier font;

    public FontNode(TextNode[] children, Identifier font) {
        super(children);
        this.font = font;
    }

    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.withFont(new FontDescription.Resource(font));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new FontNode(children, this.font);
    }

    @Override
    public String toString() {
        return "FontNode{" +
                "font=" + font +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
