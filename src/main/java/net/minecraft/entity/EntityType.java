package net.minecraft.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record EntityType<T>(Identifier id) {
    public static final EntityType<?> PIG = new EntityType<>(Identifier.of("pig"));
    public static Optional<EntityType<?>> get(String s) {
        return Optional.empty();
    }
}
