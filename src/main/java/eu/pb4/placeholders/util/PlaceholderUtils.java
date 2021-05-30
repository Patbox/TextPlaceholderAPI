package eu.pb4.placeholders.util;

import eu.pb4.placeholders.PlaceholderContext;
import eu.pb4.placeholders.PlaceholderHandler;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtils {

    public static Text recursivePlaceholderParsing(Text text, Object object, Pattern pattern, Map<Identifier, PlaceholderHandler> placeholders) {
        MutableText out;

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
        } else {
            String string = text.asString();
            Matcher matcher = pattern.matcher(string);
            int start;
            int end;

            int previousEnd = 0;

            out = new LiteralText("").setStyle(text.getStyle());

            while (matcher.find()) {
                String placeholder = matcher.group("id");
                start = matcher.start();
                end = matcher.end();

                out.append(new LiteralText(string.substring(previousEnd, start)).setStyle(text.getStyle()));

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

            out.append(new LiteralText(string.substring(previousEnd)));
        }

        for(Text text1 : text.getSiblings()) {
            out.append(recursivePlaceholderParsing(text1, object, pattern, placeholders));
        }

        return out;
    }

    @Deprecated
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

    private static PlaceholderResult parsePlaceholderFromMap(PlaceholderContext context, Map<Identifier, PlaceholderHandler> placeholders) {
        if (placeholders.containsKey(context.getIdentifier())) {
            return placeholders.get(context.getIdentifier()).PlaceholderHandler(context);
        } else {
            return PlaceholderResult.invalid("Placeholder doesn't exist!");
        }
    }
}
