package net.minecraft.stat;

import net.minecraft.util.Identifier;

public class Stats {
    public static final Stats CUSTOM = new Stats();
    public static final Identifier PLAY_TIME = Identifier.of("play_time");


    public Stat getOrCreateStat(Object obj) {
        return new Stat();
    }
}
