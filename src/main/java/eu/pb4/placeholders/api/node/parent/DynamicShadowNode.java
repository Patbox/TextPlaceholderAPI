package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public final class DynamicShadowNode extends ParentNode {
    private final float scale;
    private final float alpha;

    public DynamicShadowNode(TextNode[] children) {
        this(children, 0.25f, 1f);
    }

    public DynamicShadowNode(TextNode[] children, float scale, float alpha) {
        super(children);
        this.scale = scale;
        this.alpha = alpha;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        var transformer = context.get(ParserContext.Key.DEFAULT_SHADOW_STYLER);
        if (transformer == null) {
            var defaultColor = modifiedColor(out.getStyle().getColor() != null ? out.getStyle().getColor().getRgb() : 0xFFFFFF, this.scale, this.alpha);

            return GeneralUtils.cloneTransformText(out, text -> {
                var color = text.getStyle().getColor();
                return text.setStyle(text.getStyle().withShadowColor(color != null ? modifiedColor(color.getRgb(), this.scale, this.alpha) : defaultColor));
            }, text -> text == out || text.getStyle().getShadowColor() == null && text.getStyle().getColor() != null);
        }


        return transformer.applyShadowColors(out, this.scale, this.alpha, context);
    }

    public static int modifiedColor(int color, float scale, float alpha) {
        return ColorHelper.scaleRgb(color, scale) | 0xFF000000;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new DynamicShadowNode(children, this.scale, this.alpha);
    }

    @Override
    public String toString() {
        return "DynamicShadowNode{" +
                "scale=" + scale +
                '}';
    }

    public interface Transformer {
        Text applyShadowColors(Text text, float scale, float alpha, ParserContext context);

        default boolean hasShadowColor(ParserContext context) {
            return true;
        }
    }

    public interface SimpleColoredTransformer extends Transformer {
        @Override
        default Text applyShadowColors(Text out, float scale, float alpha, ParserContext context) {
            var defaultColor = this.getDefaultShadowColor(out, scale, alpha, context);
            return GeneralUtils.cloneTransformText(out, text -> {
                var color = text.getStyle().getColor();
                return text.setStyle(text.getStyle().withShadowColor(color != null ? modifiedColor(color.getRgb(), scale, alpha) : defaultColor));
            }, text -> text == out || text.getStyle().getShadowColor() == null && text.getStyle().getColor() != null);
        }

        int getDefaultShadowColor(Text out, float scale, float alpha, ParserContext context);
    }
}
