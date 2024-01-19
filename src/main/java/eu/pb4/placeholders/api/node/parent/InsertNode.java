package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.text.Style;

import java.util.Arrays;

public final class InsertNode extends SimpleStylingNode {
    private final TextNode value;

    public InsertNode(TextNode[] children, TextNode value) {
        super(children);
        this.value = value;
    }

    public TextNode value() {
        return this.value;
    }

    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.withInsertion(value.toText(context, true).getString());
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new InsertNode(children, this.value);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new InsertNode(children, TextNode.asSingle(parser.parseNodes(this.value)));
    }

    @Override
    public String toString() {
        return "InsertNode{" +
                "value=" + value +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    @Override
    public boolean isDynamicNoChildren() {
        return this.value.isDynamic();
    }
}
