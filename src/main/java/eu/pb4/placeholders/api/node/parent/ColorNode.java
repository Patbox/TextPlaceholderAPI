package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class ColorNode extends ParentNode {
    private final TextColor color;

    public ColorNode(TextNode[] children, TextColor color) {
        super(children);
        this.color = color;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return out.setStyle(out.getStyle().withColor(this.color));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ColorNode(children, this.color);
    }
}
