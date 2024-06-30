package net.minecraft.entity;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public class Entity {
    private final static UUID ENTITY_ID = UUID.fromString("Placeholder API");



    public UUID getUuid() {
        return ENTITY_ID;
    }
    public ServerWorld getServerWorld() {
        return ServerWorld.INSTANCE;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource();
    }

    public MinecraftServer getServer() {
        return MinecraftServer.INSTANCE;
    }

    public World getWorld() {
        return getServerWorld();
    }
}
