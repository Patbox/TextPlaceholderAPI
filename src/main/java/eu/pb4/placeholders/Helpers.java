package eu.pb4.placeholders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;

class Helpers {
    public static String textToString(Text text) {
        StringBuffer string = new StringBuffer(text.asString());
        recursiveParsing(string, text.getSiblings());
        return string.toString();
    }


    private static void recursiveParsing(StringBuffer string, List<Text> textList) {
        for (Text text : textList) {
            string.append(text.asString());

            List<Text> siblings = text.getSiblings();
            if (siblings.size() != 0) {
                recursiveParsing(string, siblings);
            }
        }
    }

    protected static Text recursivePlaceholderParsing(Text text, Object object) {
        String string = text.asString();
        Matcher matcher = PlaceholderAPI.PLACEHOLDER_PATTERN.matcher(string);

        MutableText out = null;
        int start;
        int end;

        int previousStart = 0;
        int previousEnd = 0;

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            start = matcher.start();
            end = matcher.end();

            if (out == null) {
                out = new LiteralText(string.substring(previousStart, start));
                out.setStyle(text.getStyle());
            } else {
                out.append(new LiteralText(string.substring(previousEnd, start)).setStyle(text.getStyle()));
            }

            PlaceholderResult result = player != null
                    ? PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, player))
                    : PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, server));

            if (result.isValid()) {
                out.append(result.getText());
            } else {
                out.append(new LiteralText(matcher.group(0)).setStyle(text.getStyle()));
            }

            previousStart = start;
            previousEnd = end;
        }

        if (out == null) {
            out = text.copy();
        } else {
            out.append(new LiteralText(string.substring(previousEnd)).setStyle(text.getStyle()));
        }

        for(Text text1 : text.getSiblings()) {
            out.append(recursivePlaceholderParsing(text1, object)).setStyle(text.getStyle());
        }
        return out;
    }

    public static String parseString(String text, Object object) {
        Matcher matcher = PlaceholderAPI.PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer out = new StringBuffer(text.length());

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            PlaceholderResult result =player != null
                    ? PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, player))
                    : PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, server));

            if (result.isValid()) {
                matcher.appendReplacement(out, Matcher.quoteReplacement(result.getString()));
            }
        }

        matcher.appendTail(out);

        return out.toString();
    }
}
