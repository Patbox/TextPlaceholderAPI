package eu.pb4.placeholders.impl.color;

import net.minecraft.util.Mth;

// https://bottosson.github.io/posts/oklab/
public record OkLch(float l, float c, float h) {
    public static OkLch fromRgb(int rgb) {
        var lab = OkLab.fromRgb(rgb);
        var c = Mth.sqrt(lab.a() * lab.a() + lab.b() + lab.b());
        var h = (float) Mth.atan2(lab.b(), lab.a());

        return new OkLch(lab.l(), c, h);
    }

    public float a() {
        return c * Mth.cos(h);
    }

    public float b() {
        return c * Mth.sin(h);
    }

    public int toRgb() {
        return OkLab.toRgb(l, this.a(), this.b());
    }

    public static int toRgb(float l, float c, float h) {
        return OkLab.toRgb(l, (float) (c * Math.cos(h)), (float) (c * Math.sin(h)));
    }
}
