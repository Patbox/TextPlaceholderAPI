package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public record PlaceholderContext(MinecraftServer server,
                                 ServerCommandSource source,
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
        return new PlaceholderContext(server,  server.getCommandSource(), null, null, null, null);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        var name = profile.getName() != null ? profile.getName() : profile.getId().toString();
        return new PlaceholderContext(server, new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), server.getPermissionLevel(profile), name, Text.literal(name), server, null), null, null, null, profile);
    }

    public static PlaceholderContext of(ServerPlayerEntity player) {
        return new PlaceholderContext(player.getServer(), player.getCommandSource(), player.getWorld(), player, player, player.getGameProfile());
    }

    public static PlaceholderContext of(ServerCommandSource source) {
        return new PlaceholderContext(source.getServer(), source, source.getWorld(), source.getPlayer(), source.getEntity(), source.getPlayer() != null ? source.getPlayer().getGameProfile() : null);
    }

    public static PlaceholderContext of(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            return of(player);
        } else {
            return new PlaceholderContext(entity.getServer(), entity.getCommandSource(), (ServerWorld) entity.getWorld(), null, entity, null);
        }
    }
}
