package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.Style;

import java.util.Arrays;

public final class UnderlinedNode extends SimpleStylingNode {
    private static final Style TRUE = Style.EMPTY.withUnderline(true);
    private static final Style FALSE = Style.EMPTY.withUnderline(false);
    private final boolean value;

    public UnderlinedNode(TextNode[] nodes, boolean value) {
        super(nodes);
        this.value = value;
    }

    @Override
    protected Style style(ParserContext context) {
        return this.value ? TRUE : FALSE;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new UnderlinedNode(children, this.value);
    }

    @Override
    public String toString() {
        return "UnderlinedNode{" +
                "children=" + Arrays.toString(children) +
                ", value=" + value +
                '}';
    }
}
