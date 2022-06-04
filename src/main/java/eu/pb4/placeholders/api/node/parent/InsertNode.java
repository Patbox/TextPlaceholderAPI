package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class InsertNode extends ParentNode {
    private final TextNode value;

    public InsertNode(TextNode[] children, TextNode value) {
        super(children);
        this.value = value;
    }

    public TextNode value() {
        return this.value;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return out.setStyle(out.getStyle().withInsertion(value.toText(context, true).getString()));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new InsertNode(children, this.value);
    }
}
