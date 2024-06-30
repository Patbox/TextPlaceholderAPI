package net.minecraft.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Optional;

public class EntityType<T> {
    public static final EntityType<?> PIG = new EntityType<>();
    public static Optional<EntityType<?>> get(String s) {
        return Optional.empty();
    }
}
