package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.node.TextNode;

public interface NodeParser {
    TextNode[] parseNodes(TextNode input);
}
