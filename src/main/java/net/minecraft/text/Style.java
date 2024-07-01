package net.minecraft.text;

import com.mojang.serialization.Codec;
import eu.pb4.placeholderstandalone.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record Style(@Nullable TextColor color,
                    @Nullable Boolean italic,
                    @Nullable Boolean bold,
                    @Nullable Boolean underlined,
                    @Nullable Boolean strikethrough,
                    @Nullable Boolean obfuscated,
                    @Nullable HoverEvent<?> hoverEvent,
                    @Nullable ClickEvent clickEvent,
                    @Nullable String insertion,
                    @Nullable Identifier font
) {
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);

    private static <T> T or(T a, T b) {
        return a != null ? a : b;
    }

    public Style withColor(Formatting formatting) {
        return switch (formatting) {
            case BOLD ->
                    new Style(color, italic, true, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
            case ITALIC ->
                    new Style(color, true, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
            case STRIKETHROUGH ->
                    new Style(color, italic, bold, underlined, true, obfuscated, hoverEvent, clickEvent, insertion, font);
            case UNDERLINE ->
                    new Style(color, italic, bold, true, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
            case OBFUSCATED ->
                    new Style(color, italic, bold, underlined, strikethrough, true, hoverEvent, clickEvent, insertion, font);
            case RESET -> EMPTY;
            default ->
                    new Style(TextColor.fromFormatting(formatting), italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
        };
    }

    public Style withColor(TextColor color) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withColor(int color) {
        return new Style(TextColor.fromRgb(color), italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withItalic(Boolean italic) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withHoverEvent(HoverEvent hoverEvent) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withClickEvent(ClickEvent c) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withInsertion(String insertion) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withFont(Identifier font) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withBold(Boolean bold) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withUnderline(Boolean underlined) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withStrikethrough(Boolean strikethrough) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    public Style withObfuscated(Boolean obfuscated) {
        return new Style(color, italic, bold, underlined, strikethrough, obfuscated, hoverEvent, clickEvent, insertion, font);
    }

    @Nullable
    public TextColor getColor() {
        return this.color;
    }

    @Nullable
    public HoverEvent<?> getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable

    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public Style withParent(Style style) {
        return new Style(or(color, style.color), or(italic, style.italic), or(bold, style.bold),
                or(underlined, style.underlined), or(strikethrough, style.strikethrough), or(obfuscated, style.obfuscated),
                or(hoverEvent, style.hoverEvent), or(clickEvent, style.clickEvent), or(insertion, style.insertion), or(font, style.font));
    }

    public Style withFormatting(Formatting[] formatting) {
        var s = this;
        for (var f : formatting) {
            s = s.withColor(f);
        }
        return s;
    }

    public static class Codecs {

        public static final Codec<Style> CODEC = TextCodecs.STYLE_CODEC.codec();
    }
}
