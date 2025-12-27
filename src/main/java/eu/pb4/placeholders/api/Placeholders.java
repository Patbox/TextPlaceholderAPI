package eu.pb4.placeholders.api;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import eu.pb4.placeholders.impl.placeholder.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.ServerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.WorldPlaceholders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Placeholders {
    private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();
    public static final PlaceholderGetter DEFAULT_PLACEHOLDER_GETTER = new PlaceholderGetter() {
        @Override
        public PlaceholderHandler getPlaceholder(String placeholder) {
            return PLACEHOLDERS.get(Identifier.tryParse(placeholder));
        }

        @Override
        public boolean isContextOptional() {
            return false;
        }
    };
    public static final NodeParser DEFAULT_PLACEHOLDER_PARSER = TagLikeParser.placeholder(TagLikeParser.PLACEHOLDER, PlaceholderContext.KEY, DEFAULT_PLACEHOLDER_GETTER);
    private static final List<PlaceholderListChangedCallback> CHANGED_CALLBACKS = new ArrayList<>();

    static {
        PlayerPlaceholders.register();
        ServerPlaceholders.register();
        WorldPlaceholders.register();
    }

    /**
     * Parses PlaceholderContext, can be used for parsing by hand
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult parsePlaceholder(Identifier identifier, String argument, PlaceholderContext context) {
        if (PLACEHOLDERS.containsKey(identifier)) {
            return PLACEHOLDERS.get(identifier).onPlaceholderRequest(context, argument);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }

    /**
     * Parses placeholders in nodes, without getting their final values
     * Placeholders have format of {@code %namespace:placeholder argument%}
     *
     * @return Text
     */
    public static ParentNode parseNodes(TextNode node) {
        return asSingleParent(DEFAULT_PLACEHOLDER_PARSER.parseNodes(node));
    }

    public static ParentNode parseNodes(TextNode node, ParserContext.Key<PlaceholderContext> contextKey) {
        return asSingleParent(TagLikeParser.placeholder(TagLikeParser.PLACEHOLDER, contextKey, DEFAULT_PLACEHOLDER_GETTER).parseNodes(node));
    }

    /**
     * Parses placeholders in text
     * Placeholders have format of {@code %namespace:placeholder argument%}
     *
     * @return Text
     */
    public static Component parseComponent(Component component, PlaceholderContext context) {
        return parseNodes(TextNode.convert(component)).toComponent(ParserContext.of(PlaceholderContext.KEY, context));
    }

    public static Component parseComponent(TextNode textNode, PlaceholderContext context) {
        return parseNodes(textNode).toComponent(ParserContext.of(PlaceholderContext.KEY, context));
    }

    /**
     * Registers new placeholder for identifier
     */
    public static void register(Identifier identifier, PlaceholderHandler handler) {
        PLACEHOLDERS.put(identifier, handler);
        for (var e : CHANGED_CALLBACKS) {
            e.onPlaceholderListChange(identifier, false);
        }
    }

    /**
     * Removes placeholder
     */
    public static void remove(Identifier identifier) {
        if (PLACEHOLDERS.remove(identifier) != null) {
            for (var e : CHANGED_CALLBACKS) {
                e.onPlaceholderListChange(identifier, true);
            }
        }
    }

    public static ImmutableMap<Identifier, PlaceholderHandler> getPlaceholders() {
        return ImmutableMap.copyOf(PLACEHOLDERS);
    }

    public static void registerChangeEvent(PlaceholderListChangedCallback callback) {
        CHANGED_CALLBACKS.add(callback);
    }

    private static ParentNode asSingleParent(TextNode... textNodes) {
        if (textNodes.length == 1 && textNodes[0] instanceof ParentNode) {
            return (ParentNode) textNodes[0];
        } else {
            return new ParentNode(textNodes);
        }
    }

    public interface PlaceholderListChangedCallback {
        void onPlaceholderListChange(Identifier identifier, boolean removed);
    }

    public interface PlaceholderGetter {
        @Nullable
        PlaceholderHandler getPlaceholder(String placeholder);

        @Nullable
        default PlaceholderHandler getPlaceholder(String placeholder, ParserContext context) {
            return getPlaceholder(placeholder);
        }

        default boolean isContextOptional() {
            return false;
        }

        default boolean exists(String placeholder) {
            return this.getPlaceholder(placeholder) != null;
        }
    }
}
