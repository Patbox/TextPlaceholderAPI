package net.minecraft.text;

import java.util.Map;

public class PlainTextContent implements TextContent {
    public static final PlainTextContent EMPTY = new PlainTextContent();
    public static class Literal extends PlainTextContent {

        public String string() {
            return "";
        }
    }
}
