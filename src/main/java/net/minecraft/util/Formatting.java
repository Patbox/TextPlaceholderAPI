package net.minecraft.util;

import java.util.Locale;

public enum Formatting {
    BLACK('0', 0x000000),
    DARK_BLUE('1', 0x0000AA),
    DARK_GREEN('2', 0x00AA00),
    DARK_AQUA('3', 0x00AAAA),
    DARK_RED('4', 0xAA0000),
    DARK_PURPLE('5', 0xAA00AA),
    GOLD('6', 0xFFAA00),
    GRAY('7', 0xAAAAAA),
    DARK_GRAY('8', 0x555555),
    BLUE('9', 0x5555FF),
    GREEN('a', 0x55FF55),
    AQUA('b', 0x55FFFF),
    RED('c', 0xFF5555),
    LIGHT_PURPLE('d', 0xFF55FF),
    YELLOW('e', 0xFFFF55),
    WHITE('f', 0xFFFFFF),
    OBFUSCATED('k', -1),
    BOLD('l', -1),
    STRIKETHROUGH('m', -1),
    UNDERLINE('n', -1),
    ITALIC('o', -1),
    RESET('r', -1),
    ;

    private final char letter;
    private final int color;

    private Formatting(char letter, int color) {
        this.letter = letter;
        this.color = color;
    }

    public boolean isColor() {
        return !isModifier();
    }

    public char getCode() {
        return this.letter;
    }

    public boolean isModifier() {
        return this.color < 0;
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int getColor() {
        return this.color;
    }
}
