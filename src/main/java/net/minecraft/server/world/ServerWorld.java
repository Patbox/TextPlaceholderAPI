package net.minecraft.server.world;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ServerWorld extends World {
    public static final ServerWorld INSTANCE = new ServerWorld();

    public RegistryKey getRegistryKey() {
        return new RegistryKey(Identifier.of("placeholder:world"));
    }

    public Collection<ServerPlayerEntity> getPlayers() {
        return List.of();
    }

    public ServerChunkManager getChunkManager() {
        return ServerChunkManager.INSTANCE;
    }
}
