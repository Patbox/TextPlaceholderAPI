package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class ColorNode extends SimpleStylingNode implements DynamicShadowNode.SimpleColoredTransformer {
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

    @Override
    public int getDefaultShadowColor(Component out, float scale, float alpha, ParserContext context) {
        return DynamicShadowNode.modifiedColor(this.color.getValue(), scale, alpha);
    }
}
