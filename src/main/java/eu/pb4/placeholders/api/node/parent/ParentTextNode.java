package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;

import java.util.Collection;

public interface ParentTextNode extends TextNode {
    TextNode[] getChildren();

    ParentTextNode copyWith(TextNode[] children);

    default ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return copyWith(children);
    }

    @FunctionalInterface
    interface Constructor {
        ParentTextNode createNode(String definition, Collection<ParentTextNode> children);
    }
}
