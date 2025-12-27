package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class DynamicColorNode extends SimpleStylingNode implements DynamicShadowNode.SimpleColoredTransformer {
    private static final Function<String, TextColor> DEFAULT_RESOLVER = string -> TextColor.parseColor(string).result().orElse(null);
    private final TextNode color;
    private final Function<String, TextColor> resolver;

    public DynamicColorNode(TextNode[] children, TextNode color) {
        this(children, color, DEFAULT_RESOLVER);
    }
    public DynamicColorNode(TextNode[] children, TextNode color, Function<String, @Nullable TextColor> resolver) {
        super(children);
        this.color = color;
        this.resolver = resolver;
    }

    public static Function<String, @Nullable TextColor> extendedTextColorParse(Function<String, @Nullable TextColor> resolver) {
        return string -> {
            var x = resolver.apply(string);
            if (x != null) {
                return x;
            }
            return TextColor.parseColor(string).result().orElse(null);
        };
    }

    @Override
    public boolean isDynamicNoChildren() {
        return this.color.isDynamic();
    }

    @Override
    protected Style style(ParserContext context) {
        var c = this.resolver.apply(color.toComponent(context).getString());
        return c != null ? Style.EMPTY.withColor(c) : Style.EMPTY;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new DynamicColorNode(children, this.color);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new DynamicColorNode(children, parser.parseNode(color));
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
        var color = TextColor.parseColor(this.color.toComponent(context).getString());

        if (color.result().isPresent()) {
            return DynamicShadowNode.modifiedColor(color.getOrThrow().getValue(), scale, alpha);
        }
        return 0;
    }

    @Override
    public boolean hasShadowColor(ParserContext context) {
        return TextColor.parseColor(this.color.toComponent(context).getString()).result().isPresent();
    }
}
