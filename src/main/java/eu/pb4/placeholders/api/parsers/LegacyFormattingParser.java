package eu.pb4.placeholders.api.parsers;

import com.mojang.brigadier.StringReader;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

/**
 * Parser that can read legacy (and legacy like) format and convert it into TextNodes
 */
public class LegacyFormattingParser implements NodeParser {
    public static NodeParser COLORS = new LegacyFormattingParser(true, Arrays.stream(ChatFormatting.values()).filter(x -> TextColor.fromLegacyFormat(x) != null).toArray(ChatFormatting[]::new));
    public static NodeParser BASE_COLORS = new LegacyFormattingParser(false, Arrays.stream(ChatFormatting.values()).filter(x -> TextColor.fromLegacyFormat(x) != null).toArray(ChatFormatting[]::new));
    public static NodeParser ALL = new LegacyFormattingParser(true, ChatFormatting.values());
    private final Char2ObjectOpenHashMap<ChatFormatting> map = new Char2ObjectOpenHashMap<>();
    private final boolean allowRgb;

    public LegacyFormattingParser(boolean allowRgb, ChatFormatting... allowedFormatting) {
        this.allowRgb = allowRgb;
        for (var formatting : allowedFormatting) {
            this.map.put(formatting.toString().charAt(1), formatting);
        }
    }

    public boolean allowRGB() {
        return allowRgb;
    }

    public Collection<ChatFormatting> formatting() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        return parseNodes(input, new ArrayList<>());
    }

    public TextNode[] parseNodes(TextNode input, List<TextNode> nextNodes) {
        if (input instanceof LiteralNode literalNode) {
            return parseLiteral(literalNode, nextNodes);
        } else if (input instanceof TranslatedNode translatedNode) {
            return new TextNode[] { translatedNode.transform(this) };
        } else if (input instanceof ParentTextNode parentTextNode) {
            return parseParents(parentTextNode);
        } else {
            return new TextNode[] { input };
        }
    }

    private TextNode[] parseParents(ParentTextNode parentTextNode) {
        var list = new ArrayList<TextNode>();

        if (parentTextNode.getChildren().length > 0) {
            var nodes = new ArrayList<>(List.of(parentTextNode.getChildren()));
            while (!nodes.isEmpty()) {
                list.add(TextNode.asSingle(parseNodes(nodes.remove(0), nodes)));
            }
        }

        return new TextNode[] { parentTextNode.copyWith(list.toArray(TextNode[]::new), this) };
    }

    private TextNode[] parseLiteral(LiteralNode literalNode, List<TextNode> nexts) {
        var builder = new StringBuilder();
        var reader = new StringReader(literalNode.value());

        while (reader.canRead(2)) {
            var i = reader.read();

            if (i == '\\') {
                i = reader.read();

                builder.append('\\');
                builder.append(i);

            } else if (i == '&') {
                i = reader.read();

                if (allowRgb && i == '#' && reader.canRead(6)) {
                    var start = reader.getCursor();
                    try {
                        StringBuilder builder1 = new StringBuilder();

                        for (int z = 0; z < 6; z++) {
                            builder1.append(reader.read());
                        }

                        var rgb = Integer.parseInt(builder1.toString(), 16);

                        var list = new ArrayList<TextNode>();
                        list.addAll(nexts);
                        nexts.clear();

                        var base = TextNode.asSingle(parseLiteral(new LiteralNode(reader.getRemaining()), list));
                        list.add(0, base);

                        return new TextNode[] {
                                new LiteralNode(builder.toString()),
                                new ColorNode(list.toArray(TextNode[]::new), TextColor.fromRgb(rgb))
                        };
                    } catch (Throwable e) {
                        //noop
                    }

                    reader.setCursor(start);
                }

                var x = this.map.get(i);

                if (x != null) {
                    var list = new ArrayList<TextNode>();
                    list.addAll(nexts);
                    nexts.clear();

                    var base = TextNode.asSingle(parseLiteral(new LiteralNode(reader.getRemaining()), list));

                    list.add(0, base);

                    return new TextNode[] {
                            new LiteralNode(builder.toString()),
                            new FormattingNode(list.toArray(TextNode[]::new), x)
                    };
                } else {
                    builder.append('&');
                }
            }
            builder.append(i);
        }

        return new TextNode[] { literalNode };
    }
}
