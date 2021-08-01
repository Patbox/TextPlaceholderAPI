package eu.pb4.placeholders;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.placeholders.util.TextParserUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public final class TextParser {
    private static final HashMap<String, TextFormatterHandler> TAGS = new HashMap<>();
    private static final HashMap<String, TextFormatterHandler> SAFE_TAGS = new HashMap<>();

    /**
     * You can use this codec while reading from files
     * It still has limited support for writing, but it won't work correctly
     * If you need to write text somewhere, it will be better to use it directly
     */
    public static final Codec<Text> CODEC = Codec.STRING.comapFlatMap((s) -> DataResult.success(TextParser.parse(s)), TextParserUtils::convertToString);

    /**
     * Parses input string and outputs corresponding Text
     *
     * @param input with formatting
     * @return Text
     */
    public static Text parse(String input) {
        return TextParserUtils.parse(input, TAGS);
    }

    /**
     * Parses input string and outputs corresponding Text
     * Uses only player input safe tags
     *
     * @param input with formatting
     * @return Text
     */
    public static Text parseSafe(String input) {
        return TextParserUtils.parse(input, SAFE_TAGS);
    }

    /**
     * Parses input string with it's own handler map (ideal for limiting formatting)
     *
     * @param input with formatting
     * @param handlerMap Map of handlers
     * @return Text
     */
    public static Text parse(String input, Map<String, TextFormatterHandler> handlerMap) {
        return TextParserUtils.parse(input, handlerMap);
    }

    /**
     * Registers new text tag handler
     */
    @ApiStatus.Internal
    public static void register(String identifier, TextFormatterHandler handler) {
        register(identifier, handler, true);
    }

    /**
     * Registers new text tag handler
     */
    @ApiStatus.Internal
    public static void register(String identifier, TextFormatterHandler handler, boolean safe) {
        if (safe) {
            SAFE_TAGS.put(identifier, handler);
        }
        TAGS.put(identifier, handler);
    }

    /**
     * Returns map of registered tags
     */
    public static ImmutableMap<String, TextFormatterHandler> getRegisteredTags() {
        return ImmutableMap.copyOf(TAGS);
    }

    /**
     * Returns map of registered safe tags, that can be used for custom player input
     */
    public static ImmutableMap<String, TextFormatterHandler> getRegisteredSafeTags() {
        return ImmutableMap.copyOf(SAFE_TAGS);
    }

    @FunctionalInterface
    public interface TextFormatterHandler {
        GeneralUtils.TextLengthPair parse(String tag, String data, String input, Map<String, TextFormatterHandler> handlers, String endAt);
    }
}
