package net.minecraft.server;

import net.minecraft.text.Text;

public class Metadata {
    public static final Metadata INSTANCE = new Metadata();

    public Text description() {
        return Text.literal("Not a Minecraft Server");
    }
}
