package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;


public final class FormattingNode extends SimpleStylingNode implements DynamicShadowNode.SimpleColoredTransformer {
    private final ChatFormatting[] formatting;

    public FormattingNode(TextNode[] children, ChatFormatting formatting) {
        this(children, new ChatFormatting[]{ formatting });
    }

    public FormattingNode(TextNode[] children, ChatFormatting... formatting) {
        super(children);
        this.formatting = formatting;
    }

    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.applyFormats(this.formatting);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new FormattingNode(children, this.formatting);
    }

    @Override
    public String toString() {
        return "FormattingNode{" +
                "formatting=" + formatting +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    @Override
    public int getDefaultShadowColor(Component out, float scale, float alpha, ParserContext context) {
        for (var form : formatting) {
            var color = TextColor.fromLegacyFormat(form);
            if (color != null) {
                return DynamicShadowNode.modifiedColor(color.getValue(), scale, alpha);
            }
        }
        return -1;
    }

    @Override
    public boolean hasShadowColor(ParserContext context) {
        for (var form : formatting) {
            if (TextColor.fromLegacyFormat(form) != null) {
                return true;
            }
        }
        return false;
    }
}
