package net.minecraft.text;

import com.google.common.io.BaseEncoding;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Style {
    public static final Style EMPTY = new Style();

    public Style withColor(Formatting formatting) {
        return this;
    }
    public Style withColor(TextColor color) {
        return this;
    }

    public Style withColor(int color) {
        return this;
    }

    public Style withItalic(Boolean italic) {
        return this;
    }

    public Style withHoverEvent(HoverEvent hoverEvent) {
        return this;
    }

    public Style withClickEvent(ClickEvent o) {
        return this;
    }

    public Style withInsertion(String o) {
        return this;
    }

    public Style withFont(Identifier o) {
        return this;
    }

    public Style withBold(Boolean o) {
        return this;
    }

    public Style withUnderline(Boolean o) {
        return this;
    }

    public Style withStrikethrough(Boolean o) {
        return this;
    }

    @Nullable
    public TextColor getColor() {
        return null;
    }
    @Nullable
    public HoverEvent<?> getHoverEvent() {
        return null;
    }
    @Nullable

    public ClickEvent getClickEvent() {
        return null;
    }

    @Nullable
    public String getInsertion() {
        return null;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public Style withParent(Style style) {
        return this;
    }

    public Style withFormatting(Formatting[] formatting) {
        return this;
    }

    public Style withObfuscated(Boolean b) {
        return this;
    }

    public static class Codecs {

        public static final Codec<Style> CODEC = Codec.unit(Style.EMPTY);
    }
}
