package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.impl.placeholder.PlaceholderNode;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

@ApiStatus.Experimental
public record TagLikeParser(Format format, Provider provider) implements NodeParser {
    private static final TextNode[] EMPTY = new TextNode[0];
    public static final Format TAGS = new Format('<', '>', ' ');
    public static final Format TAGS_LEGACY = new Format('<', '>', ':');
    public static final Format PLACEHOLDER = new Format('%', '%', ' ');
    public static final Format PLACEHOLDER_ALTERNATIVE = new Format('{', '}', ' ');
    public static final Format PLACEHOLDER_USER = new Format("${", "}", "");

    @Deprecated
    public static TagLikeParser wrapEmulate(TextParserV1 parser) {
        return new TagLikeParser(TAGS_LEGACY, new Provider() {
            @Override
            public boolean isValidTag(String tag, Stack<Scope> stack) {
                if (tag.equals("/") || (stack.peek().id != null && tag.equals("/" + stack.peek().id)) || tag.startsWith("#") || tag.equals("r") || tag.equals("reset")) {
                    return true;
                }

                return parser.getTagParser(tag) != null;
            }

            @Override
            public void handleTag(String id, String argument, Stack<Scope> stack, NodeParser self) {
                if ((id.equals("/") && stack.size() > 1) || id.equals("/" + stack.peek().id)) {
                    var curr = stack.pop();
                    stack.peek().nodes.add(curr.collapse(self));
                    return;
                }

                if (id.equals("r") || id.equals("reset")) {
                    while (stack.size() != 1) {
                        var curr = stack.pop();
                        stack.peek().nodes.add(curr.collapse(self));
                    }
                    return;
                }

                if (id.startsWith("#")) {
                    var text = TextColor.parse(id);
                    if (text.result().isPresent()) {
                        stack.push(Scope.enclosing(id, x -> new ColorNode(x, text.result().get())));
                    }
                    return;
                }


                var tag = parser.getTag(id);

                var x = tag.parser().parseString(id, argument, "<" + id + ":" + argument + ">", parser::getTagParser, null);

                if (x.length() != 0 && x.node() instanceof ParentTextNode parentTextNode) {
                    stack.push(Scope.enclosingParsed(id, parentTextNode::copyWith));
                } else {
                    stack.peek().nodes.add(x.node());
                }
            }
        });
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        var stack = new Stack<Scope>();
        stack.push(Scope.parent());

        parse(input, stack);

        while (!stack.isEmpty()) {
            var box = stack.pop();

            if (stack.isEmpty()) {
                return box.nodes().toArray(EMPTY);
            }

            stack.peek().nodes.add(box.collapse(this));
        }

        return new TextNode[] { input };
    }

    private void parse(TextNode node, Stack<Scope> stack) {
        if (node instanceof LiteralNode literal) {
            int pos = 0;
            var string = literal.value();

            while (true) {
                var tag = this.format.find(string, pos, provider, stack);
                if (tag == null) {
                    stack.peek().nodes.add(new LiteralNode(string.substring(pos)));
                    break;
                } else {
                    stack.peek().nodes.add(new LiteralNode(string.substring(pos, tag.start)));
                }
                pos = tag.end;
                
                this.provider.handleTag(tag.id, tag.argument, stack, this);
            }
        } else if (node instanceof TranslatedNode translatedNode) {
            stack.peek().nodes.add(translatedNode.transform(this));
        } else if (node instanceof ParentTextNode parent) {
            var box = Scope.enclosingParsed(parent::copyWith);
            stack.push(box);
            for (var x : parent.getChildren()) {
                parse(x, stack);
            }
            while (stack.peek() != box) {
                var curr = stack.pop();
                stack.peek().nodes.add(curr.collapse(this));
            }
            stack.peek().nodes.add(stack.pop().collapse(this));
        } else {
            stack.peek().nodes.add(node);
        }
    }

    public record Scope(@Nullable String id, List<TextNode> nodes, BiFunction<TextNode[], NodeParser, TextNode> merger) {
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

    public interface Provider {
        boolean isValidTag(String tag, Stack<Scope> stack);
        void handleTag(String id, String argument, Stack<Scope> stack, NodeParser parser);

        static Provider placeholder(ParserContext.Key<PlaceholderContext> contextKey, Placeholders.PlaceholderGetter placeholders) {
            return new Provider() {
                @Override
                public boolean isValidTag(String tag, Stack<Scope> stack) {
                    return placeholders.exists(tag);
                }

                @Override
                public void handleTag(String id, String argument, Stack<Scope> stack, NodeParser parser) {
                    stack.peek().nodes.add(new PlaceholderNode(contextKey, id, placeholders,
                            placeholders.isContextOptional(), argument != null && !argument.isEmpty() ? argument : null));
                }
            };
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
        TagInfo find(String string, int start, Provider provider, Stack<Scope> stack) {
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
                                continue;
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
                            if (provider.isValidTag(str, stack)) {
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

                        if (isEnd) {
                            return new TagInfo(i, b + this.end.length, id, argument);
                        }
                        continue;
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
