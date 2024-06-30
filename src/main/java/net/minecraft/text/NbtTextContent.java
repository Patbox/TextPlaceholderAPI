package net.minecraft.text;

import com.google.common.io.Files;

import java.util.Optional;

public record NbtTextContent(String path, boolean shouldInterpret, Optional<Text> separator, NbtDataSource dataSource) implements TextContent {
    public String getPath() {
        return path;
    }


    public Optional<Text> getSeparator() {
        return separator;
    }


    public NbtDataSource getDataSource() {
        return dataSource;
    }
}
