package eu.pb4.placeholders.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.text.*;
import net.minecraft.util.Pair;

import java.util.List;

public class GeneralUtils {
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

    public static Text toGradient(Text base, Int2ObjectFunction<TextColor> posToColor) {
        return recursiveGradient(base, posToColor, 0).getLeft();
    }

    private static Pair<Text, Integer> recursiveGradient(Text base, Int2ObjectFunction<TextColor> posToColor, int pos) {
        MutableText out = new LiteralText("").setStyle(base.getStyle());
        for (String letter : base.asString().split("")) {
            if (!letter.isEmpty()) {
                out.append(new LiteralText(letter).setStyle(Style.EMPTY.withColor(posToColor.apply(pos))));
                pos++;
            }
        }

        for (Text sibling : base.getSiblings()) {
            Pair<Text, Integer> pair = recursiveGradient(sibling, posToColor, pos);
            pos = pair.getRight();
            out.append(pair.getLeft());
        }

        return new Pair(out, pos);
    }

    public static int hvsToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6) % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return rgbToInt(value, t, p);
            case 1:
                return rgbToInt(q, value, p);
            case 2:
                return rgbToInt(p, value, t);
            case 3:
                return rgbToInt(p, q, value);
            case 4:
                return rgbToInt(t, p, value);
            case 5:
                return rgbToInt(value, p, q);
            default:
                return 0;
        }
    }

    public static int rgbToInt(float r, float g, float b) {
        return ((int) (r * 0xff)) << 16 | ((int) (g * 0xff)) << 8 | ((int) (b * 0xff));
    }

    public static GeneralUtils.HSV rgbToHsv(int rgb) {
        float b = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float g = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float r = (float) (rgb % 256) / 255;

        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float diff = cmax - cmin;
        float h = -1, s = -1;

        if (cmax == cmin) {
            h = 0;
        } else if (cmax == r) {
            h = (0.1666f * ((g - b) / diff) + 1) % 1;
        } else if (cmax == g) {
            h = (0.1666f * ((b - r) / diff) + 0.333f) % 1;
        } else if (cmax == b) {
            h = (0.1666f * ((r - g) / diff) + 0.666f) % 1;
        }
        if (cmax == 0) {
            s = 0;
        } else {
            s = (diff / cmax);
        }

        return new HSV(h, s, cmax);
    }

    public static record HSV(float h, float s, float v) {
    }
}
