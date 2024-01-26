package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.format.MultiCharacterFormat;
import eu.pb4.placeholders.api.parsers.format.SingleCharacterFormat;
import eu.pb4.placeholders.impl.placeholder.PlaceholderNode;
import eu.pb4.placeholders.impl.textparser.MultiTagLikeParser;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import eu.pb4.placeholders.impl.textparser.providers.LenientFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Experimental
public abstract class TagLikeParser implements NodeParser, TagLikeWrapper {
    public static final Format TAGS = Format.of('<', '>', ' ');
    public static final Format TAGS_LENIENT = new LenientFormat();
    public static final Format TAGS_LEGACY = new SingleCharacterFormat('<', '>', ':', new char[] {'\''});
    public static final Format PLACEHOLDER = Format.of('%', '%', ' ');
    public static final Format PLACEHOLDER_ALTERNATIVE = Format.of('{', '}', ' ');
    public static final Format PLACEHOLDER_ALTERNATIVE_DOUBLE = Format.of("{{", "}}", " ");
    public static final Format PLACEHOLDER_USER = Format.of("${", "}", "");
    private static final TextNode[] EMPTY = new TextNode[0];

    public static TagLikeParser placeholder(Format format, ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter placeholders) {
        return new SingleTagLikeParser(format, Provider.placeholder(contextKey, placeholders));
    }

    public static TagLikeParser direct(Format format, Function<String, @Nullable TextNode> placeholders) {
        return new SingleTagLikeParser(format, Provider.direct(placeholders));
    }

    public static TagLikeParser of(Format format, Provider provider) {
        return new SingleTagLikeParser(format, provider);
    }

    public static TagLikeParser of(Pair<Format, Provider>... formatsAndProviders) {
        return new MultiTagLikeParser(formatsAndProviders);
    }

    public static TagLikeParser of(Map<Format, Provider> formatsAndProviders) {
        var list = new ArrayList<>(formatsAndProviders.size());

        for (var entry : formatsAndProviders.entrySet()) {
            list.add(Pair.of(entry));
        }
        return new MultiTagLikeParser(list.toArray(new Pair[0]));
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        var context = new Context(this, "");
        parse(input, context);
        return context.toTextNode();
    }

    private void parse(TextNode node, Context context) {
        if (node instanceof LiteralNode literal) {
            context.input = literal.value();
            this.handleLiteral(literal.value(), context);
        } else if (node instanceof TranslatedNode translatedNode) {
            context.addNode(translatedNode.transform(this));
        } else if (node instanceof ParentTextNode parent) {
            var size = context.size();
            context.pushWithParser(null, parent::copyWith);
            for (var x : parent.getChildren()) {
                parse(x, context);
            }

            context.pop(context.size() - size);
        } else {
            context.addNode(node);
        }
    }

    protected abstract void handleLiteral(String value, Context context);

    @Override
    public TagLikeParser asTagLikeParser() {
        return this;
    }

    protected final int handleTag(String value, int pos, Format.Tag tag, Provider provider, Context context) {
        tag = provider.modifyTag(tag, context);
        if (tag == null) {
            context.addNode(new LiteralNode(value.substring(pos)));
            return -1;
        } else if (tag.start() != 0 && tag.start() != pos) {
            context.addNode(new LiteralNode(value.substring(pos, tag.start())));
        }

        pos = tag.end();
        context.currentPos = tag.start;
        provider.handleTag(tag.id(), tag.argument(), context);
        return pos;
    }

    public interface Provider {
        static Provider placeholder(ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter placeholders) {
            return new Provider() {
                @Override
                public boolean isValidTag(String tag, Context context) {
                    return placeholders.exists(tag);
                }

                @Override
                public void handleTag(String id, String argument, Context context) {
                    context.addNode(new PlaceholderNode(contextKey, id, placeholders,
                            placeholders.isContextOptional(), argument != null && !argument.isEmpty() ? argument : null));
                }
            };
        }

        static Provider direct(Function<String, @Nullable TextNode> function) {
            return new Provider() {
                @Override
                public boolean isValidTag(String tag, Context context) {
                    return function.apply(tag) != null;
                }

                @Override
                public void handleTag(String id, String argument, Context context) {
                    var x = function.apply(id);
                    if (x != null) {
                        context.addNode(x);
                    }
                }
            };
        }

        boolean isValidTag(String tag, Context context);

        void handleTag(String id, String argument, Context context);

        default Format.Tag modifyTag(Format.Tag tag, Context context) {
            return tag;
        }
    }

    public static final class Context {
        private final Stack<Scope> stack = new Stack<>();
        private final TagLikeParser parser;
        private int currentPos;
        private String input;

        Context(TagLikeParser parser, String input) {
            this.parser = parser;
            this.input = input;
            this.stack.push(Scope.parent());
        }

        public String input() {
            return this.input;
        }

        public boolean contains(String id) {
            for (int i = 0; i < stack.size(); i++) {
                if (id.equals(stack.get(stack.size() - i - 1).id)) {
                    return true;
                }
            }

            return false;
        }

        public void pop() {
            if (this.stack.size() > 1) {
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
            }
        }

        public void pop(int count) {
            count = Math.min(count, this.stack.size() - 1);
            for (int i = 0; i < count; i++) {
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
            }
        }

        public void pop(String id) {
            if (!contains(id)) {
                return;
            }

            while (this.stack.size() > 1) {
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
                if (x.id.equals(id)) {
                    return;
                }
            }
        }

        public void pop(Predicate<String> stopPredicate) {
            while (this.stack.size() > 1) {
                if (stopPredicate.test(this.stack.peek().id)) {
                    return;
                }
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
            }
        }

        public void popInclusive(Predicate<String> stopPredicate) {
            while (this.stack.size() > 1) {
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
                if (stopPredicate.test(x.id)) {
                    return;
                }
            }
        }

        @Nullable
        public String peekId() {
            return this.stack.peek().id;
        }

        public void pushParent() {
            this.stack.push(Scope.parent());
        }

        public void push(String id, Function<TextNode[], TextNode> merge) {
            this.stack.push(Scope.enclosing(id, merge));
        }

        public void pushWithParser(String id, BiFunction<TextNode[], NodeParser, TextNode> merge) {
            this.stack.push(Scope.enclosingParsed(id, merge));
        }

        public void addNode(TextNode node) {
            this.stack.peek().nodes.add(node);
        }

        public TextNode[] toTextNode() {
            while (!stack.isEmpty()) {
                var box = stack.pop();

                if (stack.isEmpty()) {
                    return box.nodes().toArray(EMPTY);
                }

                stack.peek().nodes.add(box.collapse(this.parser));
            }

            return null;
        }

        public int size() {
            return this.stack.size() - 1;
        }

        public NodeParser parser() {
            return this.parser;
        }

        public int currentTagPos() {
            return this.currentPos;
        }
    }

    private record Scope(@Nullable String id, List<TextNode> nodes,
                         BiFunction<TextNode[], NodeParser, TextNode> merger) {
        public static Scope parent() {
            return enclosing(ParentNode::new);
        }

        public static Scope enclosing(String id, Function<TextNode[], TextNode> merge) {
            return enclosingParsed(id, (a, b) -> merge.apply(a));
        }

        public static Scope enclosing(Function<TextNode[], TextNode> merge) {
            return enclosingParsed((a, b) -> merge.apply(a));
        }

        public static Scope enclosingParsed(BiFunction<TextNode[], NodeParser, TextNode> merge) {
            return new Scope(null, new ArrayList<>(), merge);
        }

        public static Scope enclosingParsed(String id, BiFunction<TextNode[], NodeParser, TextNode> merge) {
            return new Scope(id, new ArrayList<>(), merge);
        }

        public TextNode collapse(NodeParser parser) {
            return merger.apply(this.nodes().toArray(EMPTY), parser);
        }
    }

    public interface Format {
        default @Nullable Tag findFirst(String string, int start, Provider provider, Context context) {
            int maxLength = string.length();
            for (int i = start; i < maxLength; i++) {
                var x = findAt(string, i, provider, context);
                if (x != null) {
                    return x;
                }
            }
            return null;
        }

        @Nullable Tag findAt(String string, int start, Provider provider, Context context);

        record Tag(int start, int end, String id, String argument, @Nullable Object extra) {
            public Tag(int start, int end, String id, String argument) {
                this(start, end, id, argument, null);
            }
        }

        static Format of(char start, char end) {
            return new SingleCharacterFormat(start, end);
        }

        static Format of(char start, char end, char argumentSplitter) {
            return new SingleCharacterFormat(start, end, argumentSplitter);
        }
        static Format of(String start, String end, String argumentSplitter) {
            return new MultiCharacterFormat(start, end, argumentSplitter);
        }
    }
}
