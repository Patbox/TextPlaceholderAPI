package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.impl.placeholder.context.ServerPlaceholderContextImpl;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface ServerPlaceholderContext extends PlaceholderContext {
    ParserContext.Key<ServerPlaceholderContext> SERVER_KEY = ParserContext.Key.of("server_placeholder_context", ServerPlaceholderContext.class);

    static ServerPlaceholderContext of(MinecraftServer server) {
        return of(server, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(MinecraftServer server, ViewObject view) {
        return new ServerPlaceholderContextImpl(server, server::createCommandSourceStack, null, null, null, null, view);
    }

    static ServerPlaceholderContext of(ServerLevel level) {
        return of(level, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(ServerLevel level, ViewObject view) {
        return new ServerPlaceholderContextImpl(level.getServer(), level.getServer()::createCommandSourceStack, level, null, null, null, view);
    }

    static ServerPlaceholderContext of(GameProfile profile, MinecraftServer server) {
        return of(profile, server, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(GameProfile profile, MinecraftServer server, ViewObject view) {
        var name = profile.name() != null ? profile.name() : profile.id().toString();
        return new ServerPlaceholderContextImpl(server, () -> new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, server.overworld(), server.getProfilePermissions(new NameAndId(profile)), name, Component.literal(name), server, null), null, null, null, profile, view);
    }

    static ServerPlaceholderContext of(ServerPlayer player) {
        return of(player, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(ServerPlayer player, ViewObject view) {
        return new ServerPlaceholderContextImpl(player.level().getServer(), player::createCommandSourceStack, player.level(), player, player, player.getGameProfile(), view);
    }

    static ServerPlaceholderContext of(CommandSourceStack source) {
        return of(source, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(CommandSourceStack source, ViewObject view) {
        return new ServerPlaceholderContextImpl(source.getServer(), () -> source, source.getLevel(), source.getPlayer(), source.getEntity(), source.getPlayer() != null ? source.getPlayer().getGameProfile() : null, view);
    }

    static ServerPlaceholderContext of(Entity entity) {
        return of(entity, ViewObject.DEFAULT);
    }

    static ServerPlaceholderContext of(Entity entity, ViewObject view) {
        if (entity instanceof ServerPlayer player) {
            return of(player, view);
        } else {
            var world = (ServerLevel) entity.level();
            return new ServerPlaceholderContextImpl(world.getServer(), () -> entity.createCommandSourceStackForNameResolution(world), world, null, entity, null, view);
        }
    }

    default boolean hasServerPlayer() {
        return this.serverPlayer() != null;
    }

    ServerPlaceholderContext withView(ViewObject view);

    MinecraftServer server();

    CommandSourceStack commandSourceStack();

    @Nullable
    ServerLevel serverLevel();

    @Nullable
    ServerPlayer serverPlayer();

    @Override
    default ParserContext asParserContext() {
        return ParserContext.of(SERVER_KEY, this).with(COMMON_KEY, this).with(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }

    @Override
    default void addToContext(ParserContext context) {
        context.with(SERVER_KEY, this).with(COMMON_KEY, this);
        context.withIfNotSet(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }
}
