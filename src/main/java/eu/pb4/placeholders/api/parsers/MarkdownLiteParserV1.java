package eu.pb4.placeholders.api.parsers;

import com.mojang.brigadier.StringReader;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.impl.textparser.TextParserImpl;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Basic markdown parser supporting only single-line vanilla text compatible formatting.
 */
public final class MarkdownLiteParserV1 implements NodeParser {
    public static NodeParser ALL = new MarkdownLiteParserV1(MarkdownFormat.values());
    private final EnumSet<MarkdownFormat> allowedFormatting = EnumSet.noneOf(MarkdownFormat.class);
    private final Function<TextNode[], TextNode> spoilerFormatting;
    private final Function<TextNode[], TextNode> backtickFormatting;
    private final BiFunction<TextNode[], TextNode, TextNode> urlFormatting;

    public MarkdownLiteParserV1(MarkdownFormat... formatting) {
        this(MarkdownLiteParserV1::defaultSpoilerFormatting, MarkdownLiteParserV1::defaultQuoteFormatting, formatting);
    }

    public MarkdownLiteParserV1(
            Function<TextNode[], TextNode> spoilerFormatting,
            Function<TextNode[], TextNode> quoteFormatting,
            MarkdownFormat... formatting
    ) {
        this(spoilerFormatting, quoteFormatting, MarkdownLiteParserV1::defaultUrlFormatting, formatting);
    }

    public MarkdownLiteParserV1(
            Function<TextNode[], TextNode> spoilerFormatting,
            Function<TextNode[], TextNode> quoteFormatting,
            BiFunction<TextNode[], TextNode, TextNode> urlFormatting,
            MarkdownFormat... formatting
    ) {
        for (var form : formatting) {
            this.allowedFormatting.add(form);
        }
        this.spoilerFormatting = spoilerFormatting;
        this.backtickFormatting = quoteFormatting;
        this.urlFormatting = urlFormatting;
    }

    public static TextNode defaultSpoilerFormatting(TextNode[] textNodes) {
        return new HoverNode<>(TextNode.array(
                new FormattingNode(
                        TextNode.array(TextNode.of("["), TranslatedNode.of("options.hidden"), TextNode.of("]")),
                        Formatting.GRAY, Formatting.ITALIC
                )
        ), HoverNode.Action.TEXT, TextNode.asSingle(textNodes));
    }

    public static TextNode defaultQuoteFormatting(TextNode[] textNodes) {
        return new FormattingNode(textNodes, Formatting.GRAY, Formatting.ITALIC);
    }

    public static TextNode defaultUrlFormatting(TextNode[] textNodes, TextNode url) {
        return new ClickActionNode(TextNode.array(new FormattingNode(textNodes, Formatting.BLUE, Formatting.UNDERLINE)), ClickEvent.Action.OPEN_URL, url);
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        if (input instanceof LiteralNode literalNode) {
            var list = new ArrayList<SubNode<?>>();
            parseLiteral(literalNode, list::add);
            return parseSubNodes(list.listIterator(), null, -1);
        } else if (input instanceof TranslatedNode translatedNode) {
            var list = new ArrayList<>();
            for (var arg : translatedNode.args()) {
                if (arg instanceof TextNode textNode) {
                    list.add(TextNode.asSingle(this.parseNodes(textNode)));
                } else {
                    list.add(arg);
                }
            }
            return new TextNode[]{TranslatedNode.ofFallback(translatedNode.key(), translatedNode.fallback(), list.toArray())};
        } else if (input instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<SubNode<?>>();
            for (var children : parentTextNode.getChildren()) {
                if (children instanceof LiteralNode literalNode) {
                    parseLiteral(literalNode, list::add);
                } else {
                    list.add(new SubNode<>(SubNodeType.TEXT_NODE, TextNode.asSingle(parseNodes(children))));
                }
            }
            return new TextNode[]{parentTextNode.copyWith(parseSubNodes(list.listIterator(), null, -1), this)};
        } else {
            return new TextNode[]{input};
        }
    }

    private void parseLiteral(LiteralNode literalNode, Consumer<SubNode<?>> consumer) {
        var reader = new StringReader(literalNode.value());
        var builder = new StringBuilder();

        while (reader.canRead()) {
            var i = reader.read();
            if (i == '\\' && reader.canRead()) {
                var next = reader.read();
                builder.append(i);
                builder.append(next);
                continue;
            }
            SubNodeType<String> type = null;

            if (reader.canRead()) {
                var i2 = reader.read();

                if (i2 == i) {
                    type = switch (i) {
                        case '~' -> SubNodeType.DOUBLE_WAVY_LINE;
                        case '|' -> SubNodeType.SPOILER_LINE;
                        default -> null;
                    };
                }

                if (type == null) {
                    reader.setCursor(reader.getCursor() - 1);
                }
            }

            if (type == null) {
                type = switch (i) {
                    case '`' -> SubNodeType.BACK_TICK;
                    case '*' -> SubNodeType.STAR;
                    case '_' -> {
                        if (reader.getCursor() == 1 || !reader.canRead()
                                || Character.isWhitespace(reader.peek(-2))
                                || Character.isWhitespace(reader.peek())) {
                            yield SubNodeType.FLOOR;
                        }
                        yield null;
                    }
                    case '(' -> SubNodeType.BRACKET_OPEN;
                    case ')' -> SubNodeType.BRACKET_CLOSE;
                    case '[' -> SubNodeType.SQR_BRACKET_OPEN;
                    case ']' -> SubNodeType.SQR_BRACKET_CLOSE;
                    default -> null;
                };
            }

            if (type != null) {
                if (!builder.isEmpty()) {
                    consumer.accept(new SubNode<>(SubNodeType.STRING, builder.toString()));
                    builder = new StringBuilder();
                }
                consumer.accept(new SubNode<>(type, type.selfValue));
            } else {
                builder.append(i);
            }
        }


        if (!builder.isEmpty()) {
            consumer.accept(new SubNode<>(SubNodeType.STRING, builder.toString()));
        }
    }

    private TextNode[] parseSubNodes(ListIterator<SubNode<?>> nodes, @Nullable SubNodeType endAt, int count) {
        var out = new ArrayList<TextNode>();
        int startIndex = nodes.nextIndex();
        var builder = new StringBuilder();
        while (nodes.hasNext()) {
            var next = nodes.next();

            if (next.type == endAt) {
                int foundCount = 1;

                if (foundCount == count) {
                    if (!builder.isEmpty()) {
                        out.add(new LiteralNode(builder.toString()));
                    }
                    return out.toArray(TextParserImpl.CASTER);
                }

                var xStart = nodes.nextIndex();

                while (nodes.hasNext()) {
                    if (nodes.next().type == endAt) {
                        if ((++foundCount) == count) {
                            if (nodes.hasNext()) {
                                var prev = nodes.next();
                                nodes.previous();
                                if (prev.type == SubNodeType.STRING && !((String) prev.value).startsWith(" ")) {
                                    break;
                                }
                            }

                            if (!builder.isEmpty()) {
                                out.add(new LiteralNode(builder.toString()));
                            }
                            return out.toArray(TextParserImpl.CASTER);
                        }
                    } else {
                        break;
                    }
                }

                while (xStart != nodes.nextIndex()) {
                    nodes.previous();
                }
            }

            if (next.type == SubNodeType.TEXT_NODE) {
                if (!builder.isEmpty()) {
                    out.add(new LiteralNode(builder.toString()));
                    builder = new StringBuilder();
                }
                out.add((TextNode) next.value);
                continue;
            } else if (next.type == SubNodeType.STRING) {
                builder.append((String) next.value);
                continue;
            } else if (next.type == SubNodeType.BACK_TICK && this.allowedFormatting.contains(MarkdownFormat.QUOTE)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (!builder.isEmpty()) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(this.backtickFormatting.apply(value));
                    continue;
                }
            } else if (next.type == SubNodeType.SPOILER_LINE && this.allowedFormatting.contains(MarkdownFormat.SPOILER)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (!builder.isEmpty()) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(this.spoilerFormatting.apply(value));
                    continue;
                }
            } else if (next.type == SubNodeType.DOUBLE_WAVY_LINE && this.allowedFormatting.contains(MarkdownFormat.STRIKETHROUGH)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (!builder.isEmpty()) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(new FormattingNode(value, Formatting.STRIKETHROUGH));
                    continue;
                }
            } else if (next.type == SubNodeType.STAR || next.type == SubNodeType.FLOOR) {
                boolean two = false;
                if (nodes.hasNext()) {
                    if ((next.type == SubNodeType.STAR && this.allowedFormatting.contains(MarkdownFormat.BOLD))
                            || (next.type == SubNodeType.FLOOR && this.allowedFormatting.contains(MarkdownFormat.UNDERLINE))
                    ) {
                        var nexter = nodes.next();
                        if (nexter.type == next.type) {
                            two = true;
                            var i = nodes.nextIndex();
                            var value = parseSubNodes(nodes, next.type, 2);

                            if (value != null) {
                                if (!builder.isEmpty()) {
                                    out.add(new LiteralNode(builder.toString()));
                                    builder = new StringBuilder();
                                }
                                out.add(new FormattingNode(value, next.type == SubNodeType.STAR ? Formatting.BOLD : Formatting.UNDERLINE));
                                continue;
                            }
                        }
                        nodes.previous();
                    }
                }

                if (!two && this.allowedFormatting.contains(MarkdownFormat.ITALIC)) {
                    boolean startingOrSpace;
                    if (nodes.hasPrevious()) {
                        var prev = nodes.previous();
                        startingOrSpace = prev.type != SubNodeType.STRING || ((String) prev.value).endsWith(" ");
                        nodes.next();
                    } else {
                        startingOrSpace = true;
                    }

                    if (startingOrSpace) {
                        var value = parseSubNodes(nodes, next.type, 1);

                        if (value != null) {
                            if (!builder.isEmpty()) {
                                out.add(new LiteralNode(builder.toString()));
                                builder = new StringBuilder();
                            }
                            out.add(new FormattingNode(value, Formatting.ITALIC));
                            continue;
                        }
                    }
                }
            } else if (next.type == SubNodeType.SQR_BRACKET_OPEN && this.allowedFormatting.contains(MarkdownFormat.URL) && nodes.hasNext()) {
                var start = nodes.nextIndex();
                var value = parseSubNodes(nodes, SubNodeType.SQR_BRACKET_CLOSE, 1);

                if (value != null) {
                    if (nodes.hasNext()) {
                        var check = nodes.next().type == SubNodeType.BRACKET_OPEN;

                        if (check) {
                            var url = parseSubNodes(nodes, SubNodeType.BRACKET_CLOSE, 1);
                            if (url != null) {
                                if (!builder.isEmpty()) {
                                    out.add(new LiteralNode(builder.toString()));
                                    builder = new StringBuilder();
                                }
                                out.add(this.urlFormatting.apply(value, TextNode.asSingle(url)));
                                continue;
                            }
                        }
                    }
                }
                while (start != nodes.nextIndex()) {
                    nodes.previous();
                }
            }

            builder.append((String) next.value);
        }

        if (endAt == null) {
            if (!builder.isEmpty()) {
                out.add(new LiteralNode(builder.toString()));
            }
            return out.toArray(TextParserImpl.CASTER);
        } else {
            while (startIndex != nodes.nextIndex()) {
                nodes.previous();
            }

            return null;
        }
    }

    public enum MarkdownFormat {
        BOLD,
        ITALIC,
        UNDERLINE,
        STRIKETHROUGH,
        QUOTE,
        SPOILER,
        URL
    }

    private record SubNodeType<T>(T selfValue) {
        public static final SubNodeType<TextNode> TEXT_NODE = new SubNodeType<>(null);
        public static final SubNodeType<String> STRING = new SubNodeType<>(null);

        public static final SubNodeType<String> STAR = new SubNodeType<>("*");
        public static final SubNodeType<String> FLOOR = new SubNodeType<>("_");
        public static final SubNodeType<String> DOUBLE_WAVY_LINE = new SubNodeType<>("~~");
        public static final SubNodeType<String> BACK_TICK = new SubNodeType<>("`");
        public static final SubNodeType<String> SPOILER_LINE = new SubNodeType<>("||");
        public static final SubNodeType<String> BRACKET_OPEN = new SubNodeType<>("(");
        public static final SubNodeType<String> BRACKET_CLOSE = new SubNodeType<>(")");
        public static final SubNodeType<String> SQR_BRACKET_OPEN = new SubNodeType<>("[");
        public static final SubNodeType<String> SQR_BRACKET_CLOSE = new SubNodeType<>("]");
    }

    private record SubNode<T>(SubNodeType<T> type, T value) {
    }

}
