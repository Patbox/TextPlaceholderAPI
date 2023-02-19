package eu.pb4.placeholders.api.parsers;


import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record PatternPlaceholderParser(Pattern pattern, Function<String, @Nullable TextNode> placeholderProvider) implements NodeParser {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[%](?<id>[^%]+:[^%]+)[%]");
    public static final Pattern ALT_PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[{](?<id>[^{}]+:[^{}]+)[}]");

    public static final Pattern PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!((?<!(\\\\))\\\\))[%](?<id>[^%]+)[%]");
    public static final Pattern ALT_PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!((?<!(\\\\))\\\\))[{](?<id>[^{}]+)[}]");

    public static final Pattern PREDEFINED_PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))\\$[{](?<id>[^}]+)}");

    @Override
    public TextNode[] parseNodes(TextNode text) {
        if (text instanceof TranslatedNode translatedNode) {
            var list = new ArrayList<>();

            for (var arg : translatedNode.args()) {
                if (arg instanceof TextNode textNode) {
                    list.add(new ParentNode(this.parseNodes(textNode)));
                } else {
                    list.add(arg);
                }
            }

            return new TextNode[]{new TranslatedNode(translatedNode.key(), list.toArray())};

        } else if (text instanceof LiteralNode literalNode) {
            var out = new ArrayList<TextNode>();

            String string = literalNode.value();
            Matcher matcher = pattern.matcher(string);
            int start;
            int end;

            int previousEnd = 0;

            while (matcher.find()) {
                var placeholder = matcher.group("id");
                start = matcher.start();
                end = matcher.end();

                if (start != 0) {
                    out.add(new LiteralNode(string.substring(previousEnd, start)));
                }

                var output = this.placeholderProvider.apply(placeholder);

                if (output != null) {
                    out.add(output);
                } else {
                    out.add(new LiteralNode(matcher.group(0)));
                }

                previousEnd = end;
            }

            if (previousEnd != string.length()) {
                out.add(new LiteralNode(string.substring(previousEnd)));
            }

            return out.toArray(new TextNode[0]);
        }


        if (text instanceof ParentNode parentNode) {
            var out = new ArrayList<TextNode>();

            for (var text1 : parentNode.getChildren()) {
                out.add(new ParentNode(this.parseNodes(text1)));
            }

            return new TextNode[]{parentNode.copyWith(out.toArray(new TextNode[0]), this)};
        }

        return new TextNode[]{text};
    }
}
