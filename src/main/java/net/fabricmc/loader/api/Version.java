package net.fabricmc.loader.api;

public record Version(String version) {
    public static final Version INSTANCE = new Version("1.2.3");

    public String getFriendlyString() {
        return this.version;
    }
}
