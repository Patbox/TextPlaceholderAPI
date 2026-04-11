package eu.pb4.placeholders.impl.textparser;


import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.tag.NodeCreator;
import eu.pb4.placeholders.api.parsers.tag.SimpleTags;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.GeneralUtils;
import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

@ApiStatus.Internal
public final class BuiltinTags {
    public static final TextColor DEFAULT_COLOR = TextColor.fromLegacyFormat(ChatFormatting.WHITE);

    public static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();

    public static void register() {
        var EMPTY_ID = Identifier.fromNamespaceAndPath("", "");

        var extenderColorResolver = DynamicColorNode.extendedTextColorParse(COLOR_ALIASES::get);
        {
            Map<ChatFormatting, List<String>> aliases = new HashMap<>();
            aliases.put(ChatFormatting.GOLD, List.of("orange"));
            aliases.put(ChatFormatting.GRAY, List.of("grey", "light_gray", "light_grey"));
            aliases.put(ChatFormatting.LIGHT_PURPLE, List.of("pink"));
            aliases.put(ChatFormatting.DARK_PURPLE, List.of("purple"));
            aliases.put(ChatFormatting.DARK_GRAY, List.of("dark_grey"));

            for (ChatFormatting formatting : ChatFormatting.values()) {
                if (formatting.isFormat()) {
                    continue;
                }
                var alias = aliases.getOrDefault(formatting, List.of());

                for (var x : alias) {
                    COLOR_ALIASES.put(x, TextColor.fromLegacyFormat(formatting));
                }

                TagRegistry.registerDefault(
                        SimpleTags.color(
                                formatting.getName(),
                                alias,
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
                                return new DynamicColorNode(nodes, parser.parseNode(data.get("value", 0, "white")), extenderColorResolver);
                            })
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "shadow",
                            List.of("shadow_color"),
                            "color",
                            false,
                            (nodes, data, parser) -> {
                                try {
                                    if (data.contains("scale") && data.size() == 1) {
                                        return new DynamicShadowNode(nodes, Float.parseFloat(data.get("scale", "0")), 1);
                                    }

                                    var color = data.get("value", 0);
                                    if (color == null) {
                                        return new DynamicShadowNode(nodes);
                                    }

                                    int value;
                                    if (color.startsWith("#")) {
                                        value = Integer.parseUnsignedInt(color.substring(1), 16);
                                        if (color.length() == 7) {
                                            value = (value & 0xFFFFFF) | 0xFF000000;
                                        }
                                    } else {
                                        value = extenderColorResolver.apply(color).getValue() | 0xFF000000;
                                    }

                                    return new ShadowNode(nodes, value);
                                } catch (Throwable e) {
                                    return new ParentNode(nodes);
                                }
                            })
            );
        }
        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "font",
                            "other_formatting",
                            false,
                            (nodes, data, parser) -> {
                                var val = data.get("value");
                                if (val == null) {
                                    if (data.size() > 1) {
                                        val = data.getNext("key", "minecraft") + ":" + data.getNext("path", "");
                                    } else {
                                        val = data.getNext("val");
                                        if (val == null) {
                                            val = data.input().strip();
                                        }
                                    }
                                }
                                var id = Identifier.tryParse(val);

                                return new FontNode(nodes, id != null ? id : EMPTY_ID);
                            }
                    )
            );
        }

        {
            var emptyId = Identifier.parse("");
            TagRegistry.registerDefault(
                    TextTag.self(
                            "atlas",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                var atlas = Objects.requireNonNullElse(Identifier.tryParse(data.getNext("atlas", "")), emptyId);
                                var texture = Objects.requireNonNullElse(Identifier.tryParse(data.getNext("texture", "")), emptyId);
                                var fallback = Optional.ofNullable(data.get("fallback")).map(parser::parseNode);

                                return new ObjectNode(new AtlasSprite(atlas, texture), fallback);
                            }
                    )
            );
        }

        {
            var emptyId = Identifier.parse("");
            TagRegistry.registerDefault(
                    TextTag.self(
                            "player",
                            "special",
                            false,
                            (nodes, data, parser) -> {
                                var fallback = Optional.ofNullable(data.get("fallback")).map(parser::parseNode);
                                var hat = SimpleArguments.bool(data.get("hat"), true);

                                var texture = data.get("texture");

                                if (texture != null) {
                                    PropertyMap map = new PropertyMap(ImmutableMultimap.of("textures", new Property("textures", texture, null)));
                                    return new ObjectNode(new PlayerSprite(ResolvableProfile.createResolved(new GameProfile(Util.NIL_UUID, "", map)), hat), fallback);
                                }

                                var next = data.getNext("name", "");
                                var maybeUuid = data.get("uuid");

                                if (maybeUuid != null) {
                                    return new DynamicPlayerHeadNode(parser.parseNode(maybeUuid), hat, DynamicPlayerHeadNode.Type.UUID, fallback);
                                }

                                UUID uuid = null;
                                if (maybeUuid == null) {
                                    try {
                                        uuid = UUID.fromString(next);
                                    } catch (Throwable ignored) {
                                    }
                                } else {
                                    try {
                                        uuid = UUID.fromString(maybeUuid);
                                    } catch (Throwable ignored) {
                                    }
                                }

                                if (uuid != null) {
                                    try {
                                        return new ObjectNode(new PlayerSprite(ResolvableProfile.createUnresolved(uuid), hat), fallback);
                                    } catch (Throwable e) {
                                    }
                                }

                                if (next != null) {
                                    return new DynamicPlayerHeadNode(parser.parseNode(next), hat, DynamicPlayerHeadNode.Type.EITHER, fallback);
                                }

                                return new ObjectNode(new PlayerSprite(ResolvableProfile.createUnresolved(""), hat), fallback);
                            }
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

                            return TranslatedNode.ofFallback(key, fallback, (Object[]) textList.toArray(TextNode[]::new));
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

                            return TranslatedNode.ofFallback(key, fallback, (Object[]) textList.toArray(TextNode[]::new));
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
                            var type = data.getNext("type");
                            var value = data.getNext("value", "");
                            var extraData = data.getNext("data", null);
                            var extraData2 = data.getNested("data");

                            for (var action : ClickEvent.Action.values()) {
                                if (action.getSerializedName().equals(type) && action.isAllowedFromServer()) {
                                    return new ClickActionNode(nodes, action, parser.parseNode(value),
                                            extraData != null ? Either.left(parser.parseNode(extraData)) : (extraData2 != null ? Either.right(extraData2) : null));
                                }
                            }
                        }
                        return new ParentNode(nodes);
                    }));
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "show_dialog",
                            "click_action",
                            false,
                            (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    return new ClickActionNode(nodes, ClickEvent.Action.SHOW_DIALOG, parser.parseNode(data.get("value", 0, "")));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
        }

        {
            TagRegistry.registerDefault(
                    TextTag.enclosing(
                            "custom_click",
                            List.of("click"),
                            "click_action",
                            false,
                            (nodes, data, parser) -> {
                                if (!data.isEmpty()) {
                                    var value = data.get("value", 0, "");
                                    var extraData = data.get("data", 1);
                                    var extraData2 = data.getNested("data");

                                    return new ClickActionNode(nodes, ClickEvent.Action.CUSTOM, parser.parseNode(value),
                                            extraData != null ? Either.left(parser.parseNode(extraData)) : (extraData2 != null ? Either.right(extraData2) : null));
                                }
                                return new ParentNode(nodes);
                            }
                    )
            );
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.RUN_COMMAND, parser.parseNode(data.get("value", 0, "")));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.SUGGEST_COMMAND, parser.parseNode(data.getNext("value", "")));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.OPEN_URL, parser.parseNode(data.get("value", 0, "")));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.COPY_TO_CLIPBOARD, parser.parseNode(data.get("value", 0)));
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
                                    return new ClickActionNode(nodes, ClickEvent.Action.CHANGE_PAGE, parser.parseNode(data.get("value", 0)));
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
                                    var type = data.get("type");
                                    if (type != null || data.size() > 1) {
                                        if (type == null) {
                                            type = data.getNext("type", "");
                                        }
                                        type = type.toLowerCase(Locale.ROOT);
                                        switch (type) {
                                            case "show_text", "text" -> {
                                                return new HoverNode<>(nodes, HoverNode.Action.TEXT_NODE,
                                                        parser.parseNode(data.getNext("value", ""))
                                                );
                                            }
                                            case "show_entity", "entity" -> {
                                                var entType = data.getNext("entity", "");
                                                var uuid = data.getNext("uuid", Util.NIL_UUID.toString());

                                                return new HoverNode<>(nodes, HoverNode.Action.ENTITY_NODE,
                                                        new HoverNode.EntityNodeContent(EntityType.byString(entType).orElse(EntityType.PIG),
                                                                UUID.fromString(uuid),
                                                                new ParentNode(parser.parseNode(data.get("name", 3, "")))
                                                        )
                                                );
                                            }
                                            case "show_item", "item" -> {
                                                var value = data.getNext("value", "");
                                                try {
                                                    var nbt = TagParser.parseCompoundFully(value);

                                                    return new HoverNode<>(nodes, HoverNode.Action.LAZY_ITEM_STACK,
                                                            new HoverNode.LazyItemStackNodeContent<>(Identifier.parse(nbt.getStringOr("id", "")),
                                                                    nbt.contains("count") ? nbt.getIntOr("count", 1) : 1,
                                                                    NbtOps.INSTANCE,
                                                                    nbt.contains("components") ? nbt.getCompound("components").orElse(null) : null
                                                            )
                                                    );
                                                } catch (Throwable ignored) {
                                                }
                                                try {
                                                    var id = Identifier.parse(data.get("item", value));
                                                    var count = 1;
                                                    var countTxt = data.getNext("count", "1");
                                                    if (countTxt != null) {
                                                        count = Integer.parseInt(countTxt);
                                                    }

                                                    return new HoverNode<>(nodes, HoverNode.Action.LAZY_ITEM_STACK,
                                                            new HoverNode.LazyItemStackNodeContent<>(id, count,
                                                                    StringArgOps.INSTANCE,
                                                                    Either.right(data.getNestedOrEmpty("components"))
                                                            )
                                                    );
                                                } catch (Throwable ignored) {
                                                }
                                            }
                                            default -> {
                                                return new HoverNode<>(nodes, HoverNode.Action.TEXT_NODE,
                                                        parser.parseNode(data.get("value", type))
                                                );
                                            }
                                        }
                                    } else {
                                        return new HoverNode<>(nodes, HoverNode.Action.TEXT_NODE,
                                                parser.parseNode(data.getNext("value"))
                                        );
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
                            (nodes, data, parser) -> new InsertNode(nodes, parser.parseNode(data.get("value", 0)))));
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

                                    TextColor.parseColor(part).result().ifPresent(textColors::add);
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

                                    TextColor.parseColor(part).result().ifPresent(textColors::add);
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
                                var x = Style.Serializer.CODEC.decode(StringArgOps.INSTANCE, Either.right(data));
                                if (x.error().isPresent()) {
                                    System.out.println(x.error().get().message());
                                    return TextNode.asSingle(nodes);
                                }
                                return new StyledNode(nodes, x.result().get().getFirst(), (StyledNode.HoverData<?>) null, null, null);
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

                                return new SelectorNode(parser.parseNode(sel), arg != null ? Optional.of(TextNode.of(arg)) : Optional.empty());
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
                                var type = data.getNext("type", "");
                                var path = data.getNext("path", "");

                                var separ = data.getNext("separator");

                                Optional<TextNode> separator = separ != null ?
                                        Optional.of(TextNode.asSingle(parser.parseNode(separ))) : Optional.empty();
                                var shouldInterpret = SimpleArguments.bool(data.getNext("interpret"), false);
                                var plain = SimpleArguments.bool(data.getNext("plain"), false);

                                return new NbtNode(type, path, source, shouldInterpret, plain, separator);
                            }
                    )
            );
        }
    }

    private static Function<MutableComponent, Component> getTransform(StringArgs val) {
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
                case "underline" -> x -> x.withUnderlined(null);
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