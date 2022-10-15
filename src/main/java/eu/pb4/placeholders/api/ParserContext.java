package eu.pb4.placeholders.api;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ParserContext {
    private final Map<Key<?>, Object> map = new HashMap<>();

    private ParserContext() {}

    public static ParserContext of() {
        return new ParserContext();
    }

    public static <T> ParserContext of(Key<T> key, T object) {
        return new ParserContext().with(key, object);
    }

    public <T> ParserContext with(Key<T> key, T object) {
        this.map.put(key, object);
        return this;
    }

    @Nullable
    public <T> T get(Key<T> key) {
        return (T) this.map.get(key);
    };


    public record Key<T>(String key, Class<T> type) {
        public static final Key<Boolean> COMPACT_TEXT = new Key<>("compact_text", Boolean.class);
    };
}
