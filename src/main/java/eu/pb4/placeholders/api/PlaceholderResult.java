package eu.pb4.placeholders.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public final class PlaceholderResult {
    private final Component component;
    private final boolean valid;

    private PlaceholderResult(Component text, String reason) {
        if (text != null) {
            this.component = text;
            this.valid = true;
        } else {
            this.component = Component.literal("[" + (reason != null ? reason : "Invalid placeholder!") + "]").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true));
            this.valid = false;
        }
    }

    /**
     * Returns component component from placeholder
     *
     * @return Text
     */
    public Component component() {
        return this.component;
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
    public static PlaceholderResult value(Component component) {
        return new PlaceholderResult(component, null);
    }

    /**
     * Create result for placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(String component) {
        return new PlaceholderResult(Component.literal(component), null);
    }
}


