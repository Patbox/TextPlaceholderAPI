package eu.pb4.placeholders.impl.textparser;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.TextParserV2;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

import static eu.pb4.placeholders.impl.textparser.TextParserImpl.cleanArgument;
import static eu.pb4.placeholders.impl.textparser.TextParserImpl.restoreOriginalEscaping;

@ApiStatus.Internal
public final class TextTagsV2 {
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

                TextParserV2.registerDefault(
                        TextParserV2.TextTag.enclosing(
                                formatting.getName(),
                                aliases.containsKey(formatting.getName()) ? aliases.get(formatting.getName()) : List.of(),
                                "color",
                                true,
                                (nodes, arg, parser) -> new FormattingNode(nodes, formatting)
                        )
                );
            }
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "bold",
                            List.of("b"),
                            "formatting",
                            true,
                            TextParserV2.NodeCreator.bool(BoldNode::new)
                    )
            );

            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "underline",
                            List.of("underlined", "u"),
                            "formatting",
                            true,
                            TextParserV2.NodeCreator.bool(UnderlinedNode::new)
                    )
            );

            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "strikethrough", List.of("st"),
                            "formatting",
                            true,
                            TextParserV2.NodeCreator.bool(StrikethroughNode::new)
                    )
            );


            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "obfuscated",
                            List.of("obf", "matrix"),
                            "formatting",
                            true,
                            TextParserV2.NodeCreator.bool(ObfuscatedNode::new)
                    )
            );

            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "italic",
                            List.of("i", "em"),
                            "formatting",
                            true,
                            TextParserV2.NodeCreator.bool(ItalicNode::new)
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "color",
                            List.of("colour", "c"),
                            "color",
                            true,
                            (nodes, data, parser) -> new ColorNode(nodes, TextColor.parse(cleanArgument(data)).get().left().orElse(null))
                    )
            );
        }
        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "font",
                            "other_formatting",
                            false,
                            (nodes, data, parser) -> new FontNode(nodes, Identifier.tryParse(cleanArgument(data)))
                    )
            );
        }
        {
            TextParserV2.registerDefault(TextParserV2.TextTag.self(
                    "lang",
                    List.of("translate"),
                    "special",
                    false,
                    (nodes, data, parser) -> {
                        var lines = data.split(":");
                        if (lines.length > 0) {
                            List<TextNode> textList = new ArrayList<>();
                            boolean skipped = false;
                            for (String part : lines) {
                                if (!skipped) {
                                    skipped = true;
                                    continue;
                                }
                                textList.add(parser.parseNode(cleanArgument(part)));
                            }

                            return TranslatedNode.of(cleanArgument(lines[0]), textList.toArray(TextParserImpl.CASTER));
                        }
                        return TextNode.empty();
                    })
            );
        }

        {
            TextParserV2.registerDefault(TextParserV2.TextTag.self(
                    "lang_fallback",
                    List.of("translatef", "langf", "translate_fallback"),
                    "special",
                    false,
                    (nodes, data, parser) -> {
                        var lines = data.split(":");
                        if (lines.length > 1) {
                            List<TextNode> textList = new ArrayList<>();
                            int skipped = 0;
                            for (String part : lines) {
                                if (skipped < 2) {
                                    skipped++;
                                    continue;
                                }
                                textList.add(parser.parseNode(cleanArgument(part)));
                            }

                            var out = TranslatedNode.ofFallback(cleanArgument(lines[0]), cleanArgument(lines[1]), textList.toArray(TextParserImpl.CASTER));
                            return out;
                        }
                        return TextNode.empty();
                    })
            );
        }

        {
            TextParserV2.registerDefault(TextParserV2.TextTag.self("keybind",
                    List.of("key"),
                    "special",
                    false,
                    (data) -> new KeybindNode(cleanArgument(data))));
        }

        {
            TextParserV2.registerDefault(TextParserV2.TextTag.enclosing("click", "click_action", false,
                    (nodes, data, parser) -> {
                        String[] lines = data.split(":", 2);
                        if (lines.length > 1) {
                            for (ClickEvent.Action action : ClickEvent.Action.values()) {
                                if (action.asString().equals(cleanArgument(lines[0]))) {
                                    return new ClickActionNode(nodes, action, new LiteralNode(restoreOriginalEscaping(cleanArgument(lines[1]))));
                                }
                            }
                        }
                        return new ParentNode(nodes);
                    }));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "run_command",
                            List.of("run_cmd"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.RUN_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data))));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "suggest_command",
                            List.of("cmd"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data))));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "open_url",
                            List.of("url"),
                            "click_action",
                            false, (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.OPEN_URL, new LiteralNode(restoreOriginalEscaping(cleanArgument(data))));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "copy_to_clipboard",
                            List.of("copy"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.COPY_TO_CLIPBOARD, new LiteralNode(restoreOriginalEscaping(cleanArgument(data))));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "change_page",
                            List.of("page"),
                            "click_action",
                            true, (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.CHANGE_PAGE, new LiteralNode(restoreOriginalEscaping(cleanArgument(data))));
                                }
                                return new ParentNode(nodes);
                            }));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "hover",
                            "hover_event",
                            true,
                            (nodes, data, parser) -> {
                                String[] lines = data.split(":", 2);
                                try {
                                    if (lines.length > 1) {
                                        HoverEvent.Action<?> action = HoverEvent.Action.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(cleanArgument(lines[0].toLowerCase(Locale.ROOT)))).get().left().orElse(null);
                                        if (action == HoverEvent.Action.SHOW_TEXT) {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(cleanArgument(lines[1])));
                                        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                                            lines = lines[1].split(":", 3);
                                            if (lines.length == 3) {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ENTITY,
                                                        new HoverNode.EntityNodeContent(
                                                                EntityType.get(restoreOriginalEscaping(cleanArgument(lines[0]))).orElse(EntityType.PIG),
                                                                UUID.fromString(cleanArgument(lines[1])),
                                                                new ParentNode(parser.parseNode(cleanArgument(lines[2])))
                                                        ));
                                            }
                                        } else if (action == HoverEvent.Action.SHOW_ITEM) {
                                            try {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(ItemStack.fromNbt(StringNbtReader.parse(restoreOriginalEscaping(cleanArgument(lines[1])))))
                                                );
                                            } catch (Throwable e) {
                                                lines = lines[1].split(":", 2);
                                                if (lines.length > 0) {
                                                    var stack = Registries.ITEM.get(Identifier.tryParse(lines[0])).getDefaultStack();

                                                    if (lines.length > 1) {
                                                        stack.setCount(Integer.parseInt(lines[1]));
                                                    }

                                                    if (lines.length > 2) {
                                                        stack.setNbt(StringNbtReader.parse(cleanArgument(lines[2])));
                                                    }

                                                    return new HoverNode<>(nodes,
                                                            HoverNode.Action.ITEM_STACK,
                                                            new HoverEvent.ItemStackContent(stack)
                                                    );
                                                }
                                            }
                                        } else {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(cleanArgument(data)));
                                        }
                                    } else {
                                        return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(cleanArgument(data)));
                                    }
                                } catch (Exception e) {
                                    // Shut
                                }
                                return new ParentNode(nodes);
                            }));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "insert",
                            List.of("insertion"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> new InsertNode(nodes, new LiteralNode(cleanArgument(data)))));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "clear_color",
                            List.of("uncolor", "colorless"),
                            "special",
                            false,

                            (nodes, data, parser) -> GeneralUtils.removeColors(new ParentNode(nodes))
                    ));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "rainbow",
                            List.of("rb"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {
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


                                return overriddenLength < 0
                                        ? GradientNode.rainbow(saturation, 1, freq, offset, nodes)
                                        : GradientNode.rainbow(saturation, 1, freq, offset, overriddenLength, nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "gradient",
                            List.of("gr"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {
                                String[] val = data.split(":");


                                List<TextColor> textColors = new ArrayList<>();
                                for (String string : val) {
                                    TextColor.parse(string).get().ifLeft(textColors::add);
                                }
                                return GradientNode.colors(textColors, nodes);
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "hard_gradient",
                            List.of("hgr"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {
                                String[] val = data.split(":");


                                var textColors = new ArrayList<TextColor>();

                                for (String string : val) {
                                    TextColor.parse(string).get().ifLeft(textColors::add);
                                }
                                // We cannot have an empty list!
                                if (textColors.isEmpty()) {
                                    return new ParentNode(nodes);
                                }

                                return GradientNode.colorsHard(textColors, nodes);

                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "clear",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                String[] val = data.isEmpty() ? new String[0] : data.split(":");


                                return new TransformNode(nodes, getTransform(val));

                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "score",
                            "special",
                            false, (nodes, data, parser) -> {
                                String[] lines = data.split(":");
                                if (lines.length == 2) {
                                    return new ScoreNode(cleanArgument(lines[0]), cleanArgument(lines[1]));
                                }
                                return TextNode.empty();
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "selector",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                String[] lines = data.split(":");
                                if (lines.length == 2) {
                                    return new SelectorNode(cleanArgument(lines[0]), Optional.of(TextNode.of(cleanArgument(lines[1]))));
                                } else if (lines.length == 1) {
                                    return new SelectorNode(cleanArgument(lines[0]), Optional.empty());
                                }
                                return TextNode.empty();
                            }
                    )
            );
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "nbt",
                            "special",
                            false, (nodes, data, parser) -> {
                                String[] lines = data.split(":");

                                if (lines.length < 3) {
                                    return TextNode.empty();
                                }

                                var cleanLine1 = cleanArgument(lines[1]);

                                var type = switch (lines[0]) {
                                    case "block" -> new BlockNbtDataSource(cleanLine1);
                                    case "entity" -> new EntityNbtDataSource(cleanLine1);
                                    case "storage" -> new StorageNbtDataSource(Identifier.tryParse(cleanLine1));
                                    default -> null;
                                };

                                if (type == null) {
                                    return TextNode.empty();
                                }

                                Optional<TextNode> separator = lines.length > 3 ?
                                        Optional.of(TextNode.asSingle(parser.parseNode(cleanArgument(lines[3])))) : Optional.empty();
                                var shouldInterpret = lines.length > 4 && Boolean.parseBoolean(lines[4]);

                                return new NbtNode(lines[2], shouldInterpret, separator, type);
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
        }

        return new GeneralUtils.MutableTransformer(func);
    }

    private static boolean isntFalse(String arg) {
        return arg.isEmpty() || !arg.equals("false");
    }
}