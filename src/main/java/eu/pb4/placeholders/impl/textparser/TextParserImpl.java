package eu.pb4.placeholders.impl.textparser;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import net.minecraft.text.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.pb4.placeholders.impl.GeneralUtils.Pair;


@Deprecated
@ApiStatus.Internal
public class TextParserImpl {
    // Based on minimessage's regex, modified to fit more parsers needs
    public static final Pattern STARTING_PATTERN = Pattern.compile("<(?<id>[^<>/]+)(?<data>([:]([']?([^'](\\\\\\\\['])?)+[']?))*)>");
    @Deprecated
    public static final List<Pair<String, String>> ESCAPED_CHARS = new ArrayList<>();

    static {
        ESCAPED_CHARS.add(new Pair<>("\\", "&slsh;\002"));
        ESCAPED_CHARS.add(new Pair<>("<", "&lt;\002"));
        ESCAPED_CHARS.add(new Pair<>(">", "&gt;\002"));
        ESCAPED_CHARS.add(new Pair<>("\"", "&quot;\002"));
        ESCAPED_CHARS.add(new Pair<>("'", "&pos;\002"));
        ESCAPED_CHARS.add(new Pair<>(":", "&colon;\002"));
        ESCAPED_CHARS.add(new Pair<>("&", "&amps;\002"));
        ESCAPED_CHARS.add(new Pair<>("{", "&openbrac;\002"));
        ESCAPED_CHARS.add(new Pair<>("}", "&closebrac;\002"));
        ESCAPED_CHARS.add(new Pair<>("$", "&dolar;\002"));
        ESCAPED_CHARS.add(new Pair<>("%", "&perc;\002"));
    }

    public static TextNode[] parse(String string, TextParserV1.TagParserGetter handlers) {
        return recursiveParsing(escapeCharacters(string), handlers, null).nodes();
    }

    public static String escapeCharacters(String string) {
        for (Pair<String, String> entry : ESCAPED_CHARS) {
            string = string.replace("\\" + entry.left(), entry.right());
        }
        return string;
    }

    public static String removeEscaping(String string) {
        for (var entry : ESCAPED_CHARS) {
            try {
                string = string.replace(entry.right(), entry.left());
            } catch (Exception e) {
                // Silence!
            }
        }
        return string;
    }

    public static String restoreOriginalEscaping(String string) {
        for (var entry : ESCAPED_CHARS) {
            try {
                string = string.replace(entry.right(), "\\" + entry.left());
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

    public static TextParserV1.NodeList recursiveParsing(String input, TextParserV1.TagParserGetter handlers, String endAt) {
        if (input.isEmpty()) {
            return new TextParserV1.NodeList(new TextNode[0], 0);
        }

        var text = new ArrayList<TextNode>();

        Matcher matcher = STARTING_PATTERN.matcher(input);
        Matcher matcherEnd = endAt != null ? Pattern.compile("(" + endAt + ")|(</>)").matcher(input) : null;
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
                        String restOfText = restoreOriginalEscaping(input.substring(currentPos, currentEnd));
                        if (!restOfText.isEmpty()) {
                            text.add(new LiteralNode(restOfText));
                        }
                    }

                    return new TextParserV1.NodeList(text.toArray(new TextNode[0]), currentEnd);
                } else {
                    String betweenText = input.substring(currentPos, matcher.start());

                    if (!betweenText.isEmpty()) {
                        text.add(new LiteralNode(restoreOriginalEscaping(betweenText)));
                    }
                    currentPos = matcher.end();
                }
            } else {

                if (tag.startsWith("#")) {
                    data = tag;
                    tag = "color";
                }

                String end = "</" + tag + ">";

                var handler = handlers.getTagParser(tag);
                if (handler != null) {
                    String betweenText = input.substring(currentPos, matcher.start());

                    if (!betweenText.isEmpty()) {
                        text.add(new LiteralNode(restoreOriginalEscaping(betweenText)));

                    }
                    currentPos = matcher.end();
                    try {
                        var pair = handler.parseString(tag, data, input.substring(currentPos), handlers, end);
                        if (pair.node() != null) {
                            text.add(pair.node());
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
            String restOfText = restoreOriginalEscaping(input.substring(currentPos, currentEnd));
            if (!restOfText.isEmpty()) {
                text.add(new LiteralNode(restOfText));
            }
        }

        if (hasEndTag) {
            currentEnd += matcherEnd.group().length();
        } else {
            currentEnd = input.length();
        }
        return new TextParserV1.NodeList(text.toArray(new TextNode[0]), currentEnd);
    }

    public static final TextNode[] CASTER = new TextNode[0];
}