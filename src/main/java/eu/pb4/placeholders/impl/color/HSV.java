package eu.pb4.placeholders.impl.color;

import eu.pb4.placeholders.impl.GeneralUtils;

public record HSV(float h, float s, float v) {
    public static HSV fromRgb(int rgb) {
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

    public int toRgb() {
        return toRgb(h, s, v);
    }

    public static int toRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6) % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        return switch (h) {
            case 0 -> GeneralUtils.rgbToInt(value, t, p);
            case 1 -> GeneralUtils.rgbToInt(q, value, p);
            case 2 -> GeneralUtils.rgbToInt(p, value, t);
            case 3 -> GeneralUtils.rgbToInt(p, q, value);
            case 4 -> GeneralUtils.rgbToInt(t, p, value);
            case 5 -> GeneralUtils.rgbToInt(value, p, q);
            default -> 0;
        };
    }
}
