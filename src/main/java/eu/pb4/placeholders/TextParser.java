package eu.pb4.placeholders;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.placeholders.util.TextParserUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public final class TextParser {
    private static final HashMap<String, TextFormatterHandler> TAGS = new HashMap<>();

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
    public static void register(String identifier, TextFormatterHandler handler) {
        TAGS.put(identifier, handler);
    }

    /**
     * Returns map of registered tags
     */
    public static ImmutableMap<String, TextFormatterHandler> getRegisteredTags() {
        return ImmutableMap.copyOf(TAGS);
    }

    @FunctionalInterface
    public interface TextFormatterHandler {
        GeneralUtils.TextLengthPair parse(String tag, String data, String input, Map<String, TextFormatterHandler> handlers, String endAt);
    }
}
