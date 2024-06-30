package net.minecraft.server;

public class TickManager {
    public static final TickManager INSTANCE = new TickManager();

    public int getMillisPerTick() {
        return 50;
    }
}
