package eu.pb4.placeholderstandalone;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;

public class Main {
    public static void main(String... args) {
        var parser = NodeParser.builder().quickText().globalPlaceholders().build();


        System.out.println(TextNode.asSingle(parser.parseNode("He<i>llo</> <rb>World</> <b>Tes</b>ting %player:name%")));
    }
}
