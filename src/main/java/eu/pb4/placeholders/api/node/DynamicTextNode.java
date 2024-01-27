package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record DynamicTextNode(String id, ParserContext.Key<Function<String, Text>> key) implements TextNode {
    public static DynamicTextNode of(String id, ParserContext.Key<Function<String, Text>> key) {
        return new DynamicTextNode(id, key);
    }

    public static ParserContext.Key<Function<String, @Nullable Text>> key(String id) {
        return new ParserContext.Key<>("dynamic:" + id, null);
    }

    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        var x = context.get(key);
        if (x != null) {
            return x.apply(id);
        }
        return Text.literal("[MISSING CONTEXT FOR " + this.key.key() + "]").formatted(Formatting.ITALIC).withColor(0xFF0000);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
