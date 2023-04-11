package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record TranslatedNode(String key, @Nullable String fallback, Object[] args) implements TextNode {
    @Deprecated
    public TranslatedNode(String key, Object[] args) {
        this(key, null, new Object[0]);
    }


    public static TranslatedNode of(String key, Object... args) {
        return new TranslatedNode(key, null, args);
    }

    public static TranslatedNode ofFallback(String key, @Nullable String fallback, Object... args) {
        return new TranslatedNode(key, fallback, args);
    }

    public TranslatedNode(String key) {
        this(key, new Object[0]);
    }

    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        var args = new Object[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            args[i] = this.args[i] instanceof TextNode textNode ? textNode.toText(context, removeBackslashes) : this.args[i];
        }

        if (GeneralUtils.IS_LEGACY_TRANSLATION) {
            return Text.translatable(this.key, args);
        } else {
            return Text.translatableWithFallback(this.key(), this.fallback, args);
        }
    }

    @Override
    public boolean isDynamic() {
        for (var obj : args) {
            if (obj instanceof TextNode t && t.isDynamic()) {
                return true;
            }
        }

        return false;
    }
}
