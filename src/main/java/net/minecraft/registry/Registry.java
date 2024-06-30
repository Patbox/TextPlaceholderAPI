package net.minecraft.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public record Registry<T>(T object) {
    public T get(Identifier item) {
        return object;
    }
}
