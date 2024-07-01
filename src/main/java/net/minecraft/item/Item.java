package net.minecraft.item;

import net.minecraft.util.Identifier;

public record Item(Identifier id) {
    public ItemStack getDefaultStack() {
        return id.equals(ItemStack.EMPTY.getItemId()) ? ItemStack.EMPTY : new ItemStack(id, 1);
    }
}
