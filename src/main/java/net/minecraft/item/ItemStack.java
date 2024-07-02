package net.minecraft.item;

import com.google.gson.JsonElement;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemStack {
    public static final ItemStack EMPTY = new ItemStack(Identifier.of("air"), 0);
    private final Identifier identifier;
    private final Map<Identifier, JsonElement> components = new HashMap<>();
    private int count = 1;


    public ItemStack(Identifier air, int i) {
        this.identifier = air;
        this.count = i;
    }
    public ItemStack(Identifier air, int i, Map<Identifier, JsonElement> components) {
        this.identifier = air;
        this.count = i;
        this.components.putAll(components);
    }

    public static ItemStack fromNbtOrEmpty(DynamicRegistryManager empty, StringNbtReader parse) {
        return EMPTY;
    }

    public boolean isEmpty() {
        return true;
    }

    public MutableText getName() {
        return Text.literal("[ITEM]");
    }

    public boolean contains(ComponentType type) {
        return components.containsKey(type.id());
    }

    public boolean contains(Identifier type) {
        return components.containsKey(type);
    }

    @Nullable
    public JsonElement get(Identifier type) {
        return this.components.get(type);
    }

    public void set(Identifier type, JsonElement element) {
        if (element == null) {
            this.components.remove(type);
        } else {
            this.components.put(type, element);
        }
    }

    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    public Identifier getItemId() {
        return this.identifier;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public Map<Identifier, JsonElement> getComponents() {
        return this.components;
    }
}
