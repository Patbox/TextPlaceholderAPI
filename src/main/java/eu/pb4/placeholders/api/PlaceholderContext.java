package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.impl.LoaderUtil;
import eu.pb4.placeholders.impl.client.ClientPlaceholderContextImpl;
import eu.pb4.placeholders.impl.placeholder.context.CommonPlaceholderContextImpl;
import eu.pb4.placeholders.impl.placeholder.context.ServerPlaceholderContextImpl;
import eu.pb4.placeholders.impl.placeholder.ViewObjectImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface PlaceholderContext {
    ParserContext.Key<PlaceholderContext> COMMON_KEY = ParserContext.Key.of("placeholder_context", PlaceholderContext.class);

    static PlaceholderContext of(Level level) {
        return of(level, ViewObject.DEFAULT);
    }

    static PlaceholderContext of(Level level, ViewObject view) {
        if (level instanceof ServerLevel level1) {
            return ServerPlaceholderContext.of(level1, view);
        }

        if (LoaderUtil.IS_CLIENT) {
            if (level instanceof ClientLevel) {
                return ClientPlaceholderContextImpl.of(view);
            }
        }

        return new CommonPlaceholderContextImpl(level, null, null, null, view);
    }

    static PlaceholderContext of() {
        return of(ViewObject.DEFAULT);
    }

    static PlaceholderContext of(ViewObject view) {
        return new CommonPlaceholderContextImpl(null, null, null, null, view);
    }

    static PlaceholderContext of(Entity entity) {
        return of(entity, ViewObject.DEFAULT);
    }

    static PlaceholderContext of(Entity entity, ViewObject view) {
        if (entity.level() instanceof ServerLevel) {
            return ServerPlaceholderContext.of(entity, view);
        }

        if (LoaderUtil.IS_CLIENT) {
            if (entity instanceof LocalPlayer) {
                return ClientPlaceholderContextImpl.of(view);
            }
        }

        return new CommonPlaceholderContextImpl(entity.level(), entity instanceof Player player ? player : null, entity,
                entity instanceof Player player ? player.getGameProfile() : null, view);
    }


    default boolean hasLevel() {
        return this.level() != null;
    }

    default boolean hasPlayer() {
        return this.player() != null;
    }

    default boolean hasNameAndId() {
        return this.nameAndId() != null;
    }

    default boolean hasGameProfile() {
        return this.gameProfile() != null;
    }

    default boolean hasEntity() {
        return this.entity() != null;
    }

    default boolean hasHolderLookup() {
        return this.holderLookup() != null;
    }

    default boolean hasBlockPosition() {
        return this.blockPosition() != null;
    }

    default boolean hasPosition() {
        return this.position() != null;
    }

    PlaceholderContext withView(PlaceholderContext.ViewObject view);


    default ParserContext asParserContext() {
        return ParserContext.of(COMMON_KEY, this).with(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }

    default void addToContext(ParserContext context) {
        context.with(COMMON_KEY, this);
        context.withIfNotSet(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }

    HolderLookup.@Nullable Provider holderLookup();

    @Nullable
    Level level();

    @Nullable
    Player player();
    @Nullable
    Entity entity();
    @Nullable
    NameAndId nameAndId();
    @Nullable
    GameProfile gameProfile();

    PlaceholderContext.ViewObject view();

    @Nullable
    BlockPos blockPosition();
    @Nullable
    Vec3 position();

    interface ViewObject {
        ViewObject DEFAULT = of(Identifier.fromNamespaceAndPath("placeholder_api", "default"));

        static ViewObject of(Identifier identifier) {
            return new ViewObjectImpl(identifier);
        }

        Identifier identifier();
    }
}
