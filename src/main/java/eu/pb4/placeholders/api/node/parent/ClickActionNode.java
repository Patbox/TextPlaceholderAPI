package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;

import java.net.URI;

public final class ClickActionNode extends SimpleStylingNode {
    private final ClickEvent.Action action;
    private final TextNode value;

    public ClickActionNode(TextNode[] children, ClickEvent.Action action, TextNode value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    public ClickEvent.Action clickEventAction() {
        return action;
    }

    public TextNode value() {
        return value;
    }

    @Override
    protected Style style(ParserContext context) {
        return switch (this.action) {
            case OPEN_URL -> {
                try {
                    yield  Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(this.value.toText(context).getString())));
                } catch (Exception ignored) {
                    yield  Style.EMPTY;
                }
            }
            case CHANGE_PAGE -> {
                try {
                    yield Style.EMPTY.withClickEvent(new ClickEvent.ChangePage(Integer.parseInt(this.value.toText(context).getString())));
                } catch (Exception ignored) {
                    yield Style.EMPTY;
                }
            }
            case OPEN_FILE -> Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(this.value.toText(context).getString()));
            case RUN_COMMAND -> Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(this.value.toText(context).getString()));
            case SUGGEST_COMMAND -> Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand(this.value.toText(context).getString()));
            case COPY_TO_CLIPBOARD -> Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(this.value.toText(context).getString()));
        };
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
                "action=" + action.asString() +
                ", value=" + value +
                '}';
    }


    @Deprecated(forRemoval = true)
    public ClickActionNode(TextNode[] children, Action action, TextNode value) {
        super(children);
        this.action = action.vanillaType();
        this.value = value;
    }

    @Deprecated(forRemoval = true)
    public Action action() {
        return switch (this.action) {
            case OPEN_URL -> Action.OPEN_URL;
            case OPEN_FILE -> Action.OPEN_FILE;
            case CHANGE_PAGE -> Action.CHANGE_PAGE;
            case RUN_COMMAND -> Action.RUN_COMMAND;
            case SUGGEST_COMMAND -> Action.SUGGEST_COMMAND;
            case COPY_TO_CLIPBOARD -> Action.COPY_TO_CLIPBOARD;
        };
    }
    @Deprecated(forRemoval = true)
    public record Action(ClickEvent.Action vanillaType) {
        public static final Action OPEN_URL = new Action(ClickEvent.Action.OPEN_URL);
        public static final Action CHANGE_PAGE = new Action(ClickEvent.Action.CHANGE_PAGE);
        public static final Action OPEN_FILE = new Action(ClickEvent.Action.OPEN_FILE);
        public static final Action RUN_COMMAND = new Action(ClickEvent.Action.RUN_COMMAND);
        public static final Action SUGGEST_COMMAND = new Action(ClickEvent.Action.SUGGEST_COMMAND);
        public static final Action COPY_TO_CLIPBOARD = new Action(ClickEvent.Action.COPY_TO_CLIPBOARD);
    }
}
