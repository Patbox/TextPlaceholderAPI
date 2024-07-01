package net.minecraft.text;

import com.google.common.io.Files;

import java.util.Optional;

public record SelectorTextContent(String pattern, Optional<Text> separator) implements TextContent {
    public String getPattern() {
        return this.pattern;
    }

    public Optional<Text> getSeparator() {
        return this.separator;
    }

    @Override
    public String getString() {
        return "<selector " + pattern + ">";
    }
}
