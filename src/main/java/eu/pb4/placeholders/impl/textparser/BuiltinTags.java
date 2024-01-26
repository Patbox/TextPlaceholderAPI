package eu.pb4.placeholders.impl.textparser;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.tag.NodeCreator;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

@ApiStatus.Internal
public final class BuiltinTags {
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

                TagRegistry.registerDefault(
                        TextTag.enclosing(
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
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "bold",
                            List.of("b"),
                            "formatting",
                            true,
                            NodeCreator.bool(BoldNode::new)
                    )
            );

            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "underline",
                            List.of("underlined", "u"),
                            "formatting",
                            true,
                            NodeCreator.bool(UnderlinedNode::new)
                    )
            );

            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "strikethrough", List.of("st"),
                            "formatting",
                            true,
                            NodeCreator.bool(StrikethroughNode::new)
                    )
            );


            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "obfuscated",
                            List.of("obf", "matrix"),
                            "formatting",
                            true,
                            NodeCreator.bool(ObfuscatedNode::new)
                    )
            );

            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "italic",
                            List.of("i", "em"),
                            "formatting",
                            true,
                            NodeCreator.bool(ItalicNode::new)
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "color",
                            List.of("colour", "c"),
                            "color",
                            true,
                            (nodes, data, parser) -> new ColorNode(nodes, TextColor.parse(data.get("value", 0)).get().left().orElse(null))
                    )
            );
        }
        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "font",
                            "other_formatting",
                            false,
                            (nodes, data, parser) -> new FontNode(nodes, Identifier.tryParse(data.get("value", 0)))
                    )
            );
        }
        {
            TagRegistry.registerDefault(TextTag.self(
                    "lang",
                    List.of("translate"),
                    "special",
                    false,
                    (nodes, data, parser) -> {
                        if (!data.isEmpty()) {
                            List<TextNode> textList = new ArrayList<>();
                            int i = 1;
                            while (true) {
                                var part = data.get("" + i, i);
                                if (part == null) {
                                    break;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            return TranslatedNode.of(data.get("key", 0), textList.toArray(TextParserImpl.CASTER));
                        }
                        return TextNode.empty();
                    })
            );
        }

        {
            TagRegistry.registerDefault(TextTag.self(
                    "lang_fallback",
                    List.of("translatef", "langf", "translate_fallback"),
                    "special",
                    false,
                    (nodes, data, parser) -> {
                        if (!data.isEmpty()) {
                            List<TextNode> textList = new ArrayList<>();
                            int i = 1;
                            while (true) {
                                var part = data.get("" + i, i + 1);
                                if (part == null) {
                                    break;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            var out = TranslatedNode.ofFallback(data.get("key", 0, ""),
                                    data.get("fallback", 1, ""),
                                    textList.toArray(TextParserImpl.CASTER));
                            return out;
                        }
                        return TextNode.empty();
                    })
            );
        }

        {
            TagRegistry.registerDefault(TextTag.self("keybind",
                    List.of("key"),
                    "special",
                    false,
                    (data) -> new KeybindNode(data.get("value", 0))));
        }

        {
            TagRegistry.registerDefault(TextTag.enclosing("click", "click_action", false,
                    (nodes, data, parser) -> {
                        if (!data.isEmpty()) {
                            for (ClickEvent.Action action : ClickEvent.Action.values()) {
                                if (action.asString().equals(data.get("type", 0))) {
                                    return new ClickActionNode(nodes, action, new LiteralNode(data.get("value", 1, "")));
                                }
                            }
                        }
                        return new ParentNode(nodes);
                    }));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "run_command",
                            List.of("run_cmd"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.RUN_COMMAND, new LiteralNode(data.get("value", 0)));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "suggest_command",
                            List.of("cmd"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(data.get("value", 0)));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "open_url",
                            List.of("url"),
                            "click_action",
                            false, (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.OPEN_URL, new LiteralNode(data.get("value", 0)));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "copy_to_clipboard",
                            List.of("copy"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {

                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.COPY_TO_CLIPBOARD, new LiteralNode(data.get("value", 0)));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "change_page",
                            List.of("page"),
                            "click_action",
                            true, (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.CHANGE_PAGE, new LiteralNode(data.get("value", 0)));
                                }
                                return new ParentNode(nodes);
                            }));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "hover",
                            "hover_event",
                            true,
                            (nodes, data, parser) -> {
                                try {
                                    if (!data.isEmpty()) {
                                        // Todo: wtf
                                        HoverEvent.Action<?> action = HoverEvent.Action.CODEC
                                                .parse(JsonOps.INSTANCE, JsonParser.parseString('"' + data.get("type", 0, "").toLowerCase(Locale.ROOT) + '"')).get().left().orElse(null);
                                        if (action == HoverEvent.Action.SHOW_TEXT) {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(
                                                    data.get("value", 1, "")
                                            ));
                                        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                                            return new HoverNode<>(nodes,
                                                    HoverNode.Action.ENTITY,
                                                    new HoverNode.EntityNodeContent(
                                                            EntityType.get(data.get("entity", 1, "")).orElse(EntityType.PIG),
                                                            UUID.fromString(data.get("uuid", 2, Util.NIL_UUID.toString())),
                                                            new ParentNode(parser.parseNode(data.get("name", 3, "")))
                                                    ));
                                        } else if (action == HoverEvent.Action.SHOW_ITEM) {
                                            try {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(ItemStack.fromNbt(StringNbtReader.parse(data.get("value", 1, ""))))
                                                );
                                            } catch (Throwable e) {
                                                var stack = Registries.ITEM.get(Identifier.tryParse(data.get("item", 1, ""))).getDefaultStack();

                                                var count = data.get("count", 2);
                                                if (count != null) {
                                                    stack.setCount(Integer.parseInt(count));
                                                }

                                                var nbt = data.get("nbt", 3);
                                                if (nbt != null) {
                                                    stack.setNbt(StringNbtReader.parse(nbt));
                                                }

                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(stack)
                                                );
                                            }
                                        } else {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(data.get("value", 0)));
                                        }
                                    } else {
                                        return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(data.get("value", 0)));
                                    }
                                } catch (Exception e) {
                                    // Shut
                                }
                                return new ParentNode(nodes);
                            }));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "insert",
                            List.of("insertion"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> new InsertNode(nodes, new LiteralNode(data.get("value", 0)))));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "clear_color",
                            List.of("uncolor", "colorless"),
                            "special",
                            false,

                            (nodes, data, parser) -> GeneralUtils.removeColors(TextNode.asSingle(nodes))
                    ));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "rainbow",
                            List.of("rb"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {
                                float freq = 1;
                                float saturation = 1;
                                float offset = 0;
                                int overriddenLength = -1;


                                var freqs = data.get("frequency", 0, data.get("freq", data.get("f")));
                                if (freqs != null) {
                                    try {
                                        freq = Float.parseFloat(freqs);
                                    } catch (Exception e) {
                                        // No u
                                    }
                                }
                                var sats = data.get("saturation", 1, data.get("sat", data.get("s")));

                                if (sats != null) {
                                    try {
                                        saturation = Float.parseFloat(sats);
                                    } catch (Exception e) {
                                        // Idc
                                    }
                                }
                                var offs = data.get("offset", 2, data.get("off", data.get("o")));
                                if (offs != null) {
                                    try {
                                        offset = Float.parseFloat(offs);
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                var len = data.get("length", 3, data.get("len", data.get("l")));

                                if (len != null) {
                                    try {
                                        overriddenLength = Integer.parseInt(len);
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
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "gradient",
                            List.of("gr"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {
                                List<TextColor> textColors = new ArrayList<>();
                                int i = 0;
                                while (true) {
                                    var part = data.get("" + i, i);
                                    if (part == null) {
                                        break;
                                    }

                                    TextColor.parse(part).get().ifLeft(textColors::add);
                                }
                                return GradientNode.colors(textColors, nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "hard_gradient",
                            List.of("hgr"),
                            "gradient",
                            true,
                            (nodes, data, parser) -> {

                                var textColors = new ArrayList<TextColor>();

                                int i = 0;
                                while (true) {
                                    var part = data.get("" + i, i);
                                    if (part == null) {
                                        break;
                                    }

                                    TextColor.parse(part).get().ifLeft(textColors::add);
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
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "clear",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                return new TransformNode(nodes, getTransform(data));
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "score",
                            "special",
                            false, (nodes, data, parser) -> {

                                return new ScoreNode(data.get("name", 0, ""), data.get("objective", 1, ""));
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "selector",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                var arg = data.get("separator", 1);

                                return new SelectorNode(data.get("pattern", 0, "@p"), arg != null ? Optional.of(TextNode.of(arg)) : Optional.empty());
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "nbt",
                            "special",
                            false, (nodes, data, parser) -> {
                                var cleanLine1 = data.get("path", 1, "");

                                var type = switch (data.get("source", 0, "")) {
                                    case "block" -> new BlockNbtDataSource(cleanLine1);
                                    case "entity" -> new EntityNbtDataSource(cleanLine1);
                                    case "storage" -> new StorageNbtDataSource(Identifier.tryParse(cleanLine1));
                                    default -> null;
                                };

                                if (type == null) {
                                    return TextNode.empty();
                                }

                                var separ = data.get("separator", 2);

                                Optional<TextNode> separator = separ != null ?
                                        Optional.of(TextNode.asSingle(parser.parseNode(separ))) : Optional.empty();
                                var shouldInterpret = SimpleArguments.bool(data.get("interpret", 3), false);

                                return new NbtNode(cleanLine1, shouldInterpret, separator, type);
                            }
                    )
            );
        }
    }

    private static Function<MutableText, Text> getTransform(StringArgs val) {
        if (val.isEmpty()) {
            return GeneralUtils.MutableTransformer.CLEAR;
        }

        Function<Style, Style> func = (x) -> x;

        int i = 0;
        while (true) {
            var arg = val.get("" + i, i);
            if (arg == null) {
                break;
            }
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