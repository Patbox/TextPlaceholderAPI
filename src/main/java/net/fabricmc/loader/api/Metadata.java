package net.fabricmc.loader.api;

public record Metadata() {
    public static final Metadata INSTANCE = new Metadata();

    public Version getVersion() {
        return Version.INSTANCE;
    }

    public String getName() {
        return "Placeholder API";
    }

    public String getDescription() {
        return "Text parsin & stuff";
    }
}
