package net.minecraft.text;

import com.mojang.serialization.DataResult;
import eu.pb4.placeholderstandalone.TextCodecs;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public record TextColor(int rgb, @Nullable Formatting formatting) {
    public static TextColor fromRgb(int rgb) {
        return new TextColor(rgb, null);
    }
    public static TextColor fromFormatting(Formatting formatting) {
        return new TextColor(formatting.getColor(), formatting);
    }

    public static DataResult<TextColor> parse(String s) {
        try {
            if (s.startsWith("#")) {
                return DataResult.success(TextColor.fromRgb(Integer.parseInt(s.substring(1), 16)));
            }

            return DataResult.success(TextColor.fromFormatting(Formatting.valueOf(s.toUpperCase(Locale.ROOT))));
        } catch (Throwable ignored) {}


        return DataResult.error(() -> "Invalid color: " + s);
    }



    public String name() {
        if (formatting != null) {
            return formatting.getName();
        }
        return "#" + Integer.toString(rgb, 16);
    }

    public int getRgb() {
        return this.rgb;
    }
}
