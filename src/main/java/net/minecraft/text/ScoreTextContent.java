package net.minecraft.text;

public record ScoreTextContent(String name, String objective) implements TextContent {
    public String getName() {
        return name;
    }

    public String getObjective() {
        return objective;
    }
}
