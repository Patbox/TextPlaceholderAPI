package eu.pb4.placeholders;

import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.placeholders.util.PlaceholderUtils;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public final class PlaceholderResult {
    private final Text text;
    private final String string;
    private final boolean valid;

    private PlaceholderResult(Text text, String reason) {
        if (text != null) {
            this.text = text;
            this.valid = true;
        } else {
            this.text = new LiteralText(reason != null ? reason : "Invalid!");
            this.valid = false;
        }
        this.string = GeneralUtils.textToString(this.text);
    }

    /**
     * Returns text component from placeholder
     *
     * @return Text
     */
    public Text getText() {
        return this.text;
    }

    /**
     * Returns text component as String (without formatting) from placeholder
     *
     * @return String
     */
    public String getString() {
        return this.string;
    }

    /**
     * Checks if placeholder was valid
     *
     * @return boolean
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Create result for invalid placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult invalid(String reason) {
        return new PlaceholderResult(null, reason);
    }

    /**
     * Create result for invalid placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult invalid() {
        return new PlaceholderResult(null, null);
    }

    /**
     * Create result for placeholder with formatting
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(Text text) {
        return new PlaceholderResult(text, null);
    }

    /**
     * Create result for placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(String text) {
        return new PlaceholderResult(TextParser.parse(text), null);
    }
}


