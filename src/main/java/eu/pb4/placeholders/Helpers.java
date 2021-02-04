package eu.pb4.placeholders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;

public class Helpers {
    public static String textToString(Text text) {
        StringBuffer string = new StringBuffer(text.asString());
        recursiveParsing(string, text.getSiblings());
        return string.toString();
    }

    public static String durationToString(long x) {
        long seconds = x % 60;
        long minutes = (x / 60) % 60;
        long hours = (x / (60 * 60)) % 24;
        long days = x / (60 * 60 * 24);

        if (days > 0) {
            return String.format("%dd%dh%dm%ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh%dm%ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm%ds", minutes, seconds);
        } else if (seconds > 0) {
            return String.format("%ds", seconds);
        } else {
            return "---";
        }
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

        MutableText out = new LiteralText("").setStyle(text.getStyle());
        int start;
        int end;

        int previousEnd = 0;

        ServerPlayerEntity player = object instanceof ServerPlayerEntity ? (ServerPlayerEntity) object : null;
        MinecraftServer server = !(object instanceof ServerPlayerEntity) ? (MinecraftServer) object : player.server;

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            start = matcher.start();
            end = matcher.end();

            out.append(new LiteralText(string.substring(previousEnd, start)).setStyle(text.getStyle()));

            PlaceholderResult result = player != null
                    ? PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, player))
                    : PlaceholderAPI.parsePlaceholder(PlaceholderContext.create(placeholder, server));

            if (result.isValid()) {
                out.append(result.getText());
            } else {
                out.append(new LiteralText(matcher.group(0)));
            }

            previousEnd = end;
        }

        out.append(new LiteralText(string.substring(previousEnd)));

        for(Text text1 : text.getSiblings()) {
            out.append(recursivePlaceholderParsing(text1, object));
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
