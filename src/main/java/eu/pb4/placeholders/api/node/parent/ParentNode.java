package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

public class ParentNode implements ParentTextNode {
    public static final ParentNode EMPTY = new ParentNode(new TextNode[0]);
    protected final TextNode[] children;

    public ParentNode(TextNode[] children) {
        this.children = children;
    }

    @Override
    public final TextNode[] getChildren() {
        return this.children;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ParentNode(children);
    }

    @Override
    public final Text toText(ParserContext context, boolean removeSingleSlash) {
        if (this.children.length == 0) {
            return Text.empty();
        } else if (this.children.length == 1) {
            var out = this.children[0].toText(context, true);
            return ((MutableText) this.applyFormatting(out.copy(), context)).fillStyle(out.getStyle());
        } else {
            var base =  Text.empty();

            for (int i = 0; i < this.children.length; i++) {
                var child = this.children[i].toText(context, true);

                if (child.getContent() != TextContent.EMPTY || child.getSiblings().size() > 0) {
                    base.append(child);
                }
            }

            return this.applyFormatting(base, context);
        }
    }

    protected Text applyFormatting(MutableText out, ParserContext context) { return out; };
}
