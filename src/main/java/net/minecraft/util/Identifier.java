package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public record Identifier(String namespace, String path) {
    public static Identifier of(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    public static Identifier of(String id) {
        var split = id.split(":", 2);
        return new Identifier(split.length == 2 ? split[0] : "minecraft", split.length == 2 ? split[1] : split[0]);
    }

    @Nullable
    public static Identifier tryParse(String s) {
        try {
            return of(s);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return namespace + ':' + path;
    }

    public String getPath() {
        return this.path;
    }
}
