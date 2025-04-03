package eu.pb4.placeholders.api.node.parent;

import com.mojang.serialization.DynamicOps;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class HoverNode<T, H> extends SimpleStylingNode {
    private final Action<T, H> action;
    private final T value;

    public HoverNode(TextNode[] children, Action<T, H> action, T value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    public Action<T, H> action() {
        return this.action;
    }

    public T value() {
        return this.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Style style(ParserContext context) {
        return Style.EMPTY.withHoverEvent(toVanilla(this.action, this.value, context));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new HoverNode<>(children, this.action, this.value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        if (this.value == null) {
            return this.copyWith(children);
        } else if (this.action == Action.TEXT_NODE) {
            return new HoverNode<>(children,
                                   Action.TEXT_NODE,
                                   parser.parseNode((TextNode) this.value)
            );
        } else if (this.action == Action.ENTITY_NODE &&
                  ((EntityNodeContent) this.value).name != null) {
            var val = ((EntityNodeContent) this.value);
            return new HoverNode<>(children,
                                   Action.ENTITY_NODE,
                                   new EntityNodeContent(val.entityType, val.uuid, parser.parseNode(val.name))
            );
        } else if (this.action == Action.LAZY_ITEM_STACK &&
                  ((LazyItemStackNodeContent<T>) this.value).identifier != null) {
            var val = ((LazyItemStackNodeContent<T>) this.value);
            return new HoverNode<>(children,
                                   Action.LAZY_ITEM_STACK,
                                   new LazyItemStackNodeContent<>(val.identifier, val.count, val.ops, val.componentMap)
            );
        } else if (this.action == Action.VANILLA_ITEM_STACK &&
                  ((HoverEvent.ShowItem) this.value).item() != null) {
            var val = ((HoverEvent.ShowItem) this.value).item();
            return new HoverNode<>(children,
                                   Action.VANILLA_ITEM_STACK,
                                   new HoverEvent.ShowItem(val)
            );
        } else if (this.action == Action.VANILLA_ENTITY &&
                  ((HoverEvent.ShowEntity) this.value).entity() != null) {
            var val = ((HoverEvent.ShowEntity) this.value).entity();
            return new HoverNode<>(children,
                                   Action.VANILLA_ENTITY,
                                   new HoverEvent.ShowEntity(val)
            );
        } return this.copyWith(children);
    }


    @Nullable
    public static <T> HoverEvent toVanilla(HoverNode.Action<T, ?> action, T value, ParserContext context) {
        if (action == Action.TEXT_NODE) {
            return new HoverEvent.ShowText(((TextNode) value).toText(context.copyWithoutNodeContext(), true));
        } else if (action == Action.ENTITY_NODE) {
            return new HoverEvent.ShowEntity(((EntityNodeContent) value).toVanilla(context.copyWithoutNodeContext()));
        } else if (action == Action.LAZY_ITEM_STACK) {
            RegistryWrapper.WrapperLookup wrapper;
            if (context.contains(ParserContext.Key.WRAPPER_LOOKUP)) {
                wrapper = context.getOrThrow(ParserContext.Key.WRAPPER_LOOKUP);
            } else if (context.contains(PlaceholderContext.KEY)) {
                wrapper = context.getOrThrow(PlaceholderContext.KEY).server().getGameInstance().getRegistryManager();
            } else {
                return null;
            }

            return new HoverEvent.ShowItem(((LazyItemStackNodeContent<T>) value).toVanilla(wrapper));
        } else if (action == Action.VANILLA_ITEM_STACK) {
            return new HoverEvent.ShowItem(((HoverEvent.ShowItem) value).item());
        } else if (action == Action.VANILLA_ENTITY) {
            return new HoverEvent.ShowEntity(((HoverEvent.ShowEntity) value).entity());
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "HoverNode{" +
                "value=" + value +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    @Override
    public boolean isDynamicNoChildren() {
        return (this.action == Action.TEXT_NODE && ((TextNode) this.value).isDynamic()) || (this.action == Action.ENTITY_NODE && ((EntityNodeContent) this.value).name.isDynamic()) || this.action == Action.LAZY_ITEM_STACK;
    }

    public record Action<T, H>(HoverEvent.Action vanillaType) {
        public static final Action<TextNode, HoverEvent.ShowText> TEXT_NODE = new Action<>(HoverEvent.Action.SHOW_TEXT);
        public static final Action<LazyItemStackNodeContent<?>, HoverEvent.ShowItem> LAZY_ITEM_STACK = new Action<>(HoverEvent.Action.SHOW_ITEM);
        public static final Action<EntityNodeContent, HoverEvent.ShowEntity> ENTITY_NODE = new Action<>(HoverEvent.Action.SHOW_ENTITY);

        public static final Action<HoverEvent.ShowItem, HoverEvent.ShowItem> VANILLA_ITEM_STACK = new Action<>(HoverEvent.Action.SHOW_ITEM);
        public static final Action<HoverEvent.ShowEntity, HoverEvent.ShowEntity> VANILLA_ENTITY = new Action<>(HoverEvent.Action.SHOW_ENTITY);

        @Override
        public String toString()
        {
            return "HoverNode$Action{vanillaType={"+vanillaType.name()+"}}";
        }
    }

    public record EntityNodeContent(EntityType<?>entityType, UUID uuid, @Nullable TextNode name) {
        public HoverEvent.EntityContent toVanilla(ParserContext context) {
            return new HoverEvent.EntityContent(this.entityType, this.uuid, Optional.ofNullable(this.name != null ? this.name.toText(context, true) : null));
        }

        @Override
        public String toString() {
            return "HoverNode$EntityNodeContent{id="+
                    EntityType.getId(entityType).toString()
                    + ",uuid=["+
                    uuid.toString()
                    + "],name={" +
                    (name != null ? name.toText().getLiteralString() : "<NULL>")
                    + "}}";
        }
    }

    public record LazyItemStackNodeContent<T>(Identifier identifier, int count, DynamicOps<T> ops, T componentMap) {
        public ItemStack toVanilla(RegistryWrapper.WrapperLookup lookup) {
            var stack = new ItemStack(lookup.getOrThrow(RegistryKeys.ITEM).getOrThrow(RegistryKey.of(RegistryKeys.ITEM, identifier)));
            stack.setCount(count);
            if (componentMap != null) {
                stack.applyChanges(ComponentChanges.CODEC.decode(lookup.getOps(ops), componentMap).getOrThrow().getFirst());
            }
            return stack;
        }

        @Override
        public String toString() {
            return "HoverNode$LazyItemStackNodeContent{id="
                    +identifier.toString()
                    + ",count="+
                    count
                    + ",ops=["+
                    ops.toString()
                    + "],components={"+
                    componentMap.toString()
                    + "}}";
        }
    }
}
