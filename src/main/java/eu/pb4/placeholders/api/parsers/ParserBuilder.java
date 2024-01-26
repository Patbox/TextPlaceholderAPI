package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.*;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.impl.textparser.MultiTagLikeParser;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Function;

public class ParserBuilder {
    private final Map<TagLikeParser.Format, TagLikeParser.Provider> tagLike = new HashMap<>();
    private final List<NodeParser> parserList = new ArrayList<>();
    private final List<Formatting> legacyFormatting = new ArrayList<>();
    private boolean hasLegacy = false;
    private boolean legacyRGB = false;
    private boolean simplifiedTextFormat;
    private boolean quickText;
    private boolean safeOnly;
    private TagRegistry customTagRegistry;

    public ParserBuilder globalPlaceholders() {
        return add(Placeholders.DEFAULT_PLACEHOLDER_PARSER);
    }

    public ParserBuilder globalPlaceholders(TagLikeParser.Format format) {
        return customTags(format, TagLikeParser.Provider.placeholder(PlaceholderContext.KEY, Placeholders.DEFAULT_PLACEHOLDER_GETTER));
    }

    public ParserBuilder globalPlaceholders(TagLikeParser.Format format, ParserContext.Key<PlaceholderContext> contextKey) {
        return customTags(format, TagLikeParser.Provider.placeholder(contextKey, Placeholders.DEFAULT_PLACEHOLDER_GETTER));
    }

    public ParserBuilder placeholders(TagLikeParser.Format format, ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter getter) {
        return customTags(format, TagLikeParser.Provider.placeholder(contextKey, getter));
    }

    public ParserBuilder placeholders(TagLikeParser.Format format, Function<String, TextNode> function) {
        return customTags(format, TagLikeParser.Provider.direct(function));
    }

    public ParserBuilder quickText() {
        this.quickText = true;
        return this;
    }

    public ParserBuilder simplifiedTextFormat() {
        this.simplifiedTextFormat = true;
        return this;
    }

    public ParserBuilder requireSafe() {
        this.safeOnly = true;
        return this;
    }

    public ParserBuilder customTagRegistry(TagRegistry registry) {
        this.customTagRegistry = registry;
        return this;
    }

    public ParserBuilder markdown() {
        return add(MarkdownLiteParserV1.ALL);
    }

    public ParserBuilder markdown(MarkdownLiteParserV1.MarkdownFormat... formats) {
        return add(new MarkdownLiteParserV1(formats));
    }

    public ParserBuilder legacyColor() {
        return add(LegacyFormattingParser.COLORS);
    }

    public ParserBuilder legacyVanillaColor() {
        return add(LegacyFormattingParser.BASE_COLORS);
    }

    public ParserBuilder legacyAll() {
        return add(LegacyFormattingParser.ALL);
    }

    public ParserBuilder legacy(boolean allowRGB, Formatting... formatting) {
        this.hasLegacy = true;
        this.legacyRGB = allowRGB;
        this.legacyFormatting.addAll(List.of(formatting));

        return this;
    }

    public ParserBuilder customTags(TagLikeParser.Format format, TagLikeParser.Provider provider) {
        this.tagLike.put(format, provider);
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
            list.add(TagParser.createLenient(reg));
        } else if (this.quickText) {
            list.add(TagParser.create(reg));
        } else if (this.simplifiedTextFormat) {
            list.add(TagParser.createLenient(reg));
        }

        list.addAll(this.parserList);

        if (this.hasLegacy) {
            list.add(new LegacyFormattingParser(this.legacyRGB, this.legacyFormatting.toArray(new Formatting[0])));
        }

        return NodeParser.merge(list);
    }
}
