package net.minecraft.util;

public record RegistryKey(Identifier value) {
    public Identifier getValue() {
        return value;
    }
}
