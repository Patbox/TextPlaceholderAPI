package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.network.chat.Component;

public record LiteralNode(String value) implements TextNode {

    public LiteralNode(StringBuilder builder) {
        this(builder.toString());
    }
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        if (this.value.isEmpty()) {
            return Component.empty();
        }

        if (removeBackslashes) {
            var builder = new StringBuilder();

            var length = this.value.length();
            for (var i = 0; i < length; i++) {
                var c = this.value.charAt(i);

                if (c == '\\' && i + 1 < length) {
                    var n = this.value.charAt(i + 1);
                    if (Character.isWhitespace(n) || Character.isLetterOrDigit(n)) {
                        builder.append(c);
                    } else {
                        builder.append(n);
                        i++;
                    }
                } else {
                    builder.append(c);
                }
            }

            return Component.literal(builder.toString());
        } else {
            return Component.literal(this.value());
        }
    }
}
