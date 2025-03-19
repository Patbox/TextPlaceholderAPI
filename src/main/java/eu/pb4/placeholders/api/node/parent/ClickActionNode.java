package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;

import java.net.URI;

public final class ClickActionNode extends SimpleStylingNode {
    private final Action action;
    private final TextNode value;

    public ClickActionNode(TextNode[] children, Action action, TextNode value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    public Action action() {
        return action;
    }

    public TextNode value() {
        return value;
    }

    @Override
    protected Style style(ParserContext context) {
        if (this.action == Action.OPEN_URL) {
            try {
                return Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(this.value.toText(context).getString())));
            } catch (Exception ignored) {
                return Style.EMPTY;
            }
        } else if (this.action == Action.CHANGE_PAGE) {
            try {
                return Style.EMPTY.withClickEvent(new ClickEvent.ChangePage(Integer.parseInt(this.value.toText(context).getString())));
            } catch (Exception ignored) {
                return Style.EMPTY;
            }
        } else if (this.action == Action.OPEN_FILE) {
            return Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(this.value.toText(context).getString()));
        } else if (this.action == Action.RUN_COMMAND) {
            return Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(this.value.toText(context).getString()));
        } else if (this.action == Action.SUGGEST_COMMAND) {
            return Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand(this.value.toText(context).getString()));
        } else if (this.action == Action.COPY_TO_CLIPBOARD) {
            return Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(this.value.toText(context).getString()));
        } else {
            return Style.EMPTY;
        }
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ClickActionNode(children, this.action, this.value);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new ClickActionNode(children, this.action, TextNode.asSingle(parser.parseNodes(this.value)));
    }

    @Override
    public boolean isDynamicNoChildren() {
        return this.value.isDynamic();
    }

    @Override
    public String toString() {
        return "ClickActionNode{" +
                "action=" + action +
                ", value=" + value +
                '}';
    }

    public record Action(ClickEvent.Action vanillaType) {
        public static final Action OPEN_URL = new Action(ClickEvent.Action.OPEN_URL);
        public static final Action CHANGE_PAGE = new Action(ClickEvent.Action.CHANGE_PAGE);
        public static final Action OPEN_FILE = new Action(ClickEvent.Action.OPEN_FILE);
        public static final Action RUN_COMMAND = new Action(ClickEvent.Action.RUN_COMMAND);
        public static final Action SUGGEST_COMMAND = new Action(ClickEvent.Action.SUGGEST_COMMAND);
        public static final Action COPY_TO_CLIPBOARD = new Action(ClickEvent.Action.COPY_TO_CLIPBOARD);
    }
}
