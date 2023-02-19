package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.textparser.TextParserImpl;
import net.minecraft.text.Text;

public record LiteralNode(String value) implements TextNode {

    public LiteralNode(StringBuilder builder) {
        this(builder.toString());
    }
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        if (this.value.isEmpty()) {
            return Text.empty();
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

            return Text.literal(builder.toString());
        } else {
            return Text.literal(this.value());
        }
    }
}
