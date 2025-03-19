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

    private final ParentNode hoverValue;
    private final TextNode clickValue;
    private final TextNode insertion;

    public StyledNode(TextNode[] children, Style style, @Nullable ParentNode hoverValue, @Nullable TextNode clickValue, @Nullable TextNode insertion) {
        super(children);
        this.style = style;
        this.hoverValue = hoverValue;
        this.clickValue = clickValue;
        this.insertion = insertion;
    }

    public Style style(ParserContext context) {
        var style = this.style;

        if (this.hoverValue != null && style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            style = style.withHoverEvent(new HoverEvent.ShowText(this.hoverValue.toText(context, true)));
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

    @Nullable
    public ParentNode hoverValue() {
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
                this.hoverValue != null ? new ParentNode(parser.parseNodes(this.hoverValue)) : null,
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
}
