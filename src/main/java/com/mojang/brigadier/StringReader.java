package com.mojang.brigadier;

public class StringReader {
    private int pos = 0;
    private final String value;

    public StringReader(String value) {
        this.value = value;
    }

    public boolean canRead() {
        return this.pos < this.value.length();
    }
    public boolean canRead(int amount) {
        return this.pos + amount < this.value.length();
    }

    public char read() {
        return this.value.charAt(this.pos++);
    }

    public void setCursor(int i) {
        this.pos = i;
    }

    public int getCursor() {
        return this.pos;
    }

    public String getRemaining() {
        return canRead() ? this.value.substring(this.pos) : "";
    }
}
