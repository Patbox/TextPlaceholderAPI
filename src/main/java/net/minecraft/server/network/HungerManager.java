package net.minecraft.server.network;

public class HungerManager {
    public static final HungerManager INSTANCE = new HungerManager();

    public float getFoodLevel() {
        return 10;
    }

    public float getSaturationLevel() {
        return 2;
    }
}
