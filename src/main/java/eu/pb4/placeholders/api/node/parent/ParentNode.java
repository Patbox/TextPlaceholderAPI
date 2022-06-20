package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
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
        } else if (this.children.length == 1 && this.children[0] != null) {
            var out = this.children[0].toText(context, true);
            if (GeneralUtils.isEmpty(out)) {
                return out;
            }

            return ((MutableText) this.applyFormatting(out.copy(), context)).fillStyle(out.getStyle());
        } else {
            MutableText base = null;

            for (int i = 0; i < this.children.length; i++) {
                if (this.children[i] != null) {
                    var child = this.children[i].toText(context, true);

                    if (!GeneralUtils.isEmpty(child)) {
                        if (base == null) {
                            if (child.getStyle().isEmpty()) {
                                base = child.copy();
                            } else {
                                base = Text.empty();
                                base.append(child);
                            }
                        } else {
                            base.append(child);
                        }
                    }
                }
            }

            if (base == null || GeneralUtils.isEmpty(base)) {
                return Text.empty();
            }

            return this.applyFormatting(base, context);
        }
    }

    protected Text applyFormatting(MutableText out, ParserContext context) { return out; };
}
