package eu.pb4.placeholders.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.placeholders.TextParser;
import io.netty.util.internal.UnstableApi;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.pb4.placeholders.util.GeneralUtils.Pair;
import static eu.pb4.placeholders.util.GeneralUtils.TextLengthPair;

@ApiStatus.Internal
public class TextParserUtils {
    // Based on minimessage's regex, modified to fit more parsers needs
    public static final Pattern STARTING_PATTERN = Pattern.compile("<(?<id>[^<>/]+)(?<data>(:([']?([^'](\\\\\\\\['])?)+[']?))*)>");
    public static final List<Pair<String, String>> ESCAPED_CHARS = new ArrayList<>();
    public static final List<Pair<String, String>> UNESCAPED_CHARS = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).create();
    private static boolean IS_REGISTERED = false;

    public static Text parse(String string, Map<String, TextParser.TextFormatterHandler> handlers) {
        if (!IS_REGISTERED) {
            register();
        }
        return recursiveParsing(escapeCharacters(string), handlers, null).text();
    }

    public static String escapeCharacters(String string) {
        for (Pair<String, String> entry : ESCAPED_CHARS) {
            string = string.replaceAll(Matcher.quoteReplacement(entry.left()), entry.right());
        }
        return string;
    }

    public static String removeEscaping(String string) {
        for (Pair<String, String> entry : UNESCAPED_CHARS) {
            try {
                string = string.replaceAll(entry.right(), entry.left());
            } catch (Exception e) {
                // Silence!
            }
        }
        return string;
    }

    public static String cleanArgument(String string) {
        if (string.length() >= 2 && string.startsWith("'") && string.endsWith("'")) {
            return string.substring(1, string.length() - 1);
        } else {
            return string;
        }
    }

    public static TextLengthPair recursiveParsing(String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) {
        if (input.isEmpty()) {
            return new TextLengthPair(new LiteralText(""), 0);
        }

        MutableText text = null;

        Matcher matcher = STARTING_PATTERN.matcher(input);
        Matcher matcherEnd = endAt != null ? Pattern.compile(endAt).matcher(input) : null;
        int currentPos = 0;
        int offset = 0;
        boolean hasEndTag = endAt != null && matcherEnd.find();
        int currentEnd = hasEndTag ? matcherEnd.start() : input.length();

        while (matcher.find()) {
            if (currentEnd <= matcher.start()) {
                break;
            }

            String[] entireTag = (matcher.group("id") + matcher.group("data")).split(":", 2);
            String tag = entireTag[0].toLowerCase(Locale.ROOT);
            String data = "";
            if (entireTag.length == 2) {
                data = entireTag[1];
            }

            // Special reset handling for <reset> tag
            if (tag.equals("reset") || tag.equals("r")) {
                if (endAt != null) {
                    currentEnd = matcher.start();
                    if (currentPos < currentEnd) {
                        String restOfText = removeEscaping(input.substring(currentPos, currentEnd));
                        if (restOfText.length() != 0) {
                            if (text == null) {
                                text = new LiteralText(restOfText);
                            } else {
                                text.append(restOfText);
                            }
                        }
                    }

                    return new TextLengthPair(text, currentEnd);
                } else {
                    String betweenText = input.substring(currentPos, matcher.start());

                    if (betweenText.length() != 0) {
                        if (text == null) {
                            text = new LiteralText(removeEscaping(betweenText));
                        } else {
                            text.append(removeEscaping(betweenText));
                        }
                    }
                    currentPos = matcher.end();
                }
            } else {

                if (tag.startsWith("#")) {
                    data = tag;
                    tag = "color";
                }

                String end = "</" + tag + ">";

                TextParser.TextFormatterHandler handler = handlers.get(tag);
                if (handler != null) {
                    String betweenText = input.substring(currentPos, matcher.start());

                    if (betweenText.length() != 0) {
                        if (text == null) {
                            text = new LiteralText(removeEscaping(betweenText));
                        } else {
                            text.append(removeEscaping(betweenText));
                        }
                    }
                    currentPos = matcher.end();
                    try {
                        TextLengthPair pair = handler.parse(tag, data, input.substring(currentPos), handlers, end);
                        if (pair.text() != null) {
                            if (text == null) {
                                text = new LiteralText("");
                            }
                            text.append(pair.text());
                        }
                        currentPos += pair.length();

                        if (currentPos >= input.length()) {
                            currentEnd = input.length();
                            break;
                        }
                        matcher.region(currentPos, input.length());
                        if (matcherEnd != null) {
                            matcherEnd.region(currentPos, input.length());
                            if (matcherEnd.find()) {
                                hasEndTag = true;
                                currentEnd = matcherEnd.start();
                            } else {
                                hasEndTag = false;
                                currentEnd = input.length();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (currentPos < currentEnd) {
            String restOfText = removeEscaping(input.substring(currentPos, currentEnd));
            if (restOfText.length() != 0) {
                if (text == null) {
                    text = new LiteralText(restOfText);
                } else {
                    text.append(restOfText);
                }
            }
        }

        if (hasEndTag) {
            currentEnd += endAt.length();
        } else {
            currentEnd = input.length();
        }
        return new TextLengthPair(text, currentEnd);
    }

    public static void register() {
        if (IS_REGISTERED) {
            return;
        } else {
            IS_REGISTERED = true;
        }

        {
            for (Formatting formatting : Formatting.values()) {
                TextParser.register(formatting.getName(), (tag, data, input, handlers, endAt) -> {
                    TextLengthPair out = recursiveParsing(input, handlers, endAt);
                    out.text().formatted(formatting);
                    return out;
                });
            }

            var reg = TextParser.getRegisteredTags();

            TextParser.register("orange", reg.get("gold"));
            TextParser.register("grey", reg.get("gray"));
            TextParser.register("dark_grey", reg.get("dark_gray"));
            TextParser.register("st", reg.get("strikethrough"));
            TextParser.register("obf", reg.get("obfuscated"));
            TextParser.register("em", reg.get("italic"));
            TextParser.register("i", reg.get("italic"));
            TextParser.register("b", reg.get("bold"));
            TextParser.register("underlined", reg.get("underline"));
        }

        {
            TextParser.TextFormatterHandler color = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                out.text().fillStyle(Style.EMPTY.withColor(TextColor.parse(cleanArgument(data))));
                return out;
            };

            TextParser.register("color", color);
            TextParser.register("colour", color);
            TextParser.register("c", color);
        }

        TextParser.register("font", (tag, data, input, handlers, endAt) -> {
            TextLengthPair out = recursiveParsing(input, handlers, endAt);
            out.text().fillStyle(Style.EMPTY.withFont(Identifier.tryParse(cleanArgument(data))));
            return out;
        });

        TextParser.register("lang", (tag, data, input, handlers, endAt) -> {
            String[] lines = data.split(":");
            if (lines.length > 0) {
                List<Text> textList = new ArrayList<>();
                boolean skipped = false;
                for (String part : lines) {
                    if (!skipped) {
                        skipped = true;
                        continue;
                    }
                    textList.add(parse(removeEscaping(cleanArgument(part)), handlers));
                }

                MutableText out = new TranslatableText(cleanArgument(lines[0]), textList.toArray());
                return new TextLengthPair(out, 0);
            }
            return TextLengthPair.EMPTY;
        });

        TextParser.register("key", (tag, data, input, handlers, endAt) -> {
            if (!data.isEmpty()) {
                MutableText out = new KeybindText(cleanArgument(data));
                return new TextLengthPair(out, 0);
            }
            return TextLengthPair.EMPTY;
        });

        TextParser.register("click", (tag, data, input, handlers, endAt) -> {
            String[] lines = data.split(":", 2);
            TextLengthPair out = recursiveParsing(input, handlers, endAt);
            if (lines.length > 1) {
                ClickEvent.Action action = ClickEvent.Action.byName(cleanArgument(lines[0]));
                if (action != null) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(action, removeEscaping(cleanArgument(lines[1])))));
                }
            }
            return out;
        }, false);

        {
            TextParser.TextFormatterHandler formatter = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                if (!data.isEmpty()) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, removeEscaping(cleanArgument(data)))));
                }
                return out;
            };

            TextParser.register("run_command", formatter, false);
            TextParser.register("run_cmd", formatter, false);
        }

        {
            TextParser.TextFormatterHandler formatter = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                if (!data.isEmpty()) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, removeEscaping(cleanArgument(data)))));
                }
                return out;
            };

            TextParser.register("suggest_command", formatter, false);
            TextParser.register("cmd", formatter, false);
        }

        {
            TextParser.TextFormatterHandler formatter = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                if (!data.isEmpty()) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, removeEscaping(cleanArgument(data)))));
                }
                return out;
            };

            TextParser.register("open_url", formatter, false);
            TextParser.register("url", formatter, false);
        }

        {
            TextParser.TextFormatterHandler formatter = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                if (!data.isEmpty()) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, removeEscaping(cleanArgument(data)))));
                }
                return out;
            };

            TextParser.register("copy_to_clipboard", formatter, false);
            TextParser.register("copy", formatter, false);
        }

        {
            TextParser.TextFormatterHandler formatter = (tag, data, input, handlers, endAt) -> {
                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                if (!data.isEmpty()) {
                    out.text().setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, removeEscaping(cleanArgument(data)))));
                }
                return out;
            };

            TextParser.register("change_page", formatter);
            TextParser.register("page", formatter);
        }

        TextParser.register("hover", (tag, data, input, handlers, endAt) -> {
            String[] lines = data.split(":", 2);
            TextLengthPair out = recursiveParsing(input, handlers, endAt);

            try {
                if (lines.length > 1) {
                    HoverEvent.Action<?> action = HoverEvent.Action.byName(cleanArgument(lines[0].toLowerCase(Locale.ROOT)));

                    if (action == HoverEvent.Action.SHOW_TEXT) {
                        out.text().setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, parse(removeEscaping(cleanArgument(lines[1])), handlers))));
                    } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                        lines = lines[1].split(":", 3);
                        if (lines.length == 3) {
                            out.text().setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
                                    new HoverEvent.EntityContent(
                                            EntityType.get(removeEscaping(removeEscaping(cleanArgument(lines[0])))).orElse(EntityType.PIG),
                                            UUID.fromString(cleanArgument(lines[1])),
                                            parse(removeEscaping(removeEscaping(cleanArgument(lines[2]))), handlers))
                            )));
                        }
                    } else if (action == HoverEvent.Action.SHOW_ITEM) {
                        out.text().setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackContent(ItemStack.fromNbt(StringNbtReader.parse(removeEscaping(cleanArgument(lines[1])))))
                        )));
                    } else {
                        out.text().setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, parse(removeEscaping(cleanArgument(data)), handlers))));
                    }
                } else {
                    out.text().setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, parse(removeEscaping(cleanArgument(data)), handlers))));
                }
            } catch (Exception e) {
                // Shut
            }
            return out;
        });

        TextParser.register("insert", (tag, data, input, handlers, endAt) -> {
            TextLengthPair out = recursiveParsing(input, handlers, endAt);
            out.text().setStyle(Style.EMPTY.withInsertion(removeEscaping(cleanArgument(data))));
            return out;
        }, false);

        {
            TextParser.TextFormatterHandler rainbow = (tag, data, input, handlers, endAt) -> {
                String[] val = data.split(":");
                float freq = 1;
                float saturation = 1;
                float offset = 0;

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

                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                String flatString = GeneralUtils.textToString(out.text());

                final float finalFreq = freq;
                final float finalOffset = offset;
                final float finalSaturation = saturation;

                final int length = flatString.replaceAll("\\p{So}|.", "$0\0").split("\0+").length;

                return new TextLengthPair(
                        GeneralUtils.toGradient(out.text(), (pos) -> TextColor.fromRgb(GeneralUtils.hvsToRgb(((pos * finalFreq) / (length + 1) + finalOffset) % 1, finalSaturation, 1))),
                        out.length());
            };

            TextParser.register("rainbow", rainbow);
            TextParser.register("rb", rainbow);
        }

        {
            TextParser.TextFormatterHandler gradient = (tag, data, input, handlers, endAt) -> {
                String[] val = data.split(":");

                TextLengthPair out = recursiveParsing(input, handlers, endAt);
                String flatString = GeneralUtils.textToString(out.text());
                List<TextColor> textColors = new ArrayList<>();
                for (String string : val) {
                    TextColor color = TextColor.parse(string);
                    if (color != null) {
                        textColors.add(color);
                    }
                }
                if (textColors.size() == 0) {
                    textColors.add(TextColor.fromFormatting(Formatting.WHITE));
                    textColors.add(TextColor.fromFormatting(Formatting.WHITE));
                } else if (textColors.size() == 1) {
                    textColors.add(textColors.get(0));
                }

                final int length = flatString.replaceAll("\\p{So}|.", "$0\0").split("\0+").length;

                final double step = ((double) textColors.size() - 1) / length;
                final float sectionSize = ((float) textColors.size() - 1) / (length + 1);

                GeneralUtils.HSV hsv = GeneralUtils.rgbToHsv(textColors.get(0).getRgb());
                AtomicDouble hue = new AtomicDouble(hsv.h());
                AtomicDouble saturation = new AtomicDouble(hsv.s());
                AtomicDouble value = new AtomicDouble(hsv.v());


                return new TextLengthPair(GeneralUtils.toGradient(out.text(), (pos) -> {
                    GeneralUtils.HSV colorA = GeneralUtils.rgbToHsv(textColors.get((int) (pos * sectionSize)).getRgb());
                    GeneralUtils.HSV colorB = GeneralUtils.rgbToHsv(textColors.get((int) (pos * sectionSize) + 1).getRgb());

                    float localHue = (float) hue.get();
                    {
                        float h = colorB.h() - colorA.h();
                        float delta = (h + ((Math.abs(h) > 0.50001) ? ((h < 0) ? 1 : -1) : 0));

                        float futureHue = (float) (localHue + delta * step);
                        if (futureHue < 0) {
                            futureHue += 1;
                        } else if (futureHue > 1) {
                            futureHue -= 1;
                        }
                        hue.set(futureHue);
                    }

                    float localSat = (float) saturation.get();
                    {
                        float s = colorB.s() - colorA.s();
                        float futureSat = MathHelper.clamp((float) (localSat + s * step), 0, 1);
                        saturation.set(futureSat);
                    }

                    float localVal = (float) value.get();
                    {
                        float v = colorB.v() - colorA.v();
                        float futureVal = MathHelper.clamp((float) (localVal + v * step), 0, 1);
                        value.set(futureVal);
                    }

                    return TextColor.fromRgb(GeneralUtils.hvsToRgb(
                            localHue,
                            localSat,
                            localVal));
                }), out.length());
            };

            TextParser.register("gradient", gradient);
            TextParser.register("gr", gradient);
        }

        TextParser.register("style", (tag, data, input, handlers, endAt) -> {
            TextLengthPair out = recursiveParsing(input, handlers, endAt);
            out.text().setStyle(GSON.fromJson(removeEscaping(cleanArgument(data)), Style.class));
            return out;
        }, false);

        TextParser.register("raw", (tag, data, input, handlers, endAt) -> new TextLengthPair(Text.Serializer.fromLenientJson(removeEscaping(cleanArgument(data))), 0), false);

        TextParser.register("score", (tag, data, input, handlers, endAt) -> {
            String[] lines = data.split(":");
            if (lines.length == 2) {
                MutableText out = new ScoreText(removeEscaping(cleanArgument(lines[0])), removeEscaping(cleanArgument(lines[1])));
                return new TextLengthPair(out, 0);
            }
            return TextLengthPair.EMPTY;
        }, false);

        ESCAPED_CHARS.add(new Pair<>("\\\\", "&slsh;"));
        ESCAPED_CHARS.add(new Pair<>("\\<", "&lt;"));
        ESCAPED_CHARS.add(new Pair<>("\\>", "&gt;"));
        ESCAPED_CHARS.add(new Pair<>("\\\"", "&quot;"));
        ESCAPED_CHARS.add(new Pair<>("\\'", "&pos;"));
        ESCAPED_CHARS.add(new Pair<>("\\:", "&colon;"));

        UNESCAPED_CHARS.add(new Pair<>("\\", "&slsh;"));
        UNESCAPED_CHARS.add(new Pair<>("<", "&lt;"));
        UNESCAPED_CHARS.add(new Pair<>(">", "&gt;"));
        UNESCAPED_CHARS.add(new Pair<>("\"", "&quot;"));
        UNESCAPED_CHARS.add(new Pair<>("'", "&pos;"));
        UNESCAPED_CHARS.add(new Pair<>(":", "&colon;"));
    }

    // Cursed don't touch this
    @ApiStatus.Experimental
    @UnstableApi
    public static String convertToString(Text text) {
        StringBuilder builder = new StringBuilder();
        String style = GSON.toJson(text.getStyle());
        if (style != null && !style.equals("null")) {
            builder.append("<style:").append(style).append(">");
        }
        if (text instanceof LiteralText literalText) {
            builder.append(escapeCharacters(literalText.asString()));
        } else if (text instanceof TranslatableText translatableText) {
            List<String> stringList = new ArrayList<>();

            for (Object arg : translatableText.getArgs()) {
                if (arg instanceof Text text1) {
                    stringList.add("'" + escapeCharacters(convertToString(text1)) + "'");
                } else {
                    stringList.add("'" + escapeCharacters(arg.toString()) + "'");
                }
            }

            if (stringList.size() > 0) {
                stringList.add(0, "");
            }

            String additional = String.join(":", stringList);

            builder.append("<lang:'").append(translatableText.getKey()).append("'").append(additional).append(">");
        } else if (text instanceof KeybindText keybindText) {
            builder.append("<key:'").append(keybindText.getKey()).append("'>");
        } else {
            builder.append("<raw:'").append(escapeCharacters(Text.Serializer.toJson(text.copy()))).append("'>");
        }

        for (Text text1 : text.getSiblings()) {
            builder.append(convertToString(text1));
        }

        if (style != null && !style.equals("null")) {
            builder.append("</style>");
        }
        return builder.toString();
    }

}