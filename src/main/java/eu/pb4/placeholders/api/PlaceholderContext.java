package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.impl.placeholder.ViewObjectImpl;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public record PlaceholderContext(MinecraftServer server,
                                 ServerCommandSource source,
                                 @Nullable ServerWorld world,
                                 @Nullable ServerPlayerEntity player,
                                 @Nullable Entity entity,
                                 @Nullable GameProfile gameProfile,
                                 ViewObject view
) {

    public PlaceholderContext(MinecraftServer server,
                              ServerCommandSource source,
                              @Nullable ServerWorld world,
                              @Nullable ServerPlayerEntity player,
                              @Nullable Entity entity,
                              @Nullable GameProfile gameProfile) {
        this(server, source, world, player, entity, gameProfile, ViewObject.DEFAULT);
    }


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

    public PlaceholderContext withView(ViewObject view) {
        return new PlaceholderContext(this.server, this.source, this.world, this.player, this.entity, this.gameProfile, view);
    }

    public void addToContext(ParserContext context) {
        context.with(KEY, this);
    }


    public static PlaceholderContext of(MinecraftServer server) {
        return of(server, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(MinecraftServer server, ViewObject view) {
        return new PlaceholderContext(server,  server.getCommandSource(), null, null, null, null, view);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        return of(profile, server, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server, ViewObject view) {
        var name = profile.getName() != null ? profile.getName() : profile.getId().toString();
        return new PlaceholderContext(server, new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), server.getPermissionLevel(profile), name, Text.literal(name), server, null), null, null, null, profile);
    }

    public static PlaceholderContext of(ServerPlayerEntity player) {
        return of(player, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(ServerPlayerEntity player, ViewObject view) {
        return new PlaceholderContext(player.getServer(), player.getCommandSource(), player.getServerWorld(), player, player, player.getGameProfile(), view);
    }

    public static PlaceholderContext of(ServerCommandSource source) {
        return of(source, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(ServerCommandSource source, ViewObject view) {
        return new PlaceholderContext(source.getServer(), source, source.getWorld(), source.getPlayer(), source.getEntity(), source.getPlayer() != null ? source.getPlayer().getGameProfile() : null, view);
    }

    public static PlaceholderContext of(Entity entity) {
        return of(entity, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(Entity entity, ViewObject view) {
        if (entity instanceof ServerPlayerEntity player) {
            return of(player, view);
        } else {
            return new PlaceholderContext(entity.getServer(), entity.getCommandSource(), (ServerWorld) entity.getWorld(), null, entity, null, view);
        }
    }


    public interface ViewObject {
        ViewObject DEFAULT = of(new Identifier("placeholder_api", "default"));

        static ViewObject of(Identifier identifier) {
            return new ViewObjectImpl(identifier);
        }

        Identifier identifier();
    }
}
