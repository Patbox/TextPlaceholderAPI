package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.entity.EntityType;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public final class HoverNode<T, H> extends ParentNode {
    private final Action<T, H> action;
    private final T value;

    public HoverNode(TextNode[] children, Action<T, H> action, T value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        if (this.action == Action.TEXT) {
            return out.setStyle(out.getStyle().withHoverEvent(new HoverEvent((HoverEvent.Action<Object>) this.action.vanillaType(), ((ParentTextNode) this.value).toText(context, true))));
        } else if (this.action == Action.ENTITY) {
            return out.setStyle(out.getStyle().withHoverEvent(new HoverEvent((HoverEvent.Action<Object>) this.action.vanillaType(), ((EntityNodeContent) this.value).toVanilla(context))));
        } else {
            return out.setStyle(out.getStyle().withHoverEvent(new HoverEvent((HoverEvent.Action<Object>) this.action.vanillaType(), this.value)));
        }

    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new HoverNode(children, this.action, this.value);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        if (this.action == Action.TEXT) {
            return new HoverNode(children, Action.TEXT, parser.parseNode((TextNode) this.value));
        } else if (this.action == Action.ENTITY && ((EntityNodeContent) this.value).name != null) {
            var val = ((EntityNodeContent) this.value);
            return new HoverNode(children, Action.ENTITY, new EntityNodeContent(val.entityType, val.uuid, parser.parseNode(val.name)));
        }
        return this.copyWith(children);
    }

    public Action<T, H> action() {
        return this.action;
    }

    public T value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "HoverNode{" +
                "value=" + value +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    public record Action<T, H>(HoverEvent.Action<H> vanillaType) {
        public static final Action<EntityNodeContent, HoverEvent.EntityContent> ENTITY = new Action<>(HoverEvent.Action.SHOW_ENTITY);
        public static final Action<HoverEvent.ItemStackContent, HoverEvent.ItemStackContent> ITEM_STACK = new Action<>(HoverEvent.Action.SHOW_ITEM);
        public static final Action<TextNode, Text> TEXT = new Action<>(HoverEvent.Action.SHOW_TEXT);
    }

    public record EntityNodeContent(EntityType<?>entityType, UUID uuid, @Nullable TextNode name) {
        public HoverEvent.EntityContent toVanilla(ParserContext context) {
            return new HoverEvent.EntityContent(this.entityType, this.uuid, this.name != null ? this.name.toText(context, true) : null);
        }
    }
}
