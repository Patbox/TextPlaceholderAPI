package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.Arrays;

public final class ColorNode extends SimpleStylingNode {
    private final TextColor color;

    public ColorNode(TextNode[] children, TextColor color) {
        super(children);
        this.color = color;
    }

    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.withColor(this.color);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ColorNode(children, this.color);
    }

    @Override
    public String toString() {
        return "ColorNode{" +
                "color=" + color +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
