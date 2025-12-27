package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import net.minecraft.network.chat.Component;

public record TranslatedNode(String key, @Nullable String fallback, Object[] args) implements TextNode {
    public static TranslatedNode of(String key, Object... args) {
        return new TranslatedNode(key, null, args);
    }

    public static TranslatedNode ofFallback(String key, @Nullable String fallback, Object... args) {
        return new TranslatedNode(key, fallback, args);
    }

    public TranslatedNode(String key) {
        this(key, null, new Object[0]);
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var args = new Object[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            args[i] = this.args[i] instanceof TextNode textNode ? textNode.toComponent(context, removeBackslashes) : this.args[i];
        }


        return Component.translatableWithFallback(this.key(), this.fallback, args);
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

    public TextNode transform(NodeParser parser) {
        if (this.args.length == 0) {
            return this;
        }

        var list = new ArrayList<>();
        for (var arg : this.args()) {
            if (arg instanceof TextNode textNode) {
                list.add(parser.parseNode(textNode));
            } else {
                list.add(arg);
            }
        }
        return TranslatedNode.ofFallback(this.key(), this.fallback(), list.toArray());
    }
}
