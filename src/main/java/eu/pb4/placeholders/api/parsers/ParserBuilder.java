package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.*;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.impl.textparser.MultiTagLikeParser;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Allows you to create stacked parser in most "correct" and compatible way.
 */
public class ParserBuilder {
    private final Map<TagLikeParser.Format, TagLikeParser.Provider> tagLike = new LinkedHashMap<>();
    private final List<NodeParser> parserList = new ArrayList<>();
    private final List<Formatting> legacyFormatting = new ArrayList<>();
    private boolean hasLegacy = false;
    private boolean legacyRGB = false;
    private boolean simplifiedTextFormat;
    private boolean quickText;
    private boolean safeOnly;
    private TagRegistry customTagRegistry;
    private boolean staticPreParsing;

    public static ParserBuilder of() {
        return new ParserBuilder();
    }

    /**
     * Enables parsing of Global Placeholders (aka {@link Placeholders})
     */
    public ParserBuilder globalPlaceholders() {
        return add(Placeholders.DEFAULT_PLACEHOLDER_PARSER);
    }
    /**
     * Enables parsing of Global Placeholders, but with a custom format
     */
    public ParserBuilder globalPlaceholders(TagLikeParser.Format format) {
        return customTags(format, TagLikeParser.Provider.placeholder(PlaceholderContext.KEY, Placeholders.DEFAULT_PLACEHOLDER_GETTER));
    }
    /**
     * Enables parsing of Global Placeholder, but with a custom format and context source
     */
    public ParserBuilder globalPlaceholders(TagLikeParser.Format format, ParserContext.Key<PlaceholderContext> contextKey) {
        return customTags(format, TagLikeParser.Provider.placeholder(contextKey, Placeholders.DEFAULT_PLACEHOLDER_GETTER));
    }
    /**
     * Enables parsing of custom placeholder with a custom format and context source
     */
    public ParserBuilder placeholders(TagLikeParser.Format format, ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter getter) {
        return customTags(format, TagLikeParser.Provider.placeholder(contextKey, getter));
    }
    /**
     * Enables parsing of custom placeholder with a functional provider
     */
    public ParserBuilder placeholders(TagLikeParser.Format format, Function<String, TextNode> function) {
        return customTags(format, TagLikeParser.Provider.placeholder(function));
    }

    /**
     * Enables parsing of custom, context dependent placeholders
     */
    public ParserBuilder placeholders(TagLikeParser.Format format, ParserContext.Key<Function<String, Text>> key) {
        return customTags(format, TagLikeParser.Provider.placeholder(key));
    }

    /**
     * Enables parsing of custom, context dependent placeholders
     */
    public ParserBuilder placeholders(TagLikeParser.Format format, Set<String> tags, ParserContext.Key<Function<String, Text>> key) {
        return customTags(format, TagLikeParser.Provider.placeholder(tags, key));
    }

    /**
     * Enables QuickText format.
     */
    public ParserBuilder quickText() {
        this.quickText = true;
        return this;
    }
    /**
     * Enables Simplified Text Format.
     */
    public ParserBuilder simplifiedTextFormat() {
        this.simplifiedTextFormat = true;
        return this;
    }
    /**
     * Forces usage of safe tags for tag parsers.
     */
    public ParserBuilder requireSafe() {
        this.safeOnly = true;
        return this;
    }
    /**
     * Forces usage of custom registry for tag parsers.
     */
    public ParserBuilder customTagRegistry(TagRegistry registry) {
        this.customTagRegistry = registry;
        return this;
    }
    /**
     * Enables Markdown.
     */
    public ParserBuilder markdown() {
        return add(MarkdownLiteParserV1.ALL);
    }
    /**
     * Enables Markdown with limited formatting.
     */
    public ParserBuilder markdown(MarkdownLiteParserV1.MarkdownFormat... formats) {
        return add(new MarkdownLiteParserV1(formats));
    }

    /**
     * Enables Markdown with limited formatting.
     */
    public ParserBuilder markdown(Collection<MarkdownLiteParserV1.MarkdownFormat> formats) {
        return add(new MarkdownLiteParserV1(formats.toArray(new MarkdownLiteParserV1.MarkdownFormat[0])));
    }

    /**
     * Enables Markdown with limited formatting.
     */
    public ParserBuilder markdown(Function<TextNode[], TextNode> spoilerFormatting,
                                  Function<TextNode[], TextNode> quoteFormatting,
                                  BiFunction<TextNode[], TextNode, TextNode> urlFormatting,
                                  MarkdownLiteParserV1.MarkdownFormat... formatting) {
        return add(new MarkdownLiteParserV1(spoilerFormatting, quoteFormatting, urlFormatting, formatting));
    }

    /**
     * Enables Markdown with limited formatting.
     */
    public ParserBuilder markdown(Function<TextNode[], TextNode> spoilerFormatting,
                                  Function<TextNode[], TextNode> quoteFormatting,
                                  BiFunction<TextNode[], TextNode, TextNode> urlFormatting,
                                  Collection<MarkdownLiteParserV1.MarkdownFormat> formatting) {
        return add(new MarkdownLiteParserV1(spoilerFormatting, quoteFormatting, urlFormatting, formatting.toArray(new MarkdownLiteParserV1.MarkdownFormat[0])));
    }
    /**
     * Enables legacy color tags (&X) with rgb extension.
     */
    public ParserBuilder legacyColor() {
        return add(LegacyFormattingParser.COLORS);
    }
    /**
     * Enables legacy color tags (&X).
     */
    public ParserBuilder legacyVanillaColor() {
        return add(LegacyFormattingParser.BASE_COLORS);
    }
    /**
     * Enables all legacy formatting (&X) with rgb extension.
     */
    public ParserBuilder legacyAll() {
        return add(LegacyFormattingParser.ALL);
    }
    /**
     * Enables legacy formatting.
     */
    public ParserBuilder legacy(boolean allowRGB, Formatting... formatting) {
        this.hasLegacy = true;
        this.legacyRGB = allowRGB;
        this.legacyFormatting.addAll(List.of(formatting));

        return this;
    }

    /**
     * Enables legacy formatting.
     */
    public ParserBuilder legacy(boolean allowRGB, Collection<Formatting> formatting) {
        this.hasLegacy = true;
        this.legacyRGB = allowRGB;
        this.legacyFormatting.addAll(formatting);

        return this;
    }
    /**
     * Adds custom tag like parser
     */
    public ParserBuilder customTags(TagLikeParser.Format format, TagLikeParser.Provider provider) {
        this.tagLike.put(format, provider);
        return this;
    }

    /**
     * Enables pre-parsing for static elements.
     * This should only be used if you don't convert to {@link Text} right away, but also don't transform
     * it further yourself (aka you use TextNode's as a template with custom placeholders)
     */
    public ParserBuilder staticPreParsing() {
        this.staticPreParsing = true;
        return this;
    }

    public ParserBuilder add(NodeParser parser) {
        if (parser instanceof TagLikeWrapper wrapper) {
            var x = wrapper.asTagLikeParser();
            if (x instanceof SingleTagLikeParser p) {
                return customTags(p.format(), p.provider());
            } else if (x instanceof MultiTagLikeParser p) {
                this.tagLike.putAll(Map.ofEntries(p.pairs()));
                return this;
            }
        } else if (parser instanceof LegacyFormattingParser legacyFormattingParser) {
            this.hasLegacy = true;
            this.legacyFormatting.addAll(legacyFormattingParser.formatting());
            this.legacyRGB |= legacyFormattingParser.allowRGB();
        }

        return forceAdd(parser);
    }

    public ParserBuilder forceAdd(NodeParser parser) {
        this.parserList.add(parser);
        return this;
    }

    public NodeParser build() {
        var list = new ArrayList<NodeParser>(this.parserList.size() + 1);
        if (!this.tagLike.isEmpty()) {
            list.add(TagLikeParser.of(this.tagLike));
        }

        var reg = this.customTagRegistry != null ? this.customTagRegistry : this.safeOnly ? TagRegistry.SAFE : TagRegistry.DEFAULT;

        if (this.quickText && this.simplifiedTextFormat) {
            list.add(TagParser.createQuickTextWithSTF(reg));
        } else if (this.quickText) {
            list.add(TagParser.createQuickText(reg));
        } else if (this.simplifiedTextFormat) {
            list.add(TagParser.createSimplifiedTextFormat(reg));
        }

        list.addAll(this.parserList);

        if (this.hasLegacy) {
            list.add(new LegacyFormattingParser(this.legacyRGB, this.legacyFormatting.toArray(new Formatting[0])));
        }

        if (this.staticPreParsing) {
            list.add(StaticPreParser.INSTANCE);
        }

        return NodeParser.merge(list);
    }
}
