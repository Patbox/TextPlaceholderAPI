package eu.pb4.placeholders.impl.color;

import eu.pb4.placeholders.impl.GeneralUtils;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

// https://bottosson.github.io/posts/oklab/
public record OkLab(float l, float a, float b) {
    public static OkLab fromRgb(int rgb) {
        return fromLinearSRGB(ColorHelper.getRed(rgb) / 255f, ColorHelper.getGreen(rgb) / 255f,
                ColorHelper.getBlue(rgb) / 255f);
    }



    static float f(float x) {
        if (x >= 0.0031308)
            return (float) ((1.055) * Math.pow(x, (1.0/2.4)) - 0.055);
        else
            return (float) (12.92 * x);
    }

    static float f_inv(float x) {
        if (x >= 0.04045)
            return (float) Math.pow((x + 0.055)/(1 + 0.055), 2.4);
        else
            return x / 12.92f;
    }


    private static OkLab fromLinearSRGB(float r, float g, float b) {
        float l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b;
        float m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b;
        float s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b;

        float l_ = (float) Math.cbrt(l);
        float m_ = (float) Math.cbrt(m);
        float s_ = (float) Math.cbrt(s);

        return new OkLab(
                0.2104542553f*l_ + 0.7936177850f*m_ - 0.0040720468f*s_,
                1.9779984951f*l_ - 2.4285922050f*m_ + 0.4505937099f*s_,
                0.0259040371f*l_ + 0.7827717662f*m_ - 0.8086757660f*s_
                );
    }

    public int toRgb() {
        return toRgb(l, a, b);
    }

    public static int toRgb(float cL, float ca, float cb) {
        float l_ = cL + 0.3963377774f * ca + 0.2158037573f * cb;
        float m_ = cL - 0.1055613458f * ca - 0.0638541728f * cb;
        float s_ = cL - 0.0894841775f * ca - 1.2914855480f * cb;

        float l = l_*l_*l_;
        float m = m_*m_*m_;
        float s = s_*s_*s_;

        var r = +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s;
        var g = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s;
        var b = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s;

        var max = Math.max(Math.max(Math.max(r, g), b), 1);
        var min = Math.min(Math.min(Math.min(r, g), b), 0);
        float mult = 1;
        if (max > 1 || min < 0) {
            //mult = 1 / (max + min);
        }
        return GeneralUtils.rgbToInt(
                 MathHelper.clamp(r * mult, 0, 1),
                 MathHelper.clamp(g * mult, 0, 1),
                 MathHelper.clamp(b * mult, 0, 1)
        );
    }
}
