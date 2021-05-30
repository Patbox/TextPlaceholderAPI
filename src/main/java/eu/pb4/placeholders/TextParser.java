package eu.pb4.placeholders;

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
     * Registers new text element
     */
    public static void register(String identifier, TextFormatterHandler handler) {
        TAGS.put(identifier, handler);
    }

    @FunctionalInterface
    public interface TextFormatterHandler {
        int parse(String tag, String data, MutableText text, String input, Map<String, TextFormatterHandler> handlers, String endAt);
    }
}
