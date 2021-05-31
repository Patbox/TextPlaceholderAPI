package eu.pb4.placeholders.util;

import com.google.common.util.concurrent.AtomicDouble;
import eu.pb4.placeholders.TextParser;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParserUtils {
    //public static final Pattern STARTING_PATTERN = Pattern.compile("<(?<id>[^>]+)>");
    //public static final Pattern STARTING_PATTERN = Pattern.compile("<(?<id>[^>\\/]+)>");

    // Based on minimessage's regex
    public static final Pattern STARTING_PATTERN = Pattern.compile("<(?<id>[^<>\\/]+)(?<data>(:(['\\\"]?([^'\\\"](\\\\\\\\['\\\"])?)+['\\\"]?))*)>");

    public static final HashMap<String, String> ESCAPED_CHARS = new HashMap();
    public static final HashMap<String, String> UNESCAPED_CHARS = new HashMap();

    public static Text parse(String string, Map<String, TextParser.TextFormatterHandler> handlers) {
        LiteralText text = new LiteralText("");
        recursiveParsing(text, escapeCharacters(string), handlers, null);

        return text;
    }

    public static String escapeCharacters(String string) {
        for (Map.Entry<String, String> entry : ESCAPED_CHARS.entrySet()) {
            string = string.replaceAll(Matcher.quoteReplacement(entry.getKey()), entry.getValue());
        }
        return string;
    }

    public static String removeEscaping(String string) {
        for (Map.Entry<String, String> entry : UNESCAPED_CHARS.entrySet()) {
            try {
                string = string.replaceAll(entry.getValue(), entry.getKey());
            } catch (Exception e) {
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

    public static int recursiveParsing(MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) {
        if (input.isEmpty()) {
            return 0;
        }

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

            String betweenText = input.substring(currentPos, matcher.start());

            if (betweenText.length() != 0) {
                text.append(removeEscaping(betweenText));
            }

            String[] entireTag = (matcher.group("id").toLowerCase(Locale.ROOT) + matcher.group("data")).split(":", 2);
            String tag = entireTag[0];
            String data = "";
            if (entireTag.length == 2) {
                data = entireTag[1];
            }
            String end = "</" + tag + ">";

            TextParser.TextFormatterHandler handler = handlers.get(tag);
            if (handler != null) {
                currentPos = matcher.end();
                try {
                    int toIgnore = handler.parse(tag, data, text, input.substring(currentPos), handlers, end);
                    currentPos += toIgnore;

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

        if (currentPos < currentEnd) {
            String restOfText = input.substring(currentPos, currentEnd);
            if (restOfText.length() != 0) {
                text.append(removeEscaping(restOfText));
            }
        }

        if (hasEndTag) {
            currentEnd += endAt.length();
        } else {
            currentEnd = input.length();
        }

        return currentEnd;
    }

    public static void register() {
        for (Formatting formatting : Formatting.values()) {
            TextParser.register(formatting.getName(), (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
                MutableText out = new LiteralText("").formatted(formatting);
                text.append(out);
                return recursiveParsing(out, input, handlers, endAt);
            });
        }

        {
            TextParser.TextFormatterHandler color = (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
                MutableText out = new LiteralText("").fillStyle(Style.EMPTY.withColor(TextColor.parse(cleanArgument(data))));
                text.append(out);
                return recursiveParsing(out, input, handlers, endAt);
            };

            TextParser.register("color", color);
            TextParser.register("c", color);
        }

        TextParser.register("font", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
            MutableText out = new LiteralText("").fillStyle(Style.EMPTY.withFont(Identifier.tryParse(cleanArgument(data))));
            text.append(out);
            return recursiveParsing(out, input, handlers, endAt);
        });

        TextParser.register("lang", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
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
                text.append(out);
            }
            return 0;
        });

        TextParser.register("key", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
            if (!data.isEmpty()) {
                MutableText out = new KeybindText(cleanArgument(data));
                text.append(out);
            }
            return 0;
        });

        TextParser.register("click", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
            String[] lines = data.split(":", 2);
            MutableText out = new LiteralText("");
            text.append(out);
            if (lines.length > 1) {
                ClickEvent.Action action = ClickEvent.Action.byName(cleanArgument(lines[0]));
                if (action != null) {
                    out.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(action, cleanArgument(lines[1]))));
                }
            }
            return recursiveParsing(out, input, handlers, endAt);
        });

        TextParser.register("hover", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
            String[] lines = data.split(":", 2);
            MutableText out = new LiteralText("");
            if (lines.length > 1) {
                HoverEvent.Action action = HoverEvent.Action.byName(cleanArgument(lines[0]));
                if (action == HoverEvent.Action.SHOW_TEXT) {
                    out.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(action, parse(removeEscaping(cleanArgument(lines[1])), handlers))));
                }
            }
            text.append(out);
            return recursiveParsing(out, input, handlers, endAt);
        });

        TextParser.register("insert", (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
            MutableText out = new LiteralText("");
            out.setStyle(Style.EMPTY.withInsertion(cleanArgument(data)));
            text.append(out);
            return recursiveParsing(out, input, handlers, endAt);
        });

        {
            TextParser.TextFormatterHandler rainbow = (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
                MutableText out = new LiteralText("");
                String[] val = data.split(":");
                float freq = 1;
                float saturation = 1;
                float offset = 0;

                if (val.length >= 1) {
                    try {
                        freq = Float.parseFloat(val[0]);
                    } catch (Exception e) {
                    }
                }
                if (val.length >= 2) {
                    try {
                        saturation = Float.parseFloat(val[1]);
                    } catch (Exception e) {
                    }
                }
                if (val.length >= 3) {
                    try {
                        offset = Float.parseFloat(val[2]);
                    } catch (Exception e) {
                    }
                }

                int toIgnore = recursiveParsing(out, input, handlers, endAt);
                String flatString = GeneralUtils.textToString(out);

                final float finalFreq = freq;
                final float finalOffset = offset;
                final float finalSaturation = saturation;

                text.append(GeneralUtils.toGradient(out, (pos) -> TextColor.fromRgb(GeneralUtils.hvsToRgb(((pos * finalFreq) / (flatString.length() + 1) + finalOffset) % 1, finalSaturation, 1))));

                return toIgnore;
            };

            TextParser.register("rainbow", rainbow);
            TextParser.register("rb", rainbow);
        }

        {
            TextParser.TextFormatterHandler gradient = (String tag, String data, MutableText text, String input, Map<String, TextParser.TextFormatterHandler> handlers, String endAt) -> {
                MutableText out = new LiteralText("");
                String[] val = data.split(":");

                int toIgnore = recursiveParsing(out, input, handlers, endAt);
                String flatString = GeneralUtils.textToString(out);
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

                final double step = ((double) textColors.size() - 1) / flatString.length();
                final float sectionSize = ((float) textColors.size() - 1) / (flatString.length() + 1);

                GeneralUtils.HSV hsv = GeneralUtils.rgbToHsv(textColors.get(0).getRgb());
                AtomicDouble hue = new AtomicDouble(hsv.h());
                AtomicDouble saturation = new AtomicDouble(hsv.s());
                AtomicDouble value = new AtomicDouble(hsv.v());

                text.append(GeneralUtils.toGradient(out, (pos) -> {
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
                }));

                return toIgnore;
            };

            TextParser.register("gradient", gradient);
            TextParser.register("gr", gradient);
        }

        ESCAPED_CHARS.put("\\\\", "&slsh;");
        ESCAPED_CHARS.put("\\<", "&lt;");
        ESCAPED_CHARS.put("\\>", "&gt;");
        ESCAPED_CHARS.put("\\\"", "&quot;");
        ESCAPED_CHARS.put("\\'", "&pos;");
        ESCAPED_CHARS.put("\\:", "&colon;");

        UNESCAPED_CHARS.put("\\", "&slsh;");
        UNESCAPED_CHARS.put("<", "&lt;");
        UNESCAPED_CHARS.put(">", "&gt;");
        UNESCAPED_CHARS.put("\"", "&quot;");
        UNESCAPED_CHARS.put("'", "&pos;");
        UNESCAPED_CHARS.put(":", "&colon;");
    }

}