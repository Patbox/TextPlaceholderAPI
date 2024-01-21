package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.impl.placeholder.PlaceholderNode;
import eu.pb4.placeholders.impl.textparser.MultiTagLikeParser;
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

@ApiStatus.Experimental
public abstract class TagLikeParser implements NodeParser {
    public static final Format TAGS = new Format('<', '>', ' ');
    public static final Format TAGS_LEGACY = new Format('<', '>', ':');
    public static final Format PLACEHOLDER = new Format('%', '%', ' ');
    public static final Format PLACEHOLDER_ALTERNATIVE = new Format('{', '}', ' ');
    public static final Format PLACEHOLDER_USER = new Format("${", "}", "");
    private static final TextNode[] EMPTY = new TextNode[0];

    public static TagLikeParser of(Format format, Provider provider) {
        return new SingleTagLikeParser(format, provider);
    }

    public static TagLikeParser placeholder(Format format, ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter placeholders) {
        return new SingleTagLikeParser(format, Provider.placeholder(contextKey, placeholders));
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
        var context = new Context(this);
        parse(input, context);
        return context.toTextNode();
    }

    private void parse(TextNode node, Context context) {
        if (node instanceof LiteralNode literal) {
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

        boolean isValidTag(String tag, Context context);

        void handleTag(String id, String argument, Context context);
    }

    public static final class Context {
        private final Stack<Scope> stack = new Stack<>();
        private final TagLikeParser parser;

        Context(TagLikeParser parser) {
            this.parser = parser;
            this.stack.push(Scope.parent());
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

            while (true) {
                var x = this.stack.pop();
                this.stack.peek().nodes.add(x.collapse(this.parser));
                if (x.id.equals(id)) {
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

    public record Format(char[] start, char[] end, char[] argument, char[] argumentWrappers) {
        public Format(char start, char end, char argument) {
            this(new char[]{start}, new char[]{end}, new char[]{argument}, new char[]{'"', '\'', '`'});
        }

        public Format(String start, String end, String argument) {
            this(start.toCharArray(), end.toCharArray(), argument.toCharArray(), new char[]{'"', '\'', '`'});
        }

        public Format(String start, String end, String argument, String argumentWrappers) {
            this(start.toCharArray(), end.toCharArray(), argument.toCharArray(), argumentWrappers.toCharArray());
        }

        @Nullable
        public TagInfo find(String string, int start, Provider provider, Context context) {
            int maxLength = string.length() - this.start.length + this.end.length;
            main:
            for (int i = start; i < maxLength; i++) {
                if (string.charAt(i) == '\\') {
                    continue;
                }

                for (int a = 0; a < this.start.length; a++) {
                    var charc = string.charAt(i + a);
                    if (charc != this.start[a]) {
                        continue main;
                    }
                }

                String id = null;
                String argument = "";

                char wrapper = 0;
                var builder = new StringBuilder();
                validationLoop:
                for (int b = i + 1; b < maxLength; b++) {
                    var curr = string.charAt(b);
                    var matched = true;
                    boolean isArgument = false;

                    if (wrapper != 0) {
                        if (curr == wrapper) {
                            wrapper = 0;
                        }

                        builder.append(curr);
                        continue;
                    }

                    if (curr == '\\') {
                        if (b + 1 < maxLength) {
                            b++;
                            builder.append(string.charAt(b));
                        }

                        continue;
                    }

                    if (id != null) {
                        for (char argumentWrapper : this.argumentWrappers) {
                            if (curr == argumentWrapper) {
                                builder.append(curr);
                                wrapper = curr;
                                continue validationLoop;
                            }
                        }
                    }

                    if (id == null && this.argument.length != 0) {
                        isArgument = true;
                        for (int a = 0; a < this.argument.length; a++) {
                            var charc = string.charAt(b + a);
                            if (charc != this.argument[a]) {
                                matched = false;
                                isArgument = false;
                                break;
                            }
                        }
                    }

                    boolean isEnd = false;
                    if (!isArgument) {
                        matched = true;
                        isEnd = true;
                        for (int a = 0; a < this.end.length; a++) {
                            var charc = string.charAt(b + a);
                            if (charc != this.end[a]) {
                                matched = false;
                                isEnd = false;
                                break;
                            }
                        }
                    }

                    if (matched) {
                        var str = builder.toString();
                        if (id == null) {
                            if (provider.isValidTag(str, context)) {
                                id = str;
                                builder = new StringBuilder();
                                if (!isEnd) {
                                    continue;
                                }
                            } else {
                                continue main;
                            }
                        } else {
                            argument = str;
                        }

                        return new TagInfo(i, b + this.end.length, id, argument);
                    }

                    builder.append(curr);
                }
            }

            return null;
        }
    }

    protected record TagInfo(int start, int end, String id, String argument) {
    }
}
