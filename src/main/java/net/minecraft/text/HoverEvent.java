package net.minecraft.text;

import com.mojang.serialization.Codec;
import eu.pb4.placeholderstandalone.TextCodecs;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public record HoverEvent<T>(Action<T> action, T object) {

    public Action<?> getAction() {
        return this.action;
    }

    public <A> A getValue(Action<A> action) {
        //noinspection unchecked
        return this.action == action ? (A) this.object : null;
    }

    public record Action<T>(String name) {
        public static final Action<EntityContent> SHOW_ENTITY = new Action<>("show_entity");
        public static final Action<ItemStackContent> SHOW_ITEM = new Action<>("show_item");
        public static final Action<Text> SHOW_TEXT = new Action<>("show_text");

        public static final Codec<Action<?>> CODEC = TextCodecs.HOVER_ACTION_TYPE_CODEC;
    }

    public record EntityContent(EntityType<?> entityType, UUID uuid, @Nullable Text text) {

    }

    public record ItemStackContent(ItemStack stack) {

    }
}
