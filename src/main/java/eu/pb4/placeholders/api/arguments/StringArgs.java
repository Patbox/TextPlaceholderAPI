package eu.pb4.placeholders.api.arguments;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public final class StringArgs {
    private static final StringArgs EMPTY = new StringArgs("");
    private final List<String> ordered = new ArrayList<>();
    private final Map<String, String> keyed = new HashMap<>();
    private final String input;
    private int currentOrdered = 0;

    private StringArgs(String input) {
        this.input = input;
    }
    public static StringArgs ordered(String input, char separator) {
        var args = new StringArgs(input);
        args.ordered.addAll(SimpleArguments.split(input, separator));
        return args;
    }

    public static StringArgs keyed(String input, char separator, char map) {
        var args = new StringArgs(input);
        keyDecomposition(input, separator, map, (key, value) -> {
            if (key != null) {
                args.keyed.put(key, value != null ? SimpleArguments.unwrap(value) : "");
            }
        });

        return args;
    }

    public static StringArgs full(String input, char separator, char map) {
        var args = new StringArgs(input);
        keyDecomposition(input, separator, map, (key, value) -> {
            if (key != null) {
                args.keyed.put(key, value != null ? SimpleArguments.unwrap(value) : "");

                if (value == null) {
                    args.ordered.add(SimpleArguments.unwrap(key));
                }
            }
        });

        return args;
    }

    private static void keyDecomposition(String input, char separator, char map, BiConsumer<@Nullable String, @Nullable String> consumer) {
        String key = null;
        String value = null;
        var b = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            var chr = input.charAt(i);

            if (chr == map && key == null) {
                key = b.toString();
                b = new StringBuilder();
            } else if (chr == separator) {
                if (b.isEmpty() && key == null) {
                    consumer.accept(null, null);
                    continue;
                }

                if (key == null) {
                    key = b.toString();
                } else {
                    value = b.toString();
                }

                consumer.accept(key, value);
                key = null;
                value = null;
                b = new StringBuilder();
            } else {
                b.append(chr);
            }
        }

        if (key != null) {
            consumer.accept(key, b.isEmpty() ? null : b.toString());
        } else if (!b.isEmpty()) {
            consumer.accept(b.toString(), null);
        }
    }

    public static StringArgs empty() {
        return EMPTY;
    }

    public String input() {
        return input;
    }

    @Nullable
    public String get(String name) {
        return this.keyed.get(name);
    }

    public String get(String name, String defaultValue) {
        return this.keyed.getOrDefault(name, defaultValue);
    }

    @Nullable
    public String get(String name, int id) {
        var x = this.keyed.get(name);
        if (x != null) {
            return x;
        }
        if (id < this.ordered.size()) {
            return this.ordered.get(id);
        }
        return null;
    }

    public String get(String name, int id, String defaultValue) {
        var x = get(name, id);
        return x != null ? x : defaultValue;
    }

    @Nullable
    public String getNext(String name) {
        var x = this.keyed.get(name);
        if (x != null) {
            return x;
        }
        if (this.currentOrdered < this.ordered.size()) {
            return this.ordered.get(this.currentOrdered++);
        }
        return null;
    }

    public String getNext(String name, String defaultValue) {
        var x = getNext(name);
        return x != null ? x : defaultValue;
    }

    public boolean isEmpty() {
        return this.keyed.isEmpty() && this.ordered.isEmpty();
    }

    public List<String> ordered() {
        return Collections.unmodifiableList(this.ordered);
    }

    public int size() {
        return Math.max(this.keyed.size(), this.ordered.size());
    }
}
