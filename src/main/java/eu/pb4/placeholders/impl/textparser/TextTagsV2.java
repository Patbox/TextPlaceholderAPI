package eu.pb4.placeholders.impl.textparser;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
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
                            (nodes, data, parser) -> new ColorNode(nodes, TextColor.parse(SimpleArguments.unwrap(data)).get().left().orElse(null))
                    )
            );
        }
        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "font",
                            "other_formatting",
                            false,
                            (nodes, data, parser) -> new FontNode(nodes, Identifier.tryParse(SimpleArguments.unwrap(data)))
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
                        var lines = SimpleArguments.split(data, ':');
                        if (!lines.isEmpty()) {
                            List<TextNode> textList = new ArrayList<>();
                            boolean skipped = false;
                            for (String part : lines) {
                                if (!skipped) {
                                    skipped = true;
                                    continue;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            return TranslatedNode.of(lines.get(0), textList.toArray(TextParserImpl.CASTER));
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
                        var lines = SimpleArguments.split(data, ':');
                        if (lines.size() > 1) {
                            List<TextNode> textList = new ArrayList<>();
                            int skipped = 0;
                            for (String part : lines) {
                                if (skipped < 2) {
                                    skipped++;
                                    continue;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            var out = TranslatedNode.ofFallback(lines.get(0), lines.get(1), textList.toArray(TextParserImpl.CASTER));
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
                    (data) -> new KeybindNode(SimpleArguments.unwrap(data))));
        }

        {
            TextParserV2.registerDefault(TextParserV2.TextTag.enclosing("click", "click_action", false,
                    (nodes, data, parser) -> {
                        var lines = SimpleArguments.split(data, ':');
                        if (lines.size() > 1) {
                            for (ClickEvent.Action action : ClickEvent.Action.values()) {
                                if (action.asString().equals(lines.get(0))) {
                                    return new ClickActionNode(nodes, action, new LiteralNode(lines.get(1)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.RUN_COMMAND, new LiteralNode(SimpleArguments.unwrap(data)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(SimpleArguments.unwrap(data)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.OPEN_URL, new LiteralNode(SimpleArguments.unwrap(data)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.COPY_TO_CLIPBOARD, new LiteralNode(SimpleArguments.unwrap(data)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.CHANGE_PAGE, new LiteralNode(SimpleArguments.unwrap(data)));
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
                                var lines = SimpleArguments.split(data, ':');
                                try {
                                    if (lines.size() > 1) {
                                        // Todo: wtf
                                        HoverEvent.Action<?> action = HoverEvent.Action.CODEC
                                                .parse(JsonOps.INSTANCE, JsonParser.parseString('"' + lines.get(0).toLowerCase(Locale.ROOT) + '"')).get().left().orElse(null);
                                        if (action == HoverEvent.Action.SHOW_TEXT) {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(lines.get(1)));
                                        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                                            if (lines.size() == 4) {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ENTITY,
                                                        new HoverNode.EntityNodeContent(
                                                                EntityType.get(lines.get(1)).orElse(EntityType.PIG),
                                                                UUID.fromString(lines.get(2)),
                                                                new ParentNode(parser.parseNode(lines.get(3)))
                                                        ));
                                            }
                                        } else if (action == HoverEvent.Action.SHOW_ITEM) {
                                            try {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(ItemStack.fromNbt(StringNbtReader.parse(lines.get(1))))
                                                );
                                            } catch (Throwable e) {
                                                if (lines.size() > 1) {
                                                    var stack = Registries.ITEM.get(Identifier.tryParse(lines.get(1))).getDefaultStack();

                                                    if (lines.size() > 2) {
                                                        stack.setCount(Integer.parseInt(lines.get(2)));
                                                    }

                                                    if (lines.size() > 3) {
                                                        stack.setNbt(StringNbtReader.parse(SimpleArguments.unwrap(lines.get(3))));
                                                    }

                                                    return new HoverNode<>(nodes,
                                                            HoverNode.Action.ITEM_STACK,
                                                            new HoverEvent.ItemStackContent(stack)
                                                    );
                                                }
                                            }
                                        } else {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(SimpleArguments.unwrap(data)));
                                        }
                                    } else {
                                        return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(SimpleArguments.unwrap(data)));
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
                            (nodes, data, parser) -> new InsertNode(nodes, new LiteralNode(SimpleArguments.unwrap(data)))));
        }

        {
            TextParserV2.registerDefault(
                    TextParserV2.TextTag.enclosing(
                            "clear_color",
                            List.of("uncolor", "colorless"),
                            "special",
                            false,

                            (nodes, data, parser) -> GeneralUtils.removeColors(TextNode.asSingle(nodes))
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
                                var val = SimpleArguments.split(data, ':');
                                float freq = 1;
                                float saturation = 1;
                                float offset = 0;
                                int overriddenLength = -1;

                                if (!val.isEmpty()) {
                                    try {
                                        freq = Float.parseFloat(val.get(0));
                                    } catch (Exception e) {
                                        // No u
                                    }
                                }
                                if (val.size() >= 2) {
                                    try {
                                        saturation = Float.parseFloat(val.get(1));
                                    } catch (Exception e) {
                                        // Idc
                                    }
                                }
                                if (val.size() >= 3) {
                                    try {
                                        offset = Float.parseFloat(val.get(2));
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                if (val.size() >= 4) {
                                    try {
                                        overriddenLength = Integer.parseInt(val.get(3));
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
                                var val = SimpleArguments.split(data, ':');
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
                                var val = SimpleArguments.split(data, ':');
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
                                var val = SimpleArguments.split(data, ':');

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
                                var val = SimpleArguments.split(data, ':');
                                if (val.size() == 2) {
                                    return new ScoreNode(val.get(0), val.get(1));
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
                                var lines = SimpleArguments.split(data, ':');
                                if (lines.size() == 2) {
                                    return new SelectorNode(lines.get(0), Optional.of(TextNode.of(lines.get(1))));
                                } else if (lines.size() == 1) {
                                    return new SelectorNode(lines.get(0), Optional.empty());
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
                                var lines = SimpleArguments.split(data, ':');

                                if (lines.size() < 3) {
                                    return TextNode.empty();
                                }

                                var cleanLine1 = lines.get(1);

                                var type = switch (lines.get(0)) {
                                    case "block" -> new BlockNbtDataSource(cleanLine1);
                                    case "entity" -> new EntityNbtDataSource(cleanLine1);
                                    case "storage" -> new StorageNbtDataSource(Identifier.tryParse(cleanLine1));
                                    default -> null;
                                };

                                if (type == null) {
                                    return TextNode.empty();
                                }

                                Optional<TextNode> separator = lines.size() > 3 ?
                                        Optional.of(TextNode.asSingle(parser.parseNode(lines.get(3)))) : Optional.empty();
                                var shouldInterpret = lines.size() > 4 && SimpleArguments.bool(lines.get(4));

                                return new NbtNode(lines.get(2), shouldInterpret, separator, type);
                            }
                    )
            );
        }
    }

    private static Function<MutableText, Text> getTransform(List<String> val) {
        if (val.isEmpty()) {
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
        return SimpleArguments.bool(arg, arg.isEmpty());
    }
}