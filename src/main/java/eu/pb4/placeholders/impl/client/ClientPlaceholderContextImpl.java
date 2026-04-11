package eu.pb4.placeholders.impl.client;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.client.ClientPlaceholderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ClientPlaceholderContextImpl(Minecraft minecraft, ViewObject view) implements ClientPlaceholderContext {
    public static PlaceholderContext of(ViewObject view) {
        return new ClientPlaceholderContextImpl(Minecraft.getInstance(), view);
    }

    @Override
    public ClientPlaceholderContext withView(ViewObject view) {
        return new ClientPlaceholderContextImpl(this.minecraft, view);
    }

    @Override
    public HolderLookup.@Nullable Provider holderLookup() {
        var conn = this.minecraft.getConnection();
        if (conn == null) return null;

        return this.minecraft.getConnection().registryAccess();
    }

    @Override
    public @Nullable ClientLevel level() {
        return this.minecraft.level;
    }

    @Override
    public @Nullable LocalPlayer player() {
        return this.minecraft.player;
    }

    @Override
    public @Nullable Entity entity() {
        return this.minecraft.player;
    }

    @Override
    public @Nullable NameAndId nameAndId() {
        return this.player() != null ? this.player().nameAndId() : new NameAndId(this.minecraft.getGameProfile());
    }

    @Override
    public @Nullable GameProfile gameProfile() {
        return this.player() != null ? this.player().getGameProfile() : this.minecraft.getGameProfile();
    }

    @Override
    public @Nullable BlockPos blockPosition() {
        return this.minecraft.gameRenderer.getMainCamera().blockPosition();
    }

    @Override
    public @Nullable Vec3 position() {
        return this.minecraft.gameRenderer.getMainCamera().position();
    }
}
