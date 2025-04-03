package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.impl.placeholder.ViewObjectImpl;
import net.minecraft.class_10961;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public record PlaceholderContext(MinecraftServer server,
                                 Supplier<ServerCommandSource> lazySource,
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
                              @Nullable GameProfile gameProfile,
                              ViewObject view
    ) {
        this(server, () -> source, world, player, entity, gameProfile, view);
    }

    public ServerCommandSource source() {
        return this.lazySource.get();
    }

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
        return ParserContext.of(KEY, this).with(ParserContext.Key.WRAPPER_LOOKUP, this.server.method_70562().method_69003());
    }

    public PlaceholderContext withView(ViewObject view) {
        return new PlaceholderContext(this.server, this.lazySource, this.world, this.player, this.entity, this.gameProfile, view);
    }

    public void addToContext(ParserContext context) {
        context.with(KEY, this);
        context.with(ParserContext.Key.WRAPPER_LOOKUP, this.server.method_70562().method_69003());
    }


    public static PlaceholderContext of(MinecraftServer server) {
        return of(server, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(MinecraftServer server, ViewObject view) {
        class_10961 gameInstance = server.method_70562();
        return new PlaceholderContext(server, gameInstance::method_68953, null, null, null, null, view);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        return of(profile, server, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server, ViewObject view) {
        var name = profile.getName() != null ? profile.getName() : profile.getId().toString();
        return new PlaceholderContext(server, () -> new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, server.method_70562().method_68995(), server.getPermissionLevel(profile), name, Text.literal(name), server.method_70562(), null), null, null, null, profile, view);
    }

    public static PlaceholderContext of(ServerPlayerEntity player) {
        return of(player, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(ServerPlayerEntity player, ViewObject view) {
        return new PlaceholderContext(player.method_69130().method_68961(), player::getCommandSource, player.getServerWorld(), player, player, player.getGameProfile(), view);
    }

    public static PlaceholderContext of(ServerCommandSource source) {
        return of(source, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(ServerCommandSource source, ViewObject view) {
        return new PlaceholderContext(source.method_69818().method_68961(), source, source.getWorld(), source.getPlayer(), source.getEntity(), source.getPlayer() != null ? source.getPlayer().getGameProfile() : null, view);
    }

    public static PlaceholderContext of(Entity entity) {
        return of(entity, ViewObject.DEFAULT);
    }

    public static PlaceholderContext of(Entity entity, ViewObject view) {
        if (entity instanceof ServerPlayerEntity player) {
            return of(player, view);
        } else {
            var world = (ServerWorld) entity.getWorld();
            return new PlaceholderContext(entity.method_69130().method_68961(), () -> entity.getCommandSource(world), world, null, entity, null, view);
        }
    }


    public interface ViewObject {
        ViewObject DEFAULT = of(Identifier.of("placeholder_api", "default"));

        static ViewObject of(Identifier identifier) {
            return new ViewObjectImpl(identifier);
        }

        Identifier identifier();
    }
}
