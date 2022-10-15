package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.NodeParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class NodePlaceholderParserImpl {

    public static TextNode[] recursivePlaceholderParsing(TextNode text, Pattern pattern, Placeholders.PlaceholderGetter placeholders, NodeParser parser) {
        if (text instanceof TranslatedNode translatedNode) {
            var list = new ArrayList<>();

            for(var arg : translatedNode.args()) {
                if (arg instanceof TextNode textNode) {
                    list.add(new ParentNode(recursivePlaceholderParsing(textNode, pattern, placeholders, parser)));
                } else {
                    list.add(arg);
                }
            }

            return new TextNode[] { new TranslatedNode(translatedNode.key(), list.toArray()) };

        } else if (text instanceof LiteralNode literalNode) {
            var out = new ArrayList<TextNode>();

            String string = literalNode.value();
            Matcher matcher = pattern.matcher(string);
            int start;
            int end;

            int previousEnd = 0;

            while (matcher.find()) {
                var placeholder = matcher.group("id").split(" ", 2);
                start = matcher.start();
                end = matcher.end();

                if (start != 0) {
                    out.add(new LiteralNode(string.substring(previousEnd, start)));
                }

                if (placeholders.exists(placeholder[0])) {
                    out.add(new PlaceholderNode(placeholder[0], placeholders, placeholders.isContextOptional(), placeholder.length == 2 ? placeholder[1] : null));
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

            for(var text1 : parentNode.getChildren()) {
                out.add(new ParentNode(recursivePlaceholderParsing(text1, pattern, placeholders, parser)));
            }

            if (text instanceof HoverNode<?,?> hoverNode && hoverNode.action() == HoverNode.Action.TEXT) {
                return new TextNode[] { new HoverNode<>(out.toArray(new TextNode[0]), HoverNode.Action.TEXT, (ParentNode) recursivePlaceholderParsing((TextNode) hoverNode.value(), pattern, placeholders, parser)[0]) };
            } else if (text instanceof ClickActionNode clickActionNode) {
                return new TextNode[] { new ClickActionNode(out.toArray(new TextNode[0]), clickActionNode.action(), TextNode.asSingle(recursivePlaceholderParsing(clickActionNode.value(), pattern, placeholders, parser))) };
            } else if (text instanceof InsertNode insertNode) {
                return new TextNode[] { new InsertNode(out.toArray(new TextNode[0]), TextNode.asSingle(recursivePlaceholderParsing(insertNode.value(), pattern, placeholders, parser))) };
            } else if (text instanceof StyledNode node) {
                var style = node.rawStyle();
                var hoverValue = node.hoverValue();
                var clickValue = node.clickValue();
                var insertion = node.insertion();

                if (hoverValue != null) {
                    hoverValue = new ParentNode(recursivePlaceholderParsing(hoverValue, pattern, placeholders, parser));
                }

                if (clickValue != null) {
                    clickValue = TextNode.asSingle(recursivePlaceholderParsing(hoverValue, pattern, placeholders, parser));
                }

                if (insertion != null) {
                    insertion = TextNode.asSingle(recursivePlaceholderParsing(hoverValue, pattern, placeholders, parser));
                }


                return new TextNode[] { new StyledNode(out.toArray(new TextNode[0]), style, hoverValue, clickValue, insertion) };
            }

            return new TextNode[] { parentNode.copyWith(out.toArray(new TextNode[0])) };
        }

        return new TextNode[] { text };
    }

    /*private static TemplateStyle parsePlaceholdersInStyle(TemplateStyle style, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        if (style == null) {
            return null;
        }

        TemplateStyle out = style;

        if (style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            out = out.withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    recursivePlaceholderParsing(style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT), object, pattern, placeholders))
            );
        }

        if (style.getClickEvent() != null) {
            out = out.withClickEvent(new ClickEvent(style.getClickEvent().getAction(),
                    parseString(style.getClickEvent().getValue(), object, pattern, placeholders)));
        }


        return out;
    }

    public static String parseString(String text, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder out = new StringBuilder(text.length());

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            PlaceholderResult result = player != null
                    ? parsePlaceholderFromMap(placeholder, PlaceholderContext.create(placeholder, player), placeholders)
                    : parsePlaceholderFromMap(placeholder, PlaceholderContext.create(placeholder, server), placeholders);

            if (result.isValid()) {
                matcher.appendReplacement(out, Matcher.quoteReplacement(result.getString()));
            }
        }

        matcher.appendTail(out);

        return out.toString();
    }*/
}
