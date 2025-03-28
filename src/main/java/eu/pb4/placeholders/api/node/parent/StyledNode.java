package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public final class StyledNode extends SimpleStylingNode {
    private final Style style;

    private final HoverData<?> hoverValue;
    private final TextNode clickValue;
    private final TextNode insertion;

    public StyledNode(TextNode[] children, Style style, @Nullable HoverData<?> hoverValue, @Nullable TextNode clickValue, @Nullable TextNode insertion) {
        super(children);
        this.style = style;
        this.hoverValue = hoverValue;
        this.clickValue = clickValue;
        this.insertion = insertion;
    }

    public StyledNode(TextNode[] children, Style style, @Nullable ParentNode hoverValue, @Nullable TextNode clickValue, @Nullable TextNode insertion) {
        this(children, style, hoverValue != null ? new HoverData<>(HoverNode.Action.TEXT_NODE, hoverValue) : null, clickValue, insertion);
    }

    public Style style(ParserContext context) {
        var style = this.style;

        if (this.hoverValue != null && style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            style = style.withHoverEvent(this.hoverValue.toVanilla(context));
        }

        if (this.clickValue != null && style.getClickEvent() != null) {
            String node = this.clickValue.toText(context, true).getString();
            switch (style.getClickEvent().getAction()) {
                case OPEN_URL -> {
                    try {
                        style = style.withClickEvent(new ClickEvent.OpenUrl(URI.create(node)));
                    } catch (Exception ignored) { }
                } case CHANGE_PAGE -> {
                    try {
                        style = style.withClickEvent(new ClickEvent.ChangePage(Integer.parseInt(node)));
                    } catch (Exception ignored) { }
                }
                case OPEN_FILE -> style = style.withClickEvent(new ClickEvent.OpenFile(node));
				case RUN_COMMAND -> style = style.withClickEvent(new ClickEvent.RunCommand(node));
				case SUGGEST_COMMAND -> style = style.withClickEvent(new ClickEvent.SuggestCommand(node));
				case COPY_TO_CLIPBOARD -> style = style.withClickEvent(new ClickEvent.CopyToClipboard(node));
            }
        }

        if (this.insertion != null) {
            style = style.withInsertion(this.insertion.toText(context, true).getString());
        }
        return style;
    }


    public Style rawStyle() {
        return this.style;
    }

    @Deprecated(forRemoval = true)
    @Nullable
    public ParentNode hoverValue() {
        return hoverValue != null && hoverValue.data instanceof TextNode textNode ? new ParentNode(textNode) : null;
    }

    @Nullable
    public HoverData<?> hover() {
        return hoverValue;
    }

    @Nullable
    public TextNode clickValue() {
        return clickValue;
    }

    @Nullable
    public TextNode insertion() {
        return insertion;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new StyledNode(children, this.style, this.hoverValue, this.clickValue, this.insertion);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new StyledNode(children, this.style,
                this.hoverValue != null ? this.hoverValue.parse(parser) : null,
                this.clickValue != null ? TextNode.asSingle(parser.parseNodes(this.clickValue)) : null,
                this.insertion != null ? TextNode.asSingle(parser.parseNodes(this.insertion)) : null);
    }

    @Override
    public boolean isDynamicNoChildren() {
        return (this.clickValue != null && this.clickValue.isDynamic()) || (this.hoverValue != null && this.hoverValue.isDynamic())
                || (this.insertion != null && this.insertion.isDynamic());
    }

    @Override
    public String toString() {
        return "StyledNode{" +
                "style=" + style +
                ", hoverValue=" + hoverValue +
                ", clickValue=" + clickValue +
                ", insertion=" + insertion +
                '}';
    }

    public record HoverData<T>(HoverNode.Action<T, ?> action, T data) {
        public HoverData<T> parse(NodeParser parser) {
            if (action == HoverNode.Action.TEXT_NODE) {
                //noinspection unchecked
                return new HoverData<>(action, (T) parser.parseNode((TextNode) this.data));
            } else if (action == HoverNode.Action.ENTITY_NODE && ((HoverNode.EntityNodeContent) this.data).name() != null) {
                var data = ((HoverNode.EntityNodeContent) this.data);
                //noinspection unchecked
                return new HoverData<>(action, (T) new HoverNode.EntityNodeContent(data.entityType(), data.uuid(), parser.parseNode(data.name())));
            }

            return this;
        }

        public boolean isDynamic() {
            if (action == HoverNode.Action.TEXT_NODE) {
                //noinspection unchecked
                return ((TextNode) this.data).isDynamic();
            } else if (action == HoverNode.Action.ENTITY_NODE && ((HoverNode.EntityNodeContent) this.data).name() != null) {
                return ((HoverNode.EntityNodeContent) this.data).name().isDynamic();
            }

            return this.action == HoverNode.Action.LAZY_ITEM_STACK;
        }

        @Nullable
        public HoverEvent toVanilla(ParserContext context) {
            return HoverNode.toVanilla(this.action, this.data, context);
        }
    }
}
