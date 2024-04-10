package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.impl.textparser.tagreg.SimpleTagRegistry;
import eu.pb4.placeholders.impl.textparser.tagreg.WrappingTagRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TagRegistry {
    TagRegistry DEFAULT =  SimpleTagRegistry.DEFAULT;
    TagRegistry SAFE =  SimpleTagRegistry.SAFE;

    static TagRegistry create() {
        return new SimpleTagRegistry(false);
    }

    static Builder builder() {
        return new Builder(create());
    }

    static TagRegistry copyDefault() {
        return DEFAULT.copy();
    }

    static Builder builderCopyDefault() {
        return new Builder(copyDefault());
    }

    static TagRegistry copySafe() {
        return SAFE.copy();
    }

    static Builder builderCopySafe() {
        return new Builder(copySafe());
    }

    static TagRegistry createDefault() {
        return WrappingTagRegistry.of(DEFAULT);
    }

    static Builder builderWithDefault() {
        return new Builder(createDefault());
    }

    static TagRegistry createSafe() {
        return WrappingTagRegistry.of(SAFE);
    }

    static Builder builderWithSafe() {
        return new Builder(createSafe());
    }

    static void registerDefault(TextTag tag) {
        SimpleTagRegistry.DEFAULT.register(tag);

        if (tag.userSafe()) {
            SimpleTagRegistry.SAFE.register(tag);
        }
    }

    void register(TextTag tag);

    void remove(TextTag tag);

    TagRegistry copy();

    @Nullable TextTag getTag(String name);

    List<TextTag> getTags();

    boolean isGlobal();

    final class Builder {
        private final TagRegistry registry;
        Builder(TagRegistry tagRegistry) {
            this.registry = tagRegistry;
        }

        public Builder add(TextTag tag) {
            this.registry.register(tag);
            return this;
        }

        public Builder remove(TextTag tag) {
            this.registry.remove(tag);
            return this;
        }

        public Builder remove(String tag) {
            this.registry.remove(this.registry.getTag(tag));
            return this;
        }

        public Builder copy(TagRegistry registry) {
            registry.getTags().forEach(this.registry::register);
            return this;
        }

        public TagRegistry build() {
            return this.registry;
        }
    }
}
