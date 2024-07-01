package net.minecraft.text;

import java.util.Map;

public class PlainTextContent {
    public static final Literal EMPTY = new Literal("");
    public record Literal(String string) implements TextContent {
        @Override
        public String getString() {
            return this.string;
        }
    }
}
