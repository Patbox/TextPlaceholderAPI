package eu.pb4.placeholders;

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
        this.string = Helpers.textToString(this.text);
    }

    public Text getText() {
        return this.text;
    }

    public String getString() {
        return this.string;
    }

    public boolean isValid() {
        return this.valid;
    }

    public static PlaceholderResult invalid(String reason) {
        return new PlaceholderResult(null, reason);
    }

    public static PlaceholderResult invalid() {
        return new PlaceholderResult(null, null);
    }

    public static PlaceholderResult value(Text text) {
        return new PlaceholderResult(text, null);
    }

    public static PlaceholderResult value(String text) {
        return new PlaceholderResult(new LiteralText(text), null);
    }
}


