package net.minecraft.text;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public record HoverEvent<T>(Action<T> action, T object) {

    public Action<?> getAction() {
        return this.action;
    }

    public <A> A getValue(Action<A> action) {
        //noinspection unchecked
        return this.action == action ? (A) this.object : null;
    }

    public record Action<T>() {
        public static final Action<EntityContent> SHOW_ENTITY = new Action<>();
        public static final Action<ItemStackContent> SHOW_ITEM = new Action<>();
        public static final Action<Text> SHOW_TEXT = new Action<>();

        public static final Codec<Action<?>> CODEC = Codec.unit(SHOW_TEXT);
    }

    public static class EntityContent {
        public EntityContent(EntityType<?> entityType, UUID uuid, Text text) {
        }
    }

    public static class ItemStackContent {
        public ItemStackContent(ItemStack stack) {
        }
    }
}
