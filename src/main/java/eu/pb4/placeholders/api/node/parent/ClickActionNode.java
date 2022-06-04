package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class ClickActionNode extends ParentNode {
    private final ClickEvent.Action action;
    private final TextNode value;

    public ClickActionNode(TextNode[] children, ClickEvent.Action action, TextNode value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    public ClickEvent.Action action() {
        return action;
    }

    public TextNode value() {
        return value;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return out.setStyle(out.getStyle().withClickEvent(new ClickEvent(this.action, this.value.toText(context, true).getString())));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ClickActionNode(children, this.action, this.value);
    }
}
