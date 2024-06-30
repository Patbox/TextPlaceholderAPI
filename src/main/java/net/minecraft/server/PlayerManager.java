package net.minecraft.server;

public class PlayerManager {
    public static final PlayerManager INSTANCE = new PlayerManager();

    public int getCurrentPlayerCount() {
        return 0;
    }

    public int getMaxPlayerCount() {
        return 20;
    }
}
