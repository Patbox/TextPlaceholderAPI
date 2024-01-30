package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.impl.textparser.BuiltinTags;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class TagRegistry {
    public static final TagRegistry DEFAULT = new TagRegistry(true);
    public static final TagRegistry SAFE = new TagRegistry(true);

    static {
        BuiltinTags.register();
    }

    private final boolean global;

    private TagRegistry(boolean global) {
        this.global = global;
    }

    private final List<TextTag> tags = new ArrayList<>();
    private final Map<String, TextTag> byName = new HashMap<>();
    private final Map<String, TextTag> byNameAlias = new HashMap<>();
    private final boolean allowOverrides = false;

    public static TagRegistry create() {
        return new TagRegistry(false);
    }

    public static TagRegistry createDefault() {
        return DEFAULT.copy();
    }

    public static TagRegistry createSafe() {
        return SAFE.copy();
    }

    public static void registerDefault(TextTag tag) {
        DEFAULT.register(tag);

        if (tag.userSafe()) {
            SAFE.register(tag);
        }
    }

    public void register(TextTag tag) {
        if (this.byName.containsKey(tag.name())) {
            if (allowOverrides) {
                this.tags.removeIf((t) -> t.name().equals(tag.name()));
            } else {
                throw new RuntimeException("Duplicate tag identifier!");
            }
        }

        this.byName.put(tag.name(), tag);
        this.tags.add(tag);

        this.byNameAlias.put(tag.name(), tag);

        if (tag.aliases() != null) {
            for (int i = 0; i < tag.aliases().length; i++) {
                var alias = tag.aliases()[i];
                var old = this.byNameAlias.get(alias);
                if (old == null || !old.name().equals(alias)) {
                    this.byNameAlias.put(alias, tag);
                }
            }
        }
    }

    public TagRegistry copy() {
        var parser = new TagRegistry(false);
        for (var tag : this.tags) {
            parser.register(tag);
        }
        return parser;
    }

    public @Nullable TextTag getTag(String name) {
        return this.byNameAlias.get(name);
    }

    public List<TextTag> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

    public boolean isGlobal() {
        return this.global;
    }
}
