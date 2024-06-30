package net.minecraft.stat;

import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.List;

public class StatType<T> {
    public Text format(int x) {
        return Text.literal("" + x);
    }

    public <E> Registry<T> getRegistry() {
        return new Registry<>(null);
    }

    public Stat getOrCreateStat(T key) {
        return new Stat();
    }
}
