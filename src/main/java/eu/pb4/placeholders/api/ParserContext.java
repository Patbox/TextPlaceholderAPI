package eu.pb4.placeholders.api;

import eu.pb4.placeholders.api.node.parent.DynamicShadowNode;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;

public final class ParserContext {
    private Map<Key<?>, Object> map;
    private boolean copyOnWrite;
    private boolean hasNodeContext;

    private ParserContext(Map<Key<?>, Object> map, boolean copyOnWrite, boolean hasNodeContext) {
        this.map = map;
        this.copyOnWrite = copyOnWrite;
        this.hasNodeContext = hasNodeContext;
    }

    public static ParserContext of() {
        return new ParserContext(new HashMap<>(), false, false);
    }

    public static <T> ParserContext of(Key<T> key, T object) {
        return of().with(key, object);
    }

    public <T> ParserContext with(Key<T> key, T object) {
        if (this.map.get(key) == object) {
            return this;
        }

        if (this.copyOnWrite) {
            this.map = new HashMap<>(this.map);
            this.copyOnWrite = false;
        }
        if (object == null) {
            this.map.remove(key);
        } else {
            this.map.put(key, object);
        }
        this.hasNodeContext |= key.nodeContext();
        return this;
    }

    @Nullable
    public <T> T get(Key<T> key) {
        //noinspection unchecked
        return (T) this.map.get(key);
    }

    public <T> T getOrElse(Key<T> key, T defaultValue) {
        //noinspection unchecked
        return (T) this.map.getOrDefault(key, defaultValue);
    }

    public <T> T getOrElse(Key<T> key, Supplier<T> defaultValue) {
        //noinspection unchecked
        var x =  (T) this.map.get(key);
        if (x == null) {
            return defaultValue.get();
        }
        return x;
    }

    public <T> T getOrThrow(Key<T> key) {
        //noinspection unchecked
        return Objects.requireNonNull((T) this.map.get(key));
    };

    public boolean contains(Key<?> key) {
        return this.map.containsKey(key);
    }

    public ParserContext copy() {
        this.copyOnWrite = true;
        return new ParserContext(this.map, true, this.hasNodeContext);
    }

    public ParserContext copyWithoutNodeContext() {
        if (this.hasNodeContext) {
            var map = new HashMap<Key<?>, Object>();
            for (var key : this.map.keySet()) {
                if (!key.nodeContext()) {
                    map.put(key, this.map.get(key));
                }
            }

            return new ParserContext(map, false, false);
        }
        return this.copy();
    }

    public <T> ParserContext withIfNotSet(Key<T> key, T object) {
        if (this.map.containsKey(key)) {
            return this;
        }
        return this.with(key, object);
    }

    public record Key<T>(String key, @Nullable Class<T> type, boolean nodeContext) {
        public static final Key<Boolean> COMPACT_COMPONENT = of("compact_component", Boolean.class);
        public static final Key<HolderLookup.Provider> HOLDER_LOOKUP = of("holder_lookup", HolderLookup.Provider.class);
        public static final Key<DynamicShadowNode.Transformer> DEFAULT_SHADOW_STYLER = ofNode("default_shadow_styler", DynamicShadowNode.Transformer.class);

        public static <T> Key<T> of(String key, T type) {
            //noinspection unchecked
            return new Key<T>(key, (Class<T>) type.getClass(), false);
        }

        public static <T> Key<T> of(String key, Class<T> type) {
            //noinspection unchecked
            return new Key<T>(key, type, false);
        }

        public static <T> Key<T> of(String key) {
            //noinspection unchecked
            return new Key<T>(key, null, false);
        }

        public static <T> Key<T> ofNode(String key, T type) {
            //noinspection unchecked
            return new Key<T>(key, (Class<T>) type.getClass(), true);
        }

        public static <T> Key<T> ofNode(String key, Class<T> type) {
            //noinspection unchecked
            return new Key<T>(key, type, true);
        }


        public static <T> Key<T> ofNode(String key) {
            //noinspection unchecked
            return new Key<T>(key, null, true);
        }
    };
}
