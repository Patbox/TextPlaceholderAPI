package net.minecraft.text;

import com.mojang.serialization.DataResult;
import net.minecraft.util.Formatting;

public record TextColor(int rgb) {
    public static TextColor fromRgb(int rgb) {
        return new TextColor(rgb);
    }
    public static TextColor fromFormatting(Formatting formatting) {
        return new TextColor(0);
    }

    public static DataResult<TextColor> parse(String s) {
        return DataResult.error(() -> "Not implemented");
    }

    public int getRgb() {
        return this.rgb;
    }
}
