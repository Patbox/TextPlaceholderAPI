package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;


public final class FormattingNode extends SimpleStylingNode implements DynamicShadowNode.SimpleColoredTransformer {
    private final ChatFormatting[] formatting;
    private final @Nullable TextColor color;

    public FormattingNode(TextNode[] children, ChatFormatting formatting) {
        this(children, new ChatFormatting[]{formatting});
    }

    public FormattingNode(TextNode[] children, ChatFormatting... formatting) {
        super(children);
        this.formatting = formatting;

        TextColor color = null;
        for (var x : formatting) {
            var c = TextColor.fromLegacyFormat(x);
            if (c != null) {
                color = c;
            }
        }
        this.color = color;
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
        if (this.color != null) {
            //noinspection DataFlowIssue
            return DynamicShadowNode.modifiedColor(this.color.getValue(), scale, alpha);
        }

        return -1;
    }

    @Override
    public boolean hasShadowColor(ParserContext context) {
        return this.color != null;
    }
}
