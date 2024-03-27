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
 * Parser implementing QuickText (Recommended) and Simplified Text Format (Previous).
 * QuickText with STF parser can support both at the same time.
 * <a href="https://placeholders.pb4.eu/user/quicktext/">Format documentation for QuckText</a>
 * <a href="https://placeholders.pb4.eu/user/text-format/">Format documentation for Simplified Text Format</a>
 */
public final class TagParser implements NodeParser, TagLikeWrapper {
    private final TagRegistry registry;
    private final TagLikeParser parser;
    public static final TagParser DEFAULT = new TagParser(TagLikeParser.TAGS, TagRegistry.DEFAULT, ModernProvider::new);
    public static final TagParser DEFAULT_SAFE = new TagParser(TagLikeParser.TAGS, TagRegistry.SAFE, ModernProvider::new);
    public static final TagParser QUICK_TEXT = new TagParser(TagLikeParser.TAGS, TagRegistry.DEFAULT, ModernProvider::new);
    public static final TagParser QUICK_TEXT_SAFE = new TagParser(TagLikeParser.TAGS, TagRegistry.SAFE, ModernProvider::new);
    public static final TagParser QUICK_TEXT_WITH_STF = new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.DEFAULT, LenientProvider::new);
    public static final TagParser QUICK_TEXT_WITH_STF_SAFE = new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.SAFE, LenientProvider::new);
    public static final TagParser SIMPLIFIED_TEXT_FORMAT = new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.DEFAULT, LegacyProvider::new);
    public static final TagParser SIMPLIFIED_TEXT_FORMAT_SAFE = new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.SAFE, LegacyProvider::new);
    private final TagLikeParser.Format format;
    private final Function<TagRegistry, TagLikeParser.Provider> providerCreator;

    private TagParser(TagLikeParser.Format format, TagRegistry registry, Function<TagRegistry, TagLikeParser.Provider> providerFunction) {
        this.registry = registry;
        this.parser = new SingleTagLikeParser(format, providerFunction.apply(registry));
        this.providerCreator = providerFunction;
        this.format = format;
    }

    public static TagParser createQuickText() {
        return new TagParser(TagLikeParser.TAGS, TagRegistry.create(), ModernProvider::new);
    }

    public static TagParser createQuickTextWithSTF() {
        return new TagParser(TagLikeParser.TAGS_LENIENT, TagRegistry.create(), LenientProvider::new);
    }

    public static TagParser createSimplifiedTextFormat() {
        return new TagParser(TagLikeParser.TAGS_LEGACY, TagRegistry.create(), LegacyProvider::new);
    }

    public static TagParser createQuickText(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS, registry, ModernProvider::new);
    }

    public static TagParser createQuickTextWithSTF(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS_LENIENT, registry, LenientProvider::new);
    }

    public static TagParser createSimplifiedTextFormat(TagRegistry registry) {
        return new TagParser(TagLikeParser.TAGS_LEGACY, registry, LegacyProvider::new);
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
        return new TagParser(this.format, this.registry.copy(), this.providerCreator);
    }


    public TagRegistry tagRegistry() {
        return registry;
    }

    public Function<TagRegistry, TagLikeParser.Provider> providerCreator() {
        return providerCreator;
    }
}
