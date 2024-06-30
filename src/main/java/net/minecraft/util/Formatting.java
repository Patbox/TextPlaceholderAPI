package net.minecraft.util;

public enum Formatting {
    LIGHT_PURPLE, RED, GOLD, GREEN,
    GRAY, BLUE, UNDERLINE, STRIKETHROUGH, BOLD, ITALIC, WHITE, DARK_PURPLE, DARK_GRAY;

    public boolean isColor() {
        return false;
    }

    public char getCode() {
        return ' ';
    }

    public boolean isModifier() {
        return true;
    }

    public String getName() {
        return "";
    }
}
