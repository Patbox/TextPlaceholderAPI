package eu.pb4.placeholders.impl.textparser.tagreg;

import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.textparser.BuiltinTags;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SimpleTagRegistry implements TagRegistry {
    public static final TagRegistry DEFAULT = new SimpleTagRegistry(true);
    public static final TagRegistry SAFE = new SimpleTagRegistry(true);

    static {
        BuiltinTags.register();
    }

    private final boolean global;
    private final List<TextTag> tags = new ArrayList<>();
    private final Map<String, TextTag> byName = new HashMap<>();
    private final Map<String, TextTag> byNameAlias = new HashMap<>();
    private final boolean allowOverrides;
    public SimpleTagRegistry(boolean global) {
        this.global = global;
        this.allowOverrides = !global;
    }

    @Override
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

    @Override
    public void remove(TextTag tag) {
        if (this.allowOverrides && this.tags.remove(tag)) {
            this.byNameAlias.values().removeIf(x -> x == tag);
            this.byName.values().removeIf(x -> x == tag);
        } else if (!this.allowOverrides) {
            throw new RuntimeException("Can't remove tag!");
        }
    }

    @Override
    public TagRegistry copy() {
        var parser = new SimpleTagRegistry(false);
        for (var tag : this.tags) {
            parser.register(tag);
        }
        return parser;
    }

    @Override
    public @Nullable TextTag getTag(String name) {
        return this.byNameAlias.get(name);
    }

    @Override
    public List<TextTag> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

    @Override
    public boolean isGlobal() {
        return this.global;
    }
}
