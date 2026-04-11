package eu.pb4.placeholders.impl.placeholder.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;


public record ServerPlaceholderContextImpl(MinecraftServer server,
                                           Supplier<CommandSourceStack> lazySource,
                                           @Nullable ServerLevel level,
                                           @Nullable ServerPlayer player,
                                           @Nullable Entity entity,
                                           @Nullable GameProfile gameProfile,
                                           PlaceholderContext.ViewObject view
) implements ServerPlaceholderContext {

    public CommandSourceStack source() {
        return this.lazySource.get();
    }


    @Override
    public ServerPlaceholderContext withView(ViewObject view) {
        return new ServerPlaceholderContextImpl(this.server, this.lazySource, this.level, this.player, this.entity, this.gameProfile, view);
    }

    @Override
    public HolderLookup.@Nullable Provider holderLookup() {
        return this.server.registryAccess();
    }

    @Override
    public @Nullable NameAndId nameAndId() {
        return this.gameProfile != null ? new NameAndId(this.gameProfile) : null;
    }

    @Override
    public @Nullable BlockPos blockPosition() {
        return BlockPos.containing(this.lazySource.get().getPosition());
    }

    @Override
    public @Nullable Vec3 position() {
        return this.lazySource.get().getPosition();
    }

    @Override
    public CommandSourceStack commandSourceStack() {
        return this.lazySource.get();
    }

    @Override
    public @Nullable ServerLevel serverLevel() {
        return this.level;
    }

    @Override
    public @Nullable ServerPlayer serverPlayer() {
        return this.player;
    }
}
