package eu.pb4.placeholders.api;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.api.client.ClientPlaceholderContext;
import eu.pb4.placeholders.api.client.ClientPlaceholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import eu.pb4.placeholders.impl.LoaderUtil;
import eu.pb4.placeholders.impl.placeholder.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.ServerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.WorldPlaceholders;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Placeholders {
    private static final HashMap<Identifier, Placeholder<PlaceholderContext, ?>> COMMON_PLACEHOLDERS = new HashMap<>();
    private static final HashMap<Identifier, Placeholder<ServerPlaceholderContext, ?>> SERVER_PLACEHOLDERS = new HashMap<>();
    public static final PlaceholderGetter<ServerPlaceholderContext> SERVER_PLACEHOLDER_GETTER = placeholder -> getServerPlaceholder(Identifier.tryParse(placeholder));
    public static final PlaceholderGetter<PlaceholderContext> COMMON_PLACEHOLDER_GETTER = placeholder -> getCommonPlaceholder(Identifier.tryParse(placeholder));
    public static final NodeParser SERVER_PLACEHOLDER_PARSER = TagLikeParser.placeholder(TagLikeParser.PLACEHOLDER, ServerPlaceholderContext.SERVER_KEY, SERVER_PLACEHOLDER_GETTER);
    public static final NodeParser COMMON_PLACEHOLDER_PARSER = TagLikeParser.placeholder(TagLikeParser.PLACEHOLDER, ServerPlaceholderContext.COMMON_KEY, COMMON_PLACEHOLDER_GETTER);
    private static final List<PlaceholderListChangedCallback> SERVER_CHANGED_CALLBACKS = new ArrayList<>();
    private static final List<PlaceholderListChangedCallback> COMMON_CHANGED_CALLBACKS = new ArrayList<>();

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
    public static PlaceholderResult parseServerPlaceholder(Identifier identifier, String argument, ServerPlaceholderContext context) {
        var placeholder = getServerPlaceholder(identifier);
        if (placeholder != null) {
            return placeholder.onPlaceholderRequest(context, argument);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }

    /**
     * Parses PlaceholderContext, can be used for parsing by hand
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult parseCommonPlaceholder(Identifier identifier, String argument, PlaceholderContext context) {
        var placeholder = getCommonPlaceholder(identifier);
        if (placeholder != null) {
            return placeholder.onPlaceholderRequest(context, argument);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }

    @Nullable
    public static Placeholder<PlaceholderContext, ?> getCommonPlaceholder(Identifier identifier) {
        return COMMON_PLACEHOLDERS.get(identifier);
    }

    @Nullable
    public static Placeholder<ServerPlaceholderContext, ?> getServerPlaceholder(Identifier identifier) {
        return SERVER_PLACEHOLDERS.get(identifier);
    }

    /**
     * Registers new placeholder for identifier
     */
    public static <T> void registerServer(Identifier identifier, Placeholder.Handler<ServerPlaceholderContext, String> handler) {
        registerServer(identifier, ArgumentParser.STRING, handler);
    }

    public static <T> void registerServer(Identifier identifier, ArgumentParser<T> argumentParser, Placeholder.Handler<ServerPlaceholderContext, T> handler) {
        registerServer(new Placeholder<>(identifier, argumentParser, handler));
    }

    public static void registerServer(Placeholder<ServerPlaceholderContext, ?> placeholder) {
        SERVER_PLACEHOLDERS.put(placeholder.identifier(), placeholder);
        for (var e : SERVER_CHANGED_CALLBACKS) {
            e.onPlaceholderListChange(placeholder.identifier(), false);
        }
    }

    /**
     * Registers new placeholder for identifier
     */
    public static <T> void registerCommon(Identifier identifier, Placeholder.Handler<PlaceholderContext, String> handler) {
        registerCommon(identifier, ArgumentParser.STRING, handler);
    }

    public static <T> void registerCommon(Identifier identifier, ArgumentParser<T> argumentParser, Placeholder.Handler<PlaceholderContext, T> handler) {
        registerCommon(new Placeholder<>(identifier, argumentParser, handler));
    }

    public static void registerCommon(Placeholder<PlaceholderContext, ?> placeholder) {
        COMMON_PLACEHOLDERS.put(placeholder.identifier(), placeholder);
        for (var e : COMMON_CHANGED_CALLBACKS) {
            e.onPlaceholderListChange(placeholder.identifier(), false);
        }

        if (!SERVER_PLACEHOLDERS.containsKey(placeholder.identifier())) {
            //noinspection unchecked
            registerServer((Placeholder<ServerPlaceholderContext, ?>) (Object) placeholder);
        }

        if (LoaderUtil.IS_CLIENT) {
            //noinspection unchecked
            ClientPlaceholders.registerClient((Placeholder<ClientPlaceholderContext, ?>) (Object) placeholder);
        }
    }

    public static ImmutableMap<Identifier, Placeholder<PlaceholderContext, ?>> getCommonPlaceholders() {
        return ImmutableMap.copyOf(COMMON_PLACEHOLDERS);
    }

    public static ImmutableMap<Identifier, Placeholder<ServerPlaceholderContext, ?>> getServerPlaceholders() {
        return ImmutableMap.copyOf(SERVER_PLACEHOLDERS);
    }

    public static void registerServerChangeEvent(PlaceholderListChangedCallback callback) {
        SERVER_CHANGED_CALLBACKS.add(callback);
    }

    public static void registerCommonChangeEvent(PlaceholderListChangedCallback callback) {
        COMMON_CHANGED_CALLBACKS.add(callback);
    }

    public interface PlaceholderListChangedCallback {
        void onPlaceholderListChange(Identifier identifier, boolean removed);
    }

    public interface PlaceholderGetter<Ctx> {
        @Nullable
        Placeholder<Ctx, ?> getPlaceholder(String placeholder);
        default boolean exists(String placeholder) {
            return this.getPlaceholder(placeholder) != null;
        }

        default Placeholder<Ctx, ?> getPlaceholderOrThrow(String id) {
            var placeholder = getPlaceholder(id);
            if (placeholder == null) throw new RuntimeException("Requested placeholder '" + id + "', but it doesn't exist!");
            return placeholder;
        }
    }
}
