package net.minecraft.server.command;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ServerCommandSource {
    public ServerCommandSource() {

    }
    public ServerCommandSource(CommandOutput dummy, Vec3d zero, Vec2f zero1, ServerWorld overworld, int permissionLevel, String name, MutableText literal, MinecraftServer server, Object o) {
    }

    public MinecraftServer getServer() {
        return MinecraftServer.INSTANCE;
    }

    public ServerWorld getWorld() {
        return ServerWorld.INSTANCE;
    }

    @Nullable
    public ServerPlayerEntity getPlayer() {
        return null;
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }
}
