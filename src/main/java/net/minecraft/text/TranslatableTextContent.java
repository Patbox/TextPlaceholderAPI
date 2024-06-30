package net.minecraft.text;

import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Nullable;

public record TranslatableTextContent(String key, @Nullable String fallback, Object[] args) implements TextContent {
    public Object[] getArgs() {
        return args;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public String getFallback() {
        return this.fallback;
    }
}
