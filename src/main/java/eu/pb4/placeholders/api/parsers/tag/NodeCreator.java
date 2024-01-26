package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;

import java.util.function.Function;

public interface NodeCreator {
    TextNode createTextNode(TextNode[] nodes, StringArgs arg, NodeParser parser);

    static NodeCreator self(Function<StringArgs, TextNode> function) {
        return (a, b, c) -> function.apply(b);
    }

    static NodeCreator bool(BoolNodeArg function) {
        return (a, b, c) -> function.apply(a, SimpleArguments.bool(b.get("value", 0), true));
    }

    interface BoolNodeArg {
        TextNode apply(TextNode[] nodes, boolean argument);
    }
}
