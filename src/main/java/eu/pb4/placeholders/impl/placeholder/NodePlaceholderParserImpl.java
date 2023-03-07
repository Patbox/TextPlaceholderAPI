package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated(forRemoval = true)
@ApiStatus.Internal
public class NodePlaceholderParserImpl {
    public static TextNode[] recursivePlaceholderParsing(ParserContext.Key<PlaceholderContext> contextKey, TextNode text, Pattern pattern, Placeholders.PlaceholderGetter placeholders, NodeParser parser) {
        return PatternPlaceholderParser.of(pattern, contextKey, placeholders).parseNodes(text);
    }
}
