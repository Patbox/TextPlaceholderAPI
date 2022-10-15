package eu.pb4.placeholders.api.parsers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.impl.textparser.TextParserImpl;
import eu.pb4.placeholders.impl.textparser.TextTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TextParserV1 implements NodeParser {

    public static final TextParserV1 DEFAULT = new TextParserV1();
    public static final TextParserV1 SAFE = new TextParserV1();

    private final boolean allowOverrides = false;

    private final List<TextTag> tags = new ArrayList<>();
    private final Map<String, TextTag> byName = new HashMap<>();
    private final Map<String, TextTag> byNameAlias = new HashMap<>();

    public static TextParserV1 createDefault() {
        return DEFAULT.copy();
    }

    public static TextParserV1 createSafe() {
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

    public List<TextTag> getTags() {
        return ImmutableList.copyOf(tags);
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        return parseNodesWith(input, this::getTagParser);
    }

    public TextParserV1 copy() {
        var parser = new TextParserV1();
        for (var tag : this.tags) {
            parser.register(tag);
        }
        return parser;
    }

    public static TextNode[] parseNodesWith(TextNode input, TagParserGetter getter) {
        if (input instanceof LiteralNode literalNode) {
            return TextParserImpl.parse(literalNode.value(), getter);
        } else if (input instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentTextNode.getChildren()) {
                list.add(new ParentNode(parseNodesWith(child, getter)));
            }

            return list.toArray(new TextNode[0]);
        }

        return new TextNode[]{input};
    }

    public static NodeList parseNodesWith(String input, TagParserGetter handlers, @Nullable String endingTag) {
        return TextParserImpl.recursiveParsing(input, handlers, endingTag);
    }

    public @Nullable TagNodeBuilder getTagParser(String name) {
        var o = this.byNameAlias.get(name);
        return o != null ? o.parser() : null;
    }

    public record TextTag(String name, String[] aliases, String type, boolean userSafe, TagNodeBuilder parser) {
        public static TextTag of(String name, String type, TagNodeBuilder parser) {
            return of(name, type, true, parser);
        }

        public static TextTag of(String name, String type, boolean userSafe, TagNodeBuilder parser) {
            return of(name, List.of(), type, userSafe, parser);
        }

        public static TextTag of(String name, List<String> aliases, String type, boolean userSafe, TagNodeBuilder parser) {
            return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, parser);
        }
    }

    public record TagNodeValue(TextNode node, int length) {
        public static final TagNodeValue EMPTY = new TagNodeValue(EmptyNode.INSTANCE, 0);
    }

    public record NodeList(TextNode[] nodes, int length) {
        public static final NodeList EMPTY = new NodeList(new TextNode[0], 0);

        public TagNodeValue value(TextNode node) {
            return new TagNodeValue(node, this.length);
        }

        public TagNodeValue value(Function<TextNode[], TextNode> function) {
            return new TagNodeValue(function.apply(this.nodes), this.length);
        }
    }

    @FunctionalInterface
    public interface TagNodeBuilder {
        TagNodeValue parseString(String tag, String data, String input, TagParserGetter tags, String endAt);
    }

    @FunctionalInterface
    public interface TagParserGetter {
        @Nullable
        TagNodeBuilder getTagParser(String name);
    }

    static {
        TextTags.register();
    }
}
