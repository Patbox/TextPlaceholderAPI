package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import javax.annotation.Nullable;

public record PlaceholderContext(MinecraftServer server,
                                 @Nullable ServerWorld world,
                                 @Nullable ServerPlayerEntity player,
                                 @Nullable Entity entity,
                                 @Nullable GameProfile gameProfile
) {
    public static ParserContext.Key<PlaceholderContext> KEY = new ParserContext.Key<>("placeholder_context", PlaceholderContext.class);

    public boolean hasWorld() {
        return this.world != null;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }

    public boolean hasGameProfile() {
        return this.gameProfile != null;
    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    public ParserContext asParserContext() {
        return ParserContext.of(KEY, this);
    }

    public static PlaceholderContext of(MinecraftServer server) {
        return new PlaceholderContext(server,null, null, null, null);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        return new PlaceholderContext(server, null, null, null, profile);
    }

    public static PlaceholderContext of(ServerPlayerEntity player) {
        return new PlaceholderContext(player.getServer(), player.getWorld(), player, player, player.getGameProfile());
    }

    public static PlaceholderContext of(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            return new PlaceholderContext(entity.getServer(), player.getWorld(), player, entity, player.getGameProfile());
        } else {
            return new PlaceholderContext(entity.getServer(), (ServerWorld) entity.getWorld(), null, entity, null);
        }
    }
}
