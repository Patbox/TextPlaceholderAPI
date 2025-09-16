package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public final class FontNode extends SimpleStylingNode {
    private final Identifier font;

    public FontNode(TextNode[] children, Identifier font) {
        super(children);
        this.font = font;
    }

    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.withFont(new StyleSpriteSource.Font(this.font));
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
