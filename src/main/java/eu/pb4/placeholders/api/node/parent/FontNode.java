package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class FontNode extends ParentNode {
    private final Identifier font;

    public FontNode(TextNode[] children, Identifier font) {
        super(children);
        this.font = font;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return out.setStyle(out.getStyle().withFont(font));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new FontNode(children, this.font);
    }
}
