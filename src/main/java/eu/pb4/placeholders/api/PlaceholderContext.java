package eu.pb4.placeholders.api;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.impl.ServerPlaceholderContextImpl;
import eu.pb4.placeholders.impl.placeholder.ViewObjectImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface PlaceholderContext {
    ParserContext.Key<PlaceholderContext> COMMON_KEY = ParserContext.Key.of("placeholder_context", PlaceholderContext.class);

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
