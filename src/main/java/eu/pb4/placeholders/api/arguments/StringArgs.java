package eu.pb4.placeholders.api.arguments;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import eu.pb4.placeholders.impl.StringArgOps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.CharPredicate;
import net.minecraft.core.HolderLookup;

public final class StringArgs {
    private static final StringArgs EMPTY = new StringArgs("");
    private final List<String> ordered = new ArrayList<>();
    private final Map<String, String> keyed = new HashMap<>();
    private final Map<String, StringArgs> keyedMaps = new HashMap<>();
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
        return keyed(input, separator, map, true, SimpleArguments::isWrapCharacter);
    }
    public static StringArgs keyed(String input, char separator, char map, boolean hasMaps, CharPredicate wrapCharacters) {
        var args = new StringArgs(input);
        keyDecomposition(input, 0, separator, map, wrapCharacters, hasMaps, (char) 0, (key, value) -> {
            if (key != null) {
                args.keyed.put(key, value != null ? SimpleArguments.unwrap(value, wrapCharacters) : "");
            }
        }, args.keyedMaps::put);

        return args;
    }

    public static StringArgs full(String input, char separator, char map) {
        return full(input, separator, map, true, SimpleArguments::isWrapCharacter);
    }
    public static StringArgs full(String input, char separator, char map, boolean hasMaps, CharPredicate wrapCharacters) {
        var args = new StringArgs(input);
        keyDecomposition(input, 0, separator, map, wrapCharacters, hasMaps, (char) 0, (key, value) -> {
            if (key != null) {
                args.keyed.put(key, value != null ? SimpleArguments.unwrap(value, wrapCharacters) : "");

                if (value == null) {
                    args.ordered.add(SimpleArguments.unwrap(key, wrapCharacters));
                }
            }
        }, args.keyedMaps::put);

        return args;
    }

    private static int keyDecomposition(String input, int offset, char separator, char map, CharPredicate isWrap, boolean hasMaps, char stopAt, BiConsumer<@Nullable String, @Nullable String> consumer, BiConsumer<String, StringArgs> mapConsumer) {
        String key = null;
        String value = null;
        var b = new StringBuilder();
        char wrap = 0;
        int i = offset;
        for (; i < input.length(); i++) {
            var chr = input.charAt(i);
            var chrN = i != input.length() - 1 ? input.charAt(i + 1) : 0;
            if (chr == stopAt && wrap == 0) {
                break;
            } else if (key != null && b.isEmpty() && hasMaps && (chr == '{' || chr == '[') && wrap == 0) {
                var ordered = new ArrayList<String>();
                var keyed = new HashMap<String, String>();
                var keyedMaps = new HashMap<String, StringArgs>();
                var ti = keyDecomposition(input, i + 1, separator, map, isWrap, true,
                        chr == '{' ? '}' : ']', (keyx, valuex) -> {
                            if (keyx != null) {
                                keyed.put(keyx, valuex != null ? SimpleArguments.unwrap(valuex, isWrap) : "");

                                if (valuex == null) {
                                    ordered.add(SimpleArguments.unwrap(keyx, isWrap));
                                }
                            }
                        }, keyedMaps::put);
                if (ti == input.length()) {
                    b.append(chr);
                } else {
                    var arg = new StringArgs(input.substring(i, ti));
                    arg.ordered.addAll(ordered);
                    arg.keyed.putAll(keyed);
                    arg.keyedMaps.putAll(keyedMaps);

                    mapConsumer.accept(key, arg);
                    key = null;
                    i = ti;
                }
            } else if (chr == map && wrap == 0 && key == null) {
                key = b.toString();
                b = new StringBuilder();
            } else if ((chr == '\\' && chrN != 0) || (chrN != 0 && chr == chrN && isWrap.test(chr))) {
                b.append(chrN);
                i++;
            } else if (isWrap.test(chr) && (wrap == 0 || wrap == chr)) {
                wrap = wrap == 0 ? chr : 0;
            } else if (chr == separator && wrap == 0) {
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

        return i;
    }

    public static StringArgs empty() {
        return EMPTY;
    }

    public static StringArgs emptyNew() {
        return new StringArgs("");
    }

    public String input() {
        return input;
    }

    @Nullable
    public String get(String name) {
        return this.keyed.get(name);
    }

    @Nullable
    public StringArgs getNested(String name) {
        return this.keyedMaps.get(name);
    }

    public StringArgs getNestedOrEmpty(String name) {
        return this.keyedMaps.getOrDefault(name, EMPTY);
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

    public <T> DataResult<T> get(String name, Codec<T> codec) {
        var val = get(name);
        var map = getNested(name);
        return val == null && map == null ? DataResult.error(() -> "Empty")
                : codec.decode(StringArgOps.INSTANCE,
                val != null ? Either.left(val) : Either.right(map)).map(Pair::getFirst);
    }

    public <T> DataResult<T> get(String name, Codec<T> codec, HolderLookup.Provider wrapperLookup) {
        var val = get(name);
        var map = getNested(name);
        return val == null && map == null ? DataResult.error(() -> "Empty")
                : codec.decode(wrapperLookup.createSerializationContext(StringArgOps.INSTANCE),
                val != null ? Either.left(val) : Either.right(map)).map(Pair::getFirst);
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

    public void ifPresent(String key, Consumer<String> valueConsumer) {
        var val = get(key);
        if (val != null) {
            valueConsumer.accept(val);
        }
    }

    public boolean contains(String key) {
        return this.keyed.containsKey(key);
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

    @ApiStatus.Internal
    public List<String> unsafeOrdered() {
        return this.ordered;
    }

    @ApiStatus.Internal
    public Map<String, String> unsafeKeyed() {
        return this.keyed;
    }


    @ApiStatus.Internal
    public Map<String, StringArgs> unsafeKeyedMap() {
        return this.keyedMaps;
    }

    @Override
    public String toString() {
        return "StringArgs{" +
                "ordered=" + ordered +
                ", keyed=" + keyed +
                ", keyedMaps=" + keyedMaps +
                '}';
    }
}
