package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;

public class ServerPlayerEntity extends Entity {
    private final GameProfile gameProfile;
    public final ServerPlayNetworkHandler networkHandler = new ServerPlayNetworkHandler();

    public ServerPlayerEntity(String name) {
        this.gameProfile = new GameProfile(name, this.getUuid());
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Text getName() {
        return Text.literal(getGameProfile().getName());
    }

    public Text getDisplayName() {
        return getName();
    }

    public Inventory getInventory() {
        return null;
    }

    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    public StatHandler getStatHandler() {
        return StatHandler.INSTANCE;
    }

    public double getX() {
        return 5;
    }

    public double getY() {
        return 20.5;
    }

    public double getZ() {
        return 534.6;
    }

    public String getUuidAsString() {
        return this.getUuid().toString();
    }

    public float getHealth() {
        return 20;
    }

    public float getMaxHealth() {
        return 20;
    }

    public HungerManager getHungerManager() {
        return HungerManager.INSTANCE;
    }

    public Team getScoreboardTeam() {
        return null;
    }
}
