package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ParentNode implements ParentTextNode {
    public static final ParentNode EMPTY = new ParentNode(new TextNode[0]);
    protected final TextNode[] children;

    public ParentNode(TextNode... children) {
        this.children = children;
    }

    public ParentNode(Collection<TextNode> children) {
        this(children.toArray(GeneralUtils.CASTER));
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
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var compact = context.get(ParserContext.Key.COMPACT_COMPONENT) != Boolean.FALSE;
        var oldShadow = context.get(ParserContext.Key.DEFAULT_SHADOW_STYLER);

        if (this instanceof DynamicShadowNode.Transformer transformer && transformer.hasShadowColor(context)) {
            context.with(ParserContext.Key.DEFAULT_SHADOW_STYLER, transformer);
        }

        if (this.children.length == 0) {
            context.with(ParserContext.Key.DEFAULT_SHADOW_STYLER, oldShadow);
            return Component.empty();
        } else if ((this.children.length == 1 && this.children[0] != null) && compact) {
            var out = this.children[0].toComponent(context, true);
            if (GeneralUtils.isEmpty(out)) {
                return out;
            }
            context.with(ParserContext.Key.DEFAULT_SHADOW_STYLER, oldShadow);
            return this.applyFormatting(out.copy(), context);
        } else {
            MutableComponent base = compact ? null : Component.empty();

            for (int i = 0; i < this.children.length; i++) {
                if (this.children[i] != null) {
                    var child = this.children[i].toComponent(context, true);

                    if (!GeneralUtils.isEmpty(child)) {
                        if (base == null) {
                            if (child.getStyle().isEmpty()) {
                                base = child.copy();
                            } else {
                                base = Component.empty();
                                base.append(child);
                            }
                        } else {
                            base.append(child);
                        }
                    }
                }
            }
            context.with(ParserContext.Key.DEFAULT_SHADOW_STYLER, oldShadow);

            if (base == null || GeneralUtils.isEmpty(base)) {
                return Component.empty();
            }

            return this.applyFormatting(base, context);
        }
    }

    protected Component applyFormatting(MutableComponent out, ParserContext context) {
        return out.setStyle(applyFormatting(out.getStyle(), context));
    }

    protected Style applyFormatting(Style style, ParserContext context) {
        return style;
    }

    @Override
    public String toString() {
        return "ParentNode{" +
                "children=" + Arrays.toString(children) +
                '}';
    }
}
