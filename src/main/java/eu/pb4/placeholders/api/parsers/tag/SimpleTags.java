package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import java.util.Collection;

import eu.pb4.placeholders.impl.textparser.BuiltinTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public final class SimpleTags {
    public static TextTag color(String name, Collection<String> aliases, ChatFormatting formatting) {
        return TextTag.enclosing(
                name,
                aliases,
                "color",
                true,
                (nodes, arg, parser) -> new FormattingNode(nodes, formatting)
        );
    }

    public static TextTag color(String name, Collection<String> aliases, int rgb) {
        return TextTag.enclosing(
                name,
                aliases,
                "color",
                true,
                (nodes, arg, parser) -> new ColorNode(nodes, TextColor.fromRgb(rgb))
        );
    }

    /**
     * Defines a text name of a color for the builtin {@code <color>} tag.
     */
    public static void defineNamedColor(String name, TextColor color) {
        BuiltinTags.COLOR_ALIASES.put(name, color);
    }
}
