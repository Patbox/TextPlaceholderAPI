package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Collection;

public final class SimpleTags {
    public static TextTag color(String name, Collection<String> aliases, Formatting formatting) {
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
