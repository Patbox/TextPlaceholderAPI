package eu.pb4.placeholders.util;

import eu.pb4.placeholders.PlaceholderContext;
import eu.pb4.placeholders.PlaceholderHandler;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtils {

    public static Text recursivePlaceholderParsing(Text text, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        MutableText out = null;

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        if (text instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText) text;
            ArrayList<Object> list = new ArrayList<>();

            for(Object arg : translatableText.getArgs()) {
                if (arg instanceof Text) {
                    list.add(recursivePlaceholderParsing((Text) arg, object, pattern, placeholders));
                } else {
                    list.add(arg);
                }
            }

            out = new TranslatableText(translatableText.getKey(), list.toArray());
            out.setStyle(parsePlaceholdersInStyle(text.getStyle(), object, pattern, placeholders));
        } else {
            String string = text.asString();
            Matcher matcher = pattern.matcher(string);
            int start;
            int end;

            int previousEnd = 0;

            while (matcher.find()) {
                String placeholder = matcher.group("id");
                start = matcher.start();
                end = matcher.end();

                if (out == null) {
                    out = new LiteralText(string.substring(previousEnd, start)).setStyle(parsePlaceholdersInStyle(text.getStyle(), object, pattern, placeholders));
                } else {
                    out.append(new LiteralText(string.substring(previousEnd, start)));
                }

                PlaceholderResult result = player != null
                        ? parsePlaceholderFromMap(PlaceholderContext.create(placeholder, player), placeholders)
                        : parsePlaceholderFromMap(PlaceholderContext.create(placeholder, server), placeholders);

                if (result.isValid()) {
                    out.append(result.getText());
                } else {
                    out.append(new LiteralText(matcher.group(0)));
                }

                previousEnd = end;
            }

            if (out == null) {
                out = new LiteralText(string.substring(previousEnd)).setStyle(parsePlaceholdersInStyle(text.getStyle(), object, pattern, placeholders));
            } else {
                out.append(new LiteralText(string.substring(previousEnd)));
            }
        }

        for(Text text1 : text.getSiblings()) {
            out.append(recursivePlaceholderParsing(text1, object, pattern, placeholders));
        }

        return out;
    }

    private static Style parsePlaceholdersInStyle(Style style, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        Style out = style;

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

    public static Text recursivePredefinedPlaceholderParsing(Text text, Pattern pattern, Map<String, Text> placeholders) {
        MutableText out = null;

        if (text instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText) text;
            ArrayList<Object> list = new ArrayList<>();

            for(Object arg : translatableText.getArgs()) {
                if (arg instanceof Text) {
                    list.add(recursivePredefinedPlaceholderParsing((Text) arg, pattern, placeholders));
                } else {
                    list.add(arg);
                }
            }

            out = new TranslatableText(translatableText.getKey(), list.toArray());
            out.setStyle(parsePredefinedPlaceholdersInStyle(text.getStyle(), pattern, placeholders));
        } else {
            String string = text.asString();
            Matcher matcher = pattern.matcher(string);
            int start;
            int end;

            int previousEnd = 0;

            while (matcher.find()) {
                String placeholder = matcher.group("id");
                start = matcher.start();
                end = matcher.end();

                if (out == null) {
                    out = new LiteralText(string.substring(previousEnd, start)).setStyle(parsePredefinedPlaceholdersInStyle(text.getStyle(), pattern, placeholders));
                } else {
                    out.append(new LiteralText(string.substring(previousEnd, start)));
                }

                Text result = placeholders.get(placeholder);

                if (result != null) {
                    out.append(result.shallowCopy());
                } else {
                    out.append(new LiteralText(matcher.group(0)));
                }

                previousEnd = end;
            }

            if (out == null) {
                out = new LiteralText(string.substring(previousEnd)).setStyle(parsePredefinedPlaceholdersInStyle(text.getStyle(), pattern, placeholders));
            } else {
                out.append(new LiteralText(string.substring(previousEnd)));
            }
        }

        for(Text text1 : text.getSiblings()) {
            out.append(recursivePredefinedPlaceholderParsing(text1, pattern, placeholders));
        }

        return out;
    }

    private static Style parsePredefinedPlaceholdersInStyle(Style style, Pattern pattern, Map<String, Text> placeholders) {
        Style out = style;

        if (style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            out = out.withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    recursivePredefinedPlaceholderParsing(style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT), pattern, placeholders))
            );
        }

        if (style.getClickEvent() != null) {
            out = out.withClickEvent(new ClickEvent(style.getClickEvent().getAction(),
                    parseStringPredefined(style.getClickEvent().getValue(), pattern, placeholders)));
        }


        return out;
    }

    public static String parseString(String text, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer out = new StringBuffer(text.length());

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            PlaceholderResult result = player != null
                    ? parsePlaceholderFromMap(PlaceholderContext.create(placeholder, player), placeholders)
                    : parsePlaceholderFromMap(PlaceholderContext.create(placeholder, server), placeholders);

            if (result.isValid()) {
                matcher.appendReplacement(out, Matcher.quoteReplacement(result.getString()));
            }
        }

        matcher.appendTail(out);

        return out.toString();
    }

    public static String parseStringPredefined(String text, Pattern pattern, Map<String, Text> placeholders) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer out = new StringBuffer(text.length());

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            Text result = placeholders.get(placeholder);

            if (result != null) {
                matcher.appendReplacement(out, Matcher.quoteReplacement(result.getString()));
            }
        }

        matcher.appendTail(out);

        return out.toString();
    }

    private static PlaceholderResult parsePlaceholderFromMap(PlaceholderContext context, Map<Identifier, PlaceholderHandler> placeholders) {
        if (placeholders.containsKey(context.getIdentifier())) {
            return placeholders.get(context.getIdentifier()).PlaceholderHandler(context);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }
}
