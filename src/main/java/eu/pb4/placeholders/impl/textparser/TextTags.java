package eu.pb4.placeholders.impl.textparser;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

import static eu.pb4.placeholders.impl.textparser.TextParserImpl.*;

@ApiStatus.Internal
public final class TextTags {
    public static void register() {
        {
            Map<String, List<String>> aliases = new HashMap<>();
            aliases.put("gold", List.of("orange"));
            aliases.put("gray", List.of("grey"));
            aliases.put("light_purple", List.of("pink"));
            aliases.put("dark_gray", List.of("dark_grey"));

            for (Formatting formatting : Formatting.values()) {
                if (formatting.isModifier()) {
                    continue;
                }

                TextParserV1.registerDefault(
                        TextParserV1.TextTag.of(
                                formatting.getName(),
                                aliases.containsKey(formatting.getName()) ? aliases.get(formatting.getName()) : List.of(),
                                "color",
                                true,
                                wrap((nodes, arg) -> new FormattingNode(nodes, formatting))
                        )
                );
            }
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "bold",
                            List.of("b"),
                            "formatting",
                            true,
                            bool(BoldNode::new)
                    )
            );

            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "underline",
                            List.of("underlined", "u"),
                            "formatting",
                            true,
                            bool(UnderlinedNode::new)
                    )
            );

            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "strikethrough", List.of("st"),
                            "formatting",
                            true,
                            bool(StrikethroughNode::new)
                    )
            );


            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "obfuscated",
                            List.of("obf", "matrix"),
                            "formatting",
                            true,
                            bool(ObfuscatedNode::new)
                    )
            );

            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "italic",
                            List.of("i", "em"),
                            "formatting",
                            true,
                            bool(ItalicNode::new)
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "color",
                            List.of("colour", "c"),
                            "color",
                            true,
                            wrap((nodes, data) -> new ColorNode(nodes, TextColor.parse(cleanArgument(data)).get().left().orElse(null)))
                    )
            );
        }
        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "font",
                            "other_formatting",
                            false,
                            wrap((nodes, data) -> new FontNode(nodes, Identifier.tryParse(cleanArgument(data))))
                    )
            );
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of(
                    "lang",
                    List.of("translate"),
                    "special",
                    false,
                    (tag, data, input, handlers, endAt) -> {
                        var lines = data.split(":");
                        if (lines.length > 0) {
                            List<TextNode> textList = new ArrayList<>();
                            boolean skipped = false;
                            for (String part : lines) {
                                if (!skipped) {
                                    skipped = true;
                                    continue;
                                }
                                textList.add(new ParentNode(parse(removeEscaping(cleanArgument(part)), handlers)));
                            }

                            var out = TranslatedNode.of(removeEscaping(cleanArgument(lines[0])), textList.toArray(TextParserImpl.CASTER));
                            return new TextParserV1.TagNodeValue(out, 0);
                        }
                        return TextParserV1.TagNodeValue.EMPTY;
                    }));
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of(
                    "lang_fallback",
                    List.of("translatef", "langf", "translate_fallback"),
                    "special",
                    false,
                    (tag, data, input, handlers, endAt) -> {
                        var lines = data.split(":");
                        if (lines.length > 1) {
                            List<TextNode> textList = new ArrayList<>();
                            int skipped = 0;
                            for (String part : lines) {
                                if (skipped < 2) {
                                    skipped++;
                                    continue;
                                }
                                textList.add(new ParentNode(parse(removeEscaping(cleanArgument(part)), handlers)));
                            }

                            var out = TranslatedNode.ofFallback(removeEscaping(cleanArgument(lines[0])), removeEscaping(cleanArgument(lines[1])), textList.toArray(TextParserImpl.CASTER));
                            return new TextParserV1.TagNodeValue(out, 0);
                        }
                        return TextParserV1.TagNodeValue.EMPTY;
                    }));
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of("keybind",
                    List.of("key"),
                    "special",
                    false,
                    (tag, data, input, handlers, endAt) -> {
                        if (!data.isEmpty()) {
                            return new TextParserV1.TagNodeValue(new KeybindNode(cleanArgument(data)), 0);
                        }
                        return TextParserV1.TagNodeValue.EMPTY;
                    }));
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of("click", "click_action", false, (tag, data, input, handlers, endAt) -> {
                String[] lines = data.split(":", 2);
                var out = recursiveParsing(input, handlers, endAt);
                if (lines.length > 1) {
                    for (ClickEvent.Action action : ClickEvent.Action.values()) {
                        if (action.asString().equals(cleanArgument(lines[0]))) {
                            return out.value(new ClickActionNode(out.nodes(), action, new LiteralNode(restoreOriginalEscaping(cleanArgument(lines[1])))));
                        }
                    }
                }
                return out.value(new ParentNode(out.nodes()));
            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "run_command",
                            List.of("run_cmd"),
                            "click_action",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.RUN_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "suggest_command",
                            List.of("cmd"),
                            "click_action",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "open_url",
                            List.of("url"),
                            "click_action",
                            false, (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.OPEN_URL, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "copy_to_clipboard",
                            List.of("copy"),
                            "click_action",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.COPY_TO_CLIPBOARD, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "change_page",
                            List.of("page"),
                            "click_action",
                            true, (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.CHANGE_PAGE, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "hover",
                            "hover_event",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":", 2);
                                var out = recursiveParsing(input, handlers, endAt);

                                try {
                                    if (lines.length > 1) {
                                        HoverEvent.Action<?> action = HoverEvent.Action.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(cleanArgument(lines[0].toLowerCase(Locale.ROOT)))).get().left().orElse(null);
                                        if (action == HoverEvent.Action.SHOW_TEXT) {
                                            return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(lines[1])), handlers))));
                                        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                                            lines = lines[1].split(":", 3);
                                            if (lines.length == 3) {
                                                return out.value(new HoverNode<>(out.nodes(),
                                                        HoverNode.Action.ENTITY,
                                                        new HoverNode.EntityNodeContent(
                                                                EntityType.get(restoreOriginalEscaping(restoreOriginalEscaping(cleanArgument(lines[0])))).orElse(EntityType.PIG),
                                                                UUID.fromString(cleanArgument(lines[1])),
                                                                new ParentNode(parse(restoreOriginalEscaping(restoreOriginalEscaping(cleanArgument(lines[2]))), handlers)))
                                                ));
                                            }
                                        } else if (action == HoverEvent.Action.SHOW_ITEM) {
                                            try {
                                                return out.value(new HoverNode<>(out.nodes(),
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(ItemStack.fromNbt(StringNbtReader.parse(restoreOriginalEscaping(cleanArgument(lines[1])))))
                                                ));
                                            } catch (Throwable e) {
                                                lines = lines[1].split(":", 2);
                                                if (lines.length > 0) {
                                                    var stack = Registries.ITEM.get(Identifier.tryParse(lines[0])).getDefaultStack();

                                                    if (lines.length > 1) {
                                                        stack.setCount(Integer.parseInt(lines[1]));
                                                    }

                                                    if (lines.length > 2) {
                                                        stack.setNbt(StringNbtReader.parse(restoreOriginalEscaping(cleanArgument(lines[2]))));
                                                    }

                                                    return out.value(new HoverNode<>(out.nodes(),
                                                            HoverNode.Action.ITEM_STACK,
                                                            new HoverEvent.ItemStackContent(stack)
                                                    ));
                                                }
                                            }
                                        } else {
                                            return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(data)), handlers))));
                                        }
                                    } else {
                                        return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(data)), handlers))));
                                    }
                                } catch (Exception e) {
                                    // Shut
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "insert",
                            List.of("insertion"),
                            "click_action",
                            false,

                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                return out.value(new InsertNode(out.nodes(), new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "clear_color",
                            List.of("uncolor", "colorless"),
                            "special",
                            false,

                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);

                                return out.value(GeneralUtils.removeColors(new ParentNode(out.nodes())));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "rainbow",
                            List.of("rb"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");
                                float freq = 1;
                                float saturation = 1;
                                float offset = 0;
                                int overriddenLength = -1;

                                if (val.length >= 1) {
                                    try {
                                        freq = Float.parseFloat(val[0]);
                                    } catch (Exception e) {
                                        // No u
                                    }
                                }
                                if (val.length >= 2) {
                                    try {
                                        saturation = Float.parseFloat(val[1]);
                                    } catch (Exception e) {
                                        // Idc
                                    }
                                }
                                if (val.length >= 3) {
                                    try {
                                        offset = Float.parseFloat(val[2]);
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                if (val.length >= 4) {
                                    try {
                                        overriddenLength = Integer.parseInt(val[3]);
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                var out = recursiveParsing(input, handlers, endAt);

                                return out.value(overriddenLength < 0
                                        ? GradientNode.rainbow(saturation, 1, freq, offset, out.nodes())
                                        : GradientNode.rainbow(saturation, 1,  freq, offset, overriddenLength, out.nodes())
                                );
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "gradient",
                            List.of("gr"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");

                                var out = recursiveParsing(input, handlers, endAt);
                                List<TextColor> textColors = new ArrayList<>();
                                for (String string : val) {
                                    TextColor.parse(string).get().ifLeft(textColors::add);
                                }
                                return out.value(GradientNode.colors(textColors, out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "hard_gradient",
                            List.of("hgr"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");

                                var out = recursiveParsing(input, handlers, endAt);

                                var textColors = new ArrayList<TextColor>();

                                for (String string : val) {
                                    TextColor.parse(string).get().ifLeft(textColors::add);
                                }
                                return out.value(GradientNode.colorsHard(textColors, out.nodes()));

                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "clear",
                            "special",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.isEmpty() ? new String[0] : data.split(":");

                                var out = recursiveParsing(input, handlers, endAt);
                                return out.value(new TransformNode(out.nodes(), getTransform(val)));

                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "raw_style",
                            "special",
                            false,
                            (tag, data, input, handlers, endAt) -> new TextParserV1.TagNodeValue(new DirectTextNode(Text.Serialization.fromLenientJson(restoreOriginalEscaping(cleanArgument(data)))), 0)
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "score",
                            "special",
                            false, (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":");
                                if (lines.length == 2) {
                                    return new TextParserV1.TagNodeValue(new ScoreNode(restoreOriginalEscaping(cleanArgument(lines[0])), restoreOriginalEscaping(cleanArgument(lines[1]))), 0);
                                }
                                return TextParserV1.TagNodeValue.EMPTY;
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "selector",
                            "special",
                            false, (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":");
                                if (lines.length == 2) {
                                    return new TextParserV1.TagNodeValue(new SelectorNode(restoreOriginalEscaping(cleanArgument(lines[0])), Optional.of(TextNode.asSingle(recursiveParsing(restoreOriginalEscaping(cleanArgument(lines[1])), handlers, null).nodes()))), 0);
                                } else if (lines.length == 1) {
                                    return new TextParserV1.TagNodeValue(new SelectorNode(restoreOriginalEscaping(cleanArgument(lines[0])), Optional.empty()), 0);
                                }
                                return TextParserV1.TagNodeValue.EMPTY;
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "nbt",
                            "special",
                            false, (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":");

                                if (lines.length < 3) {
                                    return TextParserV1.TagNodeValue.EMPTY;
                                }

                                var cleanLine1 = restoreOriginalEscaping(cleanArgument(lines[1]));

                                var type = switch (lines[0]) {
                                    case "block" -> new BlockNbtDataSource(cleanLine1);
                                    case "entity" -> new EntityNbtDataSource(cleanLine1);
                                    case "storage" -> new StorageNbtDataSource(Identifier.tryParse(cleanLine1));
                                    default -> null;
                                };

                                if (type == null) {
                                    return TextParserV1.TagNodeValue.EMPTY;
                                }

                                Optional<TextNode> separator = lines.length > 3 ? Optional.of(TextNode.asSingle(recursiveParsing(restoreOriginalEscaping(cleanArgument(lines[3])), handlers, null).nodes())) : Optional.empty();
                                var shouldInterpret = lines.length > 4 && Boolean.parseBoolean(lines[4]);

                                return new TextParserV1.TagNodeValue(new NbtNode(lines[2], shouldInterpret, separator, type), 0);
                            }
                    )
            );
        }
    }

    private static Function<MutableText, Text> getTransform(String[] val) {
        if (val.length == 0) {
            return GeneralUtils.MutableTransformer.CLEAR;
        }

        Function<Style, Style> func = (x) -> x;

        for (var arg : val) {
            func = func.andThen(switch (arg) {
                case "hover" -> x -> x.withHoverEvent(null);
                case "click" -> x -> x.withClickEvent(null);
                case "color" -> x -> x.withColor((TextColor) null);
                case "insertion" -> x -> x.withInsertion(null);
                case "font" -> x -> x.withFont(null);
                case "bold" -> x -> x.withBold(null);
                case "italic" -> x -> x.withItalic(null);
                case "underline" -> x -> x.withUnderline(null);
                case "strikethrough" -> x -> x.withStrikethrough(null);
                case "all" -> x -> Style.EMPTY;
                default -> x -> x;
            });
        };

        return new GeneralUtils.MutableTransformer(func);
    }

    private static boolean isntFalse(String arg) {
        return arg.isEmpty() || !arg.equals("false");
    }

    private static TextParserV1.TagNodeBuilder wrap(Wrapper wrapper) {
        return (tag, data, input, handlers, endAt) -> {
            var out = recursiveParsing(input, handlers, endAt);
            return new TextParserV1.TagNodeValue(wrapper.wrap(out.nodes(), data), out.length());
        };
    }

    private static TextParserV1.TagNodeBuilder bool(BooleanTag wrapper) {
        return (tag, data, input, handlers, endAt) -> {
            var out = recursiveParsing(input, handlers, endAt);
            return new TextParserV1.TagNodeValue(wrapper.wrap(out.nodes(), isntFalse(data)), out.length());
        };
    }



    interface Wrapper {
        TextNode wrap(TextNode[] nodes, String arg);
    }

    interface BooleanTag {
        TextNode wrap(TextNode[] nodes, boolean value);
    }
}