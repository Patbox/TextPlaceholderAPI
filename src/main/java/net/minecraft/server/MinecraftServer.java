package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class MinecraftServer {
    public static final MinecraftServer INSTANCE = new MinecraftServer();

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource();
    }

    public int getPermissionLevel(GameProfile profile) {
        return 0;
    }

    public ServerWorld getOverworld() {
        return ServerWorld.INSTANCE;
    }

    public float getAverageTickTime() {
        return 25;
    }

    public TickManager getTickManager() {
        return TickManager.INSTANCE;
    }

    public long getTicks() {
        return 100;
    }

    public String getVersion() {
        return "1.21";
    }

    public Metadata getServerMetadata() {
        return Metadata.INSTANCE;
    }

    public String getServerModName() {
        return "Text Placeholder API Standalone";
    }

    public String getName() {
        return "world";
    }

    public PlayerManager getPlayerManager() {
        return PlayerManager.INSTANCE;
    }

    public ServerScoreboard getScoreboard() {
        return ServerScoreboard.INSTANCE;
    }
}
