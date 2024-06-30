package net.minecraft.item;

import net.minecraft.component.ComponentType;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

public class ItemStack {
    public static final ItemStack EMPTY = new ItemStack();
    public static ItemStack fromNbtOrEmpty(DynamicRegistryManager empty, StringNbtReader parse) {
        return EMPTY;
    }

    public void setCount(int i) {
    }

    public boolean isEmpty() {
        return true;
    }

    public MutableText getName() {
        return Text.literal("[ITEM]");
    }

    public boolean contains(ComponentType type) {
        return false;
    }

    public Rarity getRarity() {
        return Rarity.COMMON;
    }
}
