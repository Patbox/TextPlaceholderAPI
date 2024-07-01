package net.minecraft.text;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholderstandalone.TextCodecs;
import net.minecraft.registry.DynamicRegistryManager;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Text {
    static MutableText literal(String text) {
        return new MutableText(new PlainTextContent.Literal(text));
    }
    static MutableText translatable(String text, Object... args) {
        return translatableWithFallback(text, null, args);
    }

    static MutableText nbt(String rawPath, boolean interpret, Optional<Text> text, NbtDataSource dataSource) {
        return new MutableText(new NbtTextContent(rawPath, interpret, text, dataSource));
    }

    static MutableText empty() {
        return new MutableText(PlainTextContent.EMPTY);
    }

    static Text of(String name) {
        return name != null ? literal(name) : Text.empty();
    }

    static MutableText translatableWithFallback(String key, String fallback, Object... args) {
        return new MutableText(new TranslatableTextContent(key, fallback, args));
    }

    static MutableText score(String name, String objective) {
        return new MutableText(new ScoreTextContent(name, objective));
    }

    static MutableText selector(String pattern, Optional<Text> text) {
        return new MutableText(new SelectorTextContent(pattern, text));
    }

    static MutableText keybind(String value) {
        return new MutableText(new KeybindTextContent(value));
    }

    String getString();

    TextContent getContent();

    List<Text> getSiblings();

    Style getStyle();

    MutableText copyContentOnly();

    MutableText copy();


    class Serialization {
        public static Text fromLenientJson(String s, DynamicRegistryManager empty) {
            return TextCodecs.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(s)).result().map(Pair::getFirst).orElseThrow();
        }
    }
}
