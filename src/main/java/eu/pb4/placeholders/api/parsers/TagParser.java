package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.textparser.providers.LegacyProvider;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import eu.pb4.placeholders.impl.textparser.providers.LenientProvider;
import eu.pb4.placeholders.impl.textparser.providers.ModernProvider;

import java.util.function.Function;

/**
 * Parser implementing QuickText and Simplified Text Format (Legacy).
 * Lenient parser can support both at the same time.
 * <a href="https://placeholders.pb4.eu/user/text-format/">Format documentation</a>
 *
 * To create pure Quick Text parser, you use methods without Legacy or Lenient in the name.
 * Lenient methods/parser allow for usage of both Quick Text and Simplified Text Format at the same
 * time, allowing for better transition between the formats.
 * Legacy parser refers to Simplified Text Format, which while discouraged, it's still supported.
 *
 */
public final class TagParser implements NodeParser, TagLikeWrapper {
    private final TagRegistry registry;
    private final TagLikeParser parser;

    public static final TagParser DEFAULT = new TagParser(TagLikeParser.TAGS, TagRegistry.DEFAULT, ModernProvider::new);
    public static final TagParser SAFE = new TagParser(TagLikeParser.TAGS, TagRegistry.SAFE, ModernProvider::new);
    public static final TagParser DEFAULT_LENIENT = new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.DEFAULT, LenientProvider::new);
    public static final TagParser SAFE_LENIENT = new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.SAFE, LenientProvider::new);
    public static final TagParser DEFAULT_LEGACY = new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.DEFAULT, LegacyProvider::new);
    public static final TagParser SAFE_LEGACY = new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.SAFE, LegacyProvider::new);
    private final TagLikeParser.Format format;
    private final Function<TagRegistry, TagLikeParser.Provider> providerCreator;

    private TagParser(TagLikeParser.Format format, TagRegistry registry, Function<TagRegistry, TagLikeParser.Provider> providerFunction) {
        this.registry = registry;
        this.parser = new SingleTagLikeParser(format, providerFunction.apply(registry));
        this.providerCreator = providerFunction;
        this.format = format;
    }

    public static TagParser create() {
        return new TagParser(TagLikeParser.TAGS, TagRegistry.create(), ModernProvider::new);
    }

    public static TagParser createLenient() {
        return new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.create(), LenientProvider::new);
    }

    public static TagParser createLegacy() {
        return new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.create(), LegacyProvider::new);
    }

    public static TagParser create(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS, registry, ModernProvider::new);
    }

    public static TagParser createLenient(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS_LENIENT, registry, LenientProvider::new);
    }

    public static TagParser createLegacy(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS_LEGACY, registry, LegacyProvider::new);
    }

    public static TagParser createDefault() {
        return DEFAULT.copy();
    }

    public static TagParser createSafe() {
        return SAFE.copy();
    }

    public static TagParser createDefaultLenient() {
        return DEFAULT_LENIENT.copy();
    }

    public static TagParser createSafeLenient() {
        return SAFE_LENIENT.copy();
    }

    public static TagParser createDefaultLegacy() {
        return DEFAULT_LEGACY.copy();
    }

    public static TagParser createSafeLegacy() {
        return SAFE_LEGACY.copy();
    }

    public void register(TextTag tag) {
        this.registry.register(tag);
    }

    public TagLikeParser asTagLikeParser() {
        return this.parser;
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        return this.parser.parseNodes(input);
    }

    public TagParser copy() {
        return new TagParser(this.format, registry.copy(), this.providerCreator);
    }


    public TagRegistry tagRegistry() {
        return registry;
    }
}
