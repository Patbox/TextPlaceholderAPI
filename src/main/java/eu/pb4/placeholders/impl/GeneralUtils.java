package eu.pb4.placeholders.impl;

import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


@ApiStatus.Internal
public class GeneralUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger("Text Placeholder API");
    public static final boolean IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static final boolean IS_LEGACY_TRANSLATION;

    static {
        boolean IS_LEGACY1;
        try {
            IS_LEGACY1 = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().compareTo(Version.parse("1.19.4")) < 0;
        } catch (VersionParsingException e) {
            IS_LEGACY1 = false;
            e.printStackTrace();
        }
        IS_LEGACY_TRANSLATION = IS_LEGACY1;
    }

    public static String durationToString(long x) {
        long seconds = x % 60;
        long minutes = (x / 60) % 60;
        long hours = (x / (60 * 60)) % 24;
        long days = x / (60 * 60 * 24);

        if (days > 0) {
            return String.format("%dd%dh%dm%ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh%dm%ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm%ds", minutes, seconds);
        } else if (seconds > 0) {
            return String.format("%ds", seconds);
        } else {
            return "---";
        }
    }

    public static boolean isEmpty(Text text) {
        return (
                text.getContent() == TextContent.EMPTY
                || (text.getContent() instanceof LiteralTextContent l && l.string().isEmpty())
               ) && text.getSiblings().isEmpty();
    }

    public static MutableText toGradient(Text base, GradientNode.GradientProvider posToColor) {
        return recursiveGradient(base, posToColor, 0, getGradientLength(base)).text();
    }

    private static int getGradientLength(Text base) {
        int length = base.getContent() instanceof LiteralTextContent l ? l.string().length() : base.getContent() == TextContent.EMPTY ? 0 : 1;

        for (var text : base.getSiblings()) {
            length += getGradientLength(text);
        }

        return length;
    }

    private static TextLengthPair recursiveGradient(Text base, GradientNode.GradientProvider posToColor, int pos, int totalLength) {
        if (base.getStyle().getColor() == null) {
            MutableText out = Text.empty().setStyle(base.getStyle());
            if (base.getContent() instanceof LiteralTextContent literalTextContent) {
                var l = literalTextContent.string().length();
                for (var i = 0; i < l; i++) {
                    var character = literalTextContent.string().charAt(i);
                    int value;
                    if (Character.isHighSurrogate(character) && i + 1 < l) {
                        var next = literalTextContent.string().charAt(++i);
                        if (Character.isLowSurrogate(next)) {
                            value = Character.toCodePoint(character, next);
                        } else {
                            value = character;
                        }
                    } else {
                        value = character;
                    }

                    out.append(Text.literal(Character.toString(value)).setStyle(Style.EMPTY.withColor(posToColor.getColorAt(pos++, totalLength))));

                }
            } else {
                out.append(base.copyContentOnly().setStyle(Style.EMPTY.withColor(posToColor.getColorAt(pos++, totalLength))));
            }

            for (Text sibling : base.getSiblings()) {
                var pair = recursiveGradient(sibling, posToColor, pos, totalLength);
                pos = pair.length;
                out.append(pair.text);
            }
            return new TextLengthPair(out, pos);
        }
        return new TextLengthPair(base.copy(), pos + base.getString().length());
    }

    public static int hvsToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6) % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        return switch (h) {
            case 0 -> rgbToInt(value, t, p);
            case 1 -> rgbToInt(q, value, p);
            case 2 -> rgbToInt(p, value, t);
            case 3 -> rgbToInt(p, q, value);
            case 4 -> rgbToInt(t, p, value);
            case 5 -> rgbToInt(value, p, q);
            default -> 0;
        };
    }

    public static int rgbToInt(float r, float g, float b) {
        return (((int) (r * 0xff)) & 0xFF) << 16 | (((int) (g * 0xff)) & 0xFF) << 8 | (((int) (b * 0xff) & 0xFF));
    }

    public static HSV rgbToHsv(int rgb) {
        float b = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float g = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float r = (float) (rgb % 256) / 255;

        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float diff = cmax - cmin;
        float h = -1, s = -1;

        if (cmax == cmin) {
            h = 0;
        } else if (cmax == r) {
            h = (0.1666f * ((g - b) / diff) + 1) % 1;
        } else if (cmax == g) {
            h = (0.1666f * ((b - r) / diff) + 0.333f) % 1;
        } else if (cmax == b) {
            h = (0.1666f * ((r - g) / diff) + 0.666f) % 1;
        }
        if (cmax == 0) {
            s = 0;
        } else {
            s = (diff / cmax);
        }

        return new HSV(h, s, cmax);
    }

    public static Text removeHoverAndClick(Text input) {
        var output = cloneText(input);
        removeHoverAndClick(output);
        return output;
    }

    private static void removeHoverAndClick(MutableText input) {
        if (input.getStyle() != null) {
            input.setStyle(input.getStyle().withHoverEvent(null).withClickEvent(null));
        }

        if (input.getContent() instanceof TranslatableTextContent text) {
            for (int i = 0; i < text.getArgs().length; i++) {
                var arg = text.getArgs()[i];
                if (arg instanceof MutableText argText) {
                    removeHoverAndClick(argText);
                }
            }
        }

        for (var sibling : input.getSiblings()) {
            removeHoverAndClick((MutableText) sibling);
        }

    }

    public static MutableText cloneText(Text input) {
        MutableText baseText;
        if (input.getContent() instanceof TranslatableTextContent translatable) {
            var obj = new ArrayList<>();

            for (var arg : translatable.getArgs()) {
                if (arg instanceof Text argText) {
                    obj.add(cloneText(argText));
                } else {
                    obj.add(arg);
                }
            }

            baseText = Text.translatable(translatable.getKey(), obj.toArray());
        } else {
            baseText = input.copyContentOnly();
        }

        for (var sibling : input.getSiblings()) {
            baseText.append(cloneText(sibling));
        }

        baseText.setStyle(input.getStyle());
        return baseText;
    }

    public static Text getItemText(ItemStack stack, boolean rarity) {
        if (!stack.isEmpty()) {
            MutableText mutableText = Text.empty().append(stack.getName());
            if (stack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }

            if (rarity) {
                mutableText.formatted(stack.getRarity().formatting);
            }
            mutableText.styled((style) -> {
                return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack)));
            });

            return mutableText;
        }

        return Text.empty().append(ItemStack.EMPTY.getName());
    }

    public static ParentNode convertToNodes(Text input) {
        var list = new ArrayList<TextNode>();

        if (input.getContent() instanceof LiteralTextContent content) {
            list.add(new LiteralNode(content.string()));
        } else if (input.getContent() instanceof TranslatableTextContent content) {
            var args = new ArrayList<>();
            for (var arg : content.getArgs()) {
                if (arg instanceof Text text) {
                    args.add(convertToNodes(text));
                } else if (arg instanceof String s) {
                    args.add(new LiteralNode(s));
                } else {
                    args.add(arg);
                }
            }

            if (IS_LEGACY_TRANSLATION) {
                list.add(TranslatedNode.of(content.getKey(), args.toArray()));
            } else {
                list.add(TranslatedNode.ofFallback(content.getKey(), content.getFallback(), args.toArray()));
            }
        } else if (input.getContent() instanceof ScoreTextContent content) {
            list.add(new ScoreNode(content.getName(), content.getObjective()));
        } else if (input.getContent() instanceof KeybindTextContent content) {
            list.add(new KeybindNode(content.getKey()));
        } else if (input.getContent() instanceof SelectorTextContent content) {
            list.add(new SelectorNode(content.getPattern(), content.getSeparator().map(GeneralUtils::convertToNodes)));
        } else if (input.getContent() instanceof NbtTextContent content) {
            list.add(new NbtNode(content.getPath(), content.shouldInterpret(), content.getSeparator().map(GeneralUtils::convertToNodes), content.getDataSource()));
        }


        for (var child : input.getSiblings()) {
            list.add(convertToNodes(child));
        }

        if (input.getStyle() == Style.EMPTY) {
            return new ParentNode(list.toArray(new TextNode[0]));
        } else {
            var style = input.getStyle();
            var hoverValue = style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT
                    ? convertToNodes(style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT)) : null;

            var clickValue = style.getClickEvent() != null ? new LiteralNode(style.getClickEvent().getValue()) : null;
            var insertion = style.getInsertion() != null ? new LiteralNode(style.getInsertion()) : null;

            return new StyledNode(list.toArray(new TextNode[0]), style, hoverValue, clickValue, insertion);
        }
    }

    public static TextNode removeColors(TextNode node) {
        if (node instanceof ParentTextNode parentNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentNode.getChildren()) {
                list.add(removeColors(child));
            }

            if (node instanceof ColorNode || node instanceof FormattingNode) {
                return new ParentNode(list.toArray(new TextNode[0]));
            } else if (node instanceof StyledNode styledNode) {
                return new StyledNode(list.toArray(new TextNode[0]), styledNode.rawStyle().withColor((TextColor) null), styledNode.hoverValue(), styledNode.clickValue(), styledNode.insertion());
            }

            return parentNode.copyWith(list.toArray(new TextNode[0]));
        } else {
            return node;
        }
    }

    public record HSV(float h, float s, float v) {
    }

    public record TextLengthPair(MutableText text, int length) {
        public static final TextLengthPair EMPTY = new TextLengthPair(null, 0);
    }

    public record Pair<L, R>(L left, R right) {
    }
}
