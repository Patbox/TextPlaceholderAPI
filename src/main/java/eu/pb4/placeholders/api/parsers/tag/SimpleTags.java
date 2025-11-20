package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import java.util.Collection;
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
}
