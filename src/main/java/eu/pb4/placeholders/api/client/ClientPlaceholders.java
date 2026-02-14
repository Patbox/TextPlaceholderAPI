package eu.pb4.placeholders.api.client;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.api.*;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientPlaceholders {
    private static final HashMap<Identifier, Placeholder<ClientPlaceholderContext, ?>> CLIENT_PLACEHOLDERS = new HashMap<>();
    public static final Placeholders.PlaceholderGetter<ClientPlaceholderContext> CLIENT_PLACEHOLDER_GETTER = placeholder -> getClientPlaceholder(Identifier.tryParse(placeholder));
    public static final NodeParser CLIENT_PLACEHOLDER_PARSER = TagLikeParser.placeholder(TagLikeParser.PLACEHOLDER, ClientPlaceholderContext.CLIENT_KEY, CLIENT_PLACEHOLDER_GETTER);
    private static final List<Placeholders.PlaceholderListChangedCallback> CLIENT_CHANGED_CALLBACKS = new ArrayList<>();

    static {
        //noinspection ResultOfMethodCallIgnored
        Placeholders.COMMON_PLACEHOLDER_GETTER.getClass();
    }

    /**
     * Parses PlaceholderContext, can be used for parsing by hand
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult parseClientPlaceholder(Identifier identifier, String argument, ClientPlaceholderContext context) {
        var placeholder = getClientPlaceholder(identifier);
        if (placeholder != null) {
            return placeholder.onPlaceholderRequest(context, argument);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }

    @Nullable
    public static Placeholder<ClientPlaceholderContext, ?> getClientPlaceholder(Identifier identifier) {
        return CLIENT_PLACEHOLDERS.get(identifier);
    }

    /**
     * Registers new placeholder for identifier
     */
    public static <T> void registerClient(Identifier identifier, Placeholder.Handler<ClientPlaceholderContext, String> handler) {
        registerClient(identifier, ArgumentParser.STRING, handler);
    }

    public static <T> void registerClient(Identifier identifier, ArgumentParser<T> argumentParser, Placeholder.Handler<ClientPlaceholderContext, T> handler) {
        registerClient(new Placeholder<>(identifier, argumentParser, handler));
    }

    public static void registerClient(Placeholder<ClientPlaceholderContext, ?> placeholder) {
        CLIENT_PLACEHOLDERS.put(placeholder.identifier(), placeholder);
        for (var e : CLIENT_CHANGED_CALLBACKS) {
            e.onPlaceholderListChange(placeholder.identifier(), false);
        }
    }

    public static ImmutableMap<Identifier, Placeholder<ClientPlaceholderContext, ?>> getClientPlaceholders() {
        return ImmutableMap.copyOf(CLIENT_PLACEHOLDERS);
    }

    public static void registerClientChangeEvent(Placeholders.PlaceholderListChangedCallback callback) {
        CLIENT_CHANGED_CALLBACKS.add(callback);
    }
}
