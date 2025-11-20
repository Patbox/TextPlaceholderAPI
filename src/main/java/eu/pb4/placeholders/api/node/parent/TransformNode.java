package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class TransformNode extends ParentNode {
    private final Function<MutableComponent, Component> transform;

    public TransformNode(TextNode[] nodes, Function<MutableComponent, Component> transform) {
        super(nodes);
        this.transform = transform;
    }

    public static TransformNode deepStyle(Function<Style, Style> styleFunction, TextNode... nodes) {
        return new TransformNode(nodes, new GeneralUtils.MutableTransformer(styleFunction));
    }

    @Override
    protected Component applyFormatting(MutableComponent out, ParserContext context) {
        return this.transform.apply(out);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new TransformNode(children, this.transform);
    }

    @Override
    public String toString() {
        return "TransformNode{" +
                "transform=" + transform +
                ", children=" + Arrays.toString(children) +
                '}';
    }
}
