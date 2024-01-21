package eu.pb4.placeholders.api.parsers;

import com.google.common.collect.ImmutableList;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import eu.pb4.placeholders.impl.textparser.TextTagsV2;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Text parsing implementation. Should be used first.
 * Loosely based on MiniMessage, with some degree of compatibility with it.
 */
public class TextParserV2 implements NodeParser, TagLikeWrapper {
    private final TagLikeParser parser = new SingleTagLikeParser(TagLikeParser.TAGS_LEGACY, new TagLikeParser.Provider() {
        @Override
        public boolean isValidTag(String tag, TagLikeParser.Context context) {
            var peek = context.peekId();
            return tag.equals("r") || tag.equals("reset") || tag.startsWith("#") || TextParserV2.this.getTag(tag) != null || tag.equals("/") || (peek != null && tag.equals("/" + peek));
        }

        @Override
        public void handleTag(String id, String argument, TagLikeParser.Context context) {
            if (id.equals("/") || id.equals("/" + context.peekId())) {
                context.pop();
                return;
            }

            if (id.equals("r") || id.equals("reset")) {
                context.pop(context.size());
                return;
            }

            if (id.startsWith("#")) {
                var text = TextColor.parse(id);
                if (text.result().isPresent()) {
                    context.push(id, x -> new ColorNode(x, text.result().get()));
                }
                return;
            }


            var tag = TextParserV2.this.getTag(id);

            assert tag != null;

            if (tag.selfContained) {
                context.addNode(tag.nodeCreator.createTextNode(TextNode.array(), argument, context.parser()));
            } else {
                context.push(id, (a) -> tag.nodeCreator.createTextNode(a, argument, context.parser()));
            }
        }
    });
    public static final TextParserV2 DEFAULT = new TextParserV2();
    public static final TextParserV2 SAFE = new TextParserV2();
    private boolean allowOverrides = false;

    private final List<TextTag> tags = new ArrayList<>();
    private final Map<String, TextTag> byName = new HashMap<>();
    private final Map<String, TextTag> byNameAlias = new HashMap<>();

    public static TextParserV2 createDefault() {
        return DEFAULT.copy();
    }

    public static TextParserV2 createSafe() {
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

    public TagLikeParser asTagLikeParser() {
        return this.parser;
    }

    public List<TextTag> getTags() {
        return ImmutableList.copyOf(tags);
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        return this.parser.parseNodes(input);
    }

    public TextParserV2 copy() {
        var parser = new TextParserV2();
        for (var tag : this.tags) {
            parser.register(tag);
        }
        return parser;
    }

    public @Nullable TextTag getTag(String name) {
        return this.byNameAlias.get(name);
    }

    public record TextTag(String name, String[] aliases, String type, boolean userSafe, boolean selfContained,
                          NodeCreator nodeCreator) {
        public static TextTag self(String name, String type, Function<String, TextNode> creator) {
            return self(name, type, true, creator);
        }

        public static TextTag self(String name, String type, boolean userSafe, Function<String, TextNode> creator) {
            return self(name, List.of(), type, userSafe, creator);
        }

        public static TextTag self(String name, List<String> aliases, String type, boolean userSafe, Function<String, TextNode> creator) {
            return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, true, NodeCreator.self(creator));
        }

        public static TextTag self(String name, String type, NodeCreator creator) {
            return self(name, type, true, creator);
        }

        public static TextTag self(String name, String type, boolean userSafe, NodeCreator creator) {
            return self(name, List.of(), type, userSafe, creator);
        }

        public static TextTag self(String name, List<String> aliases, String type, boolean userSafe, NodeCreator creator) {
            return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, true, creator);
        }

        public static TextTag enclosing(String name, String type, NodeCreator creator) {
            return enclosing(name, type, true, creator);
        }

        public static TextTag enclosing(String name, String type, boolean userSafe, NodeCreator creator) {
            return enclosing(name, List.of(), type, userSafe, creator);
        }

        public static TextTag enclosing(String name, List<String> aliases, String type, boolean userSafe, NodeCreator creator) {
            return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, false, creator);
        }
    }

    public interface NodeCreator {
        TextNode createTextNode(TextNode[] nodes, String arg, NodeParser parser);

        static NodeCreator self(Function<String, TextNode> function) {
            return (a, b, c) -> function.apply(b);
        }

        static NodeCreator bool(BoolNodeArg function) {
            return (a, b, c) -> function.apply(a, SimpleArguments.bool(b, true));
        }

        interface BoolNodeArg {
            TextNode apply(TextNode[] nodes, boolean argument);
        }
    }

    static {
        TextTagsV2.register();
    }
}
