package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import java.util.Arrays;
import net.minecraft.network.chat.Style;

public final class StrikethroughNode extends SimpleStylingNode {
    private static final Style TRUE = Style.EMPTY.withStrikethrough(true);
    private static final Style FALSE = Style.EMPTY.withStrikethrough(false);
    private final boolean value;

    public StrikethroughNode(TextNode[] nodes, boolean value) {
        super(nodes);
        this.value = value;
    }

    @Override
    protected Style style(ParserContext context) {
        return this.value ? TRUE : FALSE;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new StrikethroughNode(children, this.value);
    }

    @Override
    public String toString() {
        return "StrikethroughNode{" +
                "children=" + Arrays.toString(children) +
                ", value=" + value +
                '}';
    }
}
