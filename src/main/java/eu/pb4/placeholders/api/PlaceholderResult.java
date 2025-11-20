package eu.pb4.placeholders.api;

import eu.pb4.placeholders.api.parsers.TextParserV1;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public final class PlaceholderResult {
    private final Component text;
    private String string;
    private final boolean valid;

    private PlaceholderResult(Component text, String reason) {
        if (text != null) {
            this.text = text;
            this.valid = true;
        } else {
            this.text = Component.literal("[" + (reason != null ? reason : "Invalid placeholder!") + "]").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true));
            this.valid = false;
        }
    }

    /**
     * Returns text component from placeholder
     *
     * @return Text
     */
    public Component text() {
        return this.text;
    }

    /**
     * Returns text component as String (without formatting) from placeholder
     * It's not recommended for general usage, as it makes it text static/unable to change depending on player's language or other settings
     * and removes all styling.
     *
     * @return String
     */
    @Deprecated
    public String string() {
        if (this.string == null) {
            this.string = this.text.getString();
        }
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
    public static PlaceholderResult value(Component text) {
        return new PlaceholderResult(text, null);
    }

    /**
     * Create result for placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(String text) {
        return new PlaceholderResult(TextParserV1.DEFAULT.parseText(text, null), null);
    }
}


