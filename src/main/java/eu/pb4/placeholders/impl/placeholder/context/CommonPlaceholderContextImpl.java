package eu.pb4.placeholders.impl.placeholder.context;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;


public record CommonPlaceholderContextImpl(@Nullable Level level,
                                           @Nullable Player player,
                                           @Nullable Entity entity,
                                           @Nullable GameProfile gameProfile,
                                           ViewObject view
) implements PlaceholderContext {
    

    @Override
    public CommonPlaceholderContextImpl withView(ViewObject view) {
        return new CommonPlaceholderContextImpl(this.level, this.player, this.entity, this.gameProfile, view);
    }

    @Override
    public HolderLookup.@Nullable Provider holderLookup() {
        return level != null ? level.registryAccess() : null;
    }

    @Override
    public @Nullable NameAndId nameAndId() {
        return this.gameProfile != null ? new NameAndId(this.gameProfile) : null;
    }

    @Override
    public @Nullable BlockPos blockPosition() {
        return this.entity != null ? this.entity.blockPosition() : null;
    }

    @Override
    public @Nullable Vec3 position() {
        return this.entity != null ? this.entity.position() : null;
    }
}
