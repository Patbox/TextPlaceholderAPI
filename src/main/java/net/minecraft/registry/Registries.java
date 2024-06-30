package net.minecraft.registry;

import net.minecraft.item.Item;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;

public class Registries {
    public static final Registry<Item> ITEM = new Registry<>(new Item());
    public static final Registry<Identifier> CUSTOM_STAT = new Registry<>(Identifier.of("a"));
    public static final Registry<StatType> STAT_TYPE = new Registry<>(new StatType());
}
