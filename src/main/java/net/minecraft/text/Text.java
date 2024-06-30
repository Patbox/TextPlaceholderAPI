package net.minecraft.text;

import net.minecraft.registry.DynamicRegistryManager;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Text {
    static MutableText literal(String text) {
        return new MutableText();
    }
    static MutableText translatable(String text, Object[] args) {
        return new MutableText();
    }

    static MutableText nbt(String rawPath, boolean interpret, Optional<Text> text, NbtDataSource dataSource) {
        return new MutableText();
    }

    static MutableText empty() {
        return new MutableText();
    }

    static Text of(String name) {
        return name != null ? literal(name) : Text.empty();
    }

    static Text translatableWithFallback(String key, String fallback, Object[] args) {
        return new MutableText();
    }

    static Text score(String name, String objective) {
        return new MutableText();
    }

    static Text selector(String pattern, Optional<Text> text) {
        return new MutableText();
    }

    static Text keybind(String value) {
        return new MutableText();
    }

    String getString();

    TextContent getContent();

    List<Text> getSiblings();

    Style getStyle();

    MutableText copyContentOnly();

    MutableText copy();


    class Serialization {

        public static Text fromLenientJson(String s, DynamicRegistryManager empty) {
            return null;
        }
    }
}
