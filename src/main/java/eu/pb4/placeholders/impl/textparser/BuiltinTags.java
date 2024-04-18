package eu.pb4.placeholders.impl.textparser;


import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.tag.SimpleTags;
import eu.pb4.placeholders.api.parsers.tag.NodeCreator;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.GeneralUtils;
import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.DynamicRegistryManager;
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
    public static final TextColor DEFAULT_COLOR = TextColor.fromFormatting(Formatting.WHITE);
    public static void register() {
        {
            Map<Formatting, List<String>> aliases = new HashMap<>();
            aliases.put(Formatting.GOLD, List.of("orange"));
            aliases.put(Formatting.GRAY, List.of("grey", "light_gray", "light_grey"));
            aliases.put(Formatting.LIGHT_PURPLE, List.of("pink"));
            aliases.put(Formatting.DARK_PURPLE, List.of("purple"));
            aliases.put(Formatting.DARK_GRAY, List.of("dark_grey"));

            for (Formatting formatting : Formatting.values()) {
                if (formatting.isModifier()) {
                    continue;
                }

                TagRegistry.registerDefault(
                        SimpleTags.color(
                                formatting.getName(),
                                aliases.containsKey(formatting) ? aliases.get(formatting) : List.of(),
                                formatting
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
                            (nodes, data, parser) -> {
                                return new ColorNode(nodes, TextColor.parse(data.get("value", 0, "white")).result().orElse(DEFAULT_COLOR));
                            })
            );
        }
        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "font",
                            "other_formatting",
                            false,
                            (nodes, data, parser) -> new FontNode(nodes, Identifier.tryParse(data.get("value", 0, "")))
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
                            var key = data.getNext("key");
                            var fallback = data.get("fallback");

                            List<TextNode> textList = new ArrayList<>();
                            int i = 0;
                            while (true) {
                                var part = data.getNext("" + (i++));
                                if (part == null) {
                                    break;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            return TranslatedNode.ofFallback(key, fallback, textList.toArray(TextParserImpl.CASTER));
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
                            var key = data.getNext("key");
                            var fallback = data.getNext("fallback");

                            List<TextNode> textList = new ArrayList<>();
                            int i = 0;
                            while (true) {
                                var part = data.getNext("" + (i++));
                                if (part == null) {
                                    break;
                                }
                                textList.add(parser.parseNode(part));
                            }

                            return TranslatedNode.ofFallback(key, fallback, textList.toArray(TextParserImpl.CASTER));
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
                    (data) -> new KeybindNode(data.getNext("value", ""))));
        }

        {
            TagRegistry.registerDefault(TextTag.enclosing("click", "click_action", false,
                    (nodes, data, parser) -> {
                        if (!data.isEmpty()) {
                            for (ClickEvent.Action action : ClickEvent.Action.values()) {
                                if (action.asString().equals(data.getNext("type"))) {
                                    return new ClickActionNode(nodes, action, new LiteralNode(data.getNext("value", "")));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(data.getNext("value", "")));
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
                                    var type = data.getNext("type", "").toLowerCase(Locale.ROOT);

                                    if (data.size() > 1) {
                                        if (type.equals("show_text") || type.equals("text")) {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(
                                                    data.getNext("value", "")
                                            ));
                                        } else if (type.equals("show_entity") || type.equals("entity")) {
                                            return new HoverNode<>(nodes,
                                                    HoverNode.Action.ENTITY,
                                                    new HoverNode.EntityNodeContent(
                                                            EntityType.get(data.getNext("entity", "")).orElse(EntityType.PIG),
                                                            UUID.fromString(data.getNext("uuid", Util.NIL_UUID.toString())),
                                                            new ParentNode(parser.parseNode(data.get("name", 3, "")))
                                                    ));
                                        } else if (type.equals("show_item") || type.equals("item")) {
                                            var value = data.getNext("value", "");
                                            try {
                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(ItemStack.fromNbtOrEmpty(DynamicRegistryManager.EMPTY, StringNbtReader.parse(value)))
                                                );
                                            } catch (Throwable e) {
                                                var stack = Registries.ITEM.get(Identifier.tryParse(data.get("item", value))).getDefaultStack();

                                                var count = data.getNext("count");
                                                if (count != null) {
                                                    stack.setCount(Integer.parseInt(count));
                                                }

                                                return new HoverNode<>(nodes,
                                                        HoverNode.Action.ITEM_STACK,
                                                        new HoverEvent.ItemStackContent(stack)
                                                );
                                            }
                                        } else {
                                            return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(data.get("value", type)));
                                        }
                                    } else {
                                        return new HoverNode<>(nodes, HoverNode.Action.TEXT, parser.parseNode(data.get("value", type)));
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
                                var type = data.get("type", "");

                                float freq = SimpleArguments.floatNumber(data.getNext("frequency", data.get("freq", data.get("f"))), 1);
                                float saturation = SimpleArguments.floatNumber(data.getNext("saturation", data.get("sat", data.get("s"))), 1);
                                float offset = SimpleArguments.floatNumber(data.getNext("offset", data.get("off", data.get("o"))), 0);
                                int overriddenLength = SimpleArguments.intNumber(data.getNext("length", data.get("len", data.get("l"))), -1);
                                int value = SimpleArguments.intNumber(data.get("value", data.get("val", data.get("v"))), 1);

                                return new GradientNode(nodes, switch (type) {
                                    case "oklab", "okhcl" -> overriddenLength < 0
                                            ? GradientNode.GradientProvider.rainbowOkLch(saturation, value, freq, offset)
                                            : GradientNode.GradientProvider.rainbowOkLch(saturation, value, freq, offset, overriddenLength);
                                    case "hvs" -> overriddenLength < 0
                                            ? GradientNode.GradientProvider.rainbowHvs(saturation, value, freq, offset)
                                            : GradientNode.GradientProvider.rainbowHvs(saturation, value, freq, offset, overriddenLength);
                                    default -> overriddenLength < 0
                                            ? GradientNode.GradientProvider.rainbow(saturation, value, freq, offset)
                                            : GradientNode.GradientProvider.rainbow(saturation, value, freq, offset, overriddenLength);
                                });
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
                                var textColors = new ArrayList<TextColor>();
                                int i = 0;
                                var type = data.get("type", "");

                                while (true) {
                                    var part = data.getNext("" + i);
                                    if (part == null) {
                                        break;
                                    }

                                    TextColor.parse(part).result().ifPresent(textColors::add);
                                }
                                return new GradientNode(nodes, switch (type) {
                                    case "oklab" -> GradientNode.GradientProvider.colorsOkLab(textColors);
                                    case "hvs" -> GradientNode.GradientProvider.colorsHvs(textColors);
                                    case "hard" -> GradientNode.GradientProvider.colorsHard(textColors);
                                    default -> GradientNode.GradientProvider.colors(textColors);
                                });
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
                                    var part = data.getNext("" + i);
                                    if (part == null) {
                                        break;
                                    }

                                    TextColor.parse(part).result().ifPresent(textColors::add);
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
                            "rawstyle",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                var x = Style.Codecs.CODEC.decode(StringArgOps.INSTANCE, Either.right(data));
                                if (x.error().isPresent()) {
                                    System.out.println(x.error().get().message());
                                    return TextNode.asSingle(nodes);
                                }
                                return new StyledNode(nodes, x.result().get().getFirst(), null, null, null);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.self(
                            "score",
                            "special",
                            false, (nodes, data, parser) -> {

                                return new ScoreNode(data.getNext("name", ""), data.getNext("objective", ""));
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.self(
                            "selector",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                var sel = data.getNext("pattern", "@p");
                                var arg = data.getNext("separator");

                                return new SelectorNode(sel, arg != null ? Optional.of(TextNode.of(arg)) : Optional.empty());
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.self(
                            "nbt",
                            "special",
                            false, (nodes, data, parser) -> {
                                String source = data.getNext("source", "");
                                var cleanLine1 = data.getNext("path", "");

                                var type = switch (source) {
                                    case "block" -> new BlockNbtDataSource(cleanLine1);
                                    case "entity" -> new EntityNbtDataSource(cleanLine1);
                                    case "storage" -> new StorageNbtDataSource(Identifier.tryParse(cleanLine1));
                                    default -> null;
                                };

                                if (type == null) {
                                    return TextNode.empty();
                                }

                                var separ = data.getNext("separator");

                                Optional<TextNode> separator = separ != null ?
                                        Optional.of(TextNode.asSingle(parser.parseNode(separ))) : Optional.empty();
                                var shouldInterpret = SimpleArguments.bool(data.getNext("interpret"), false);

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

        for (var arg : val.ordered()) {
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